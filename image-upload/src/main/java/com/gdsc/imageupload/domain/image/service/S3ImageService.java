package com.gdsc.imageupload.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

import com.gdsc.imageupload.global.exception.ErrorCode;
import com.gdsc.imageupload.global.exception.custom.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class S3ImageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String upload(MultipartFile image) {
        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){ // 이미지 비어있으면 예외처리
            throw new S3Exception(ErrorCode.EMPTY_FILE_EXCEPTION);
        }
        return this.uploadImage(image);
    }

    private String uploadImage(MultipartFile image) { // 이미지 업로드
        this.validateImageFileExtention(image.getOriginalFilename());
        try {
            return this.uploadImageToS3(image);
        } catch (IOException e) {
            throw new S3Exception(ErrorCode.IO_EXCEPTION_ON_IMAGE_UPLOAD);
        }
    }

    private String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename(); // 원본 파일명 가져오기
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new S3Exception(ErrorCode.EMPTY_FILE_EXCEPTION); // 파일명이 없을 경우 예외 처리
        }

        // 확장자 추출
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "_" + originalFilename; // 변경된 파일명

        // 이미지 파일의 InputStream과 바이트 배열 생성
        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentType("image/" + extension); // 확장자 기반으로 ContentType 설정

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            log.info("Uploading file '{}' with extension '{}'", originalFilename, extension); // 디버깅 정보 출력
            log.info("Bucket: {}, FileName: {}", bucketName, s3FileName); // 버킷과 파일명 정보

            // S3에 업로드 요청 생성
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead); // S3 객체를 Public Read로 설정

            amazonS3.putObject(putObjectRequest); // S3에 객체 업로드
            log.info("Upload successful, URL: {}", amazonS3.getUrl(bucketName, s3FileName).toString());
        } catch (AmazonS3Exception e) {
            log.error("AmazonS3Exception: {}", e.getErrorMessage()); // S3 관련 오류 메시지 출력
            throw new S3Exception(ErrorCode.PUT_OBJECT_EXCEPTION);
        } catch (Exception e) {
            log.error("Exception occurred: ", e); // 다른 예외 출력
            throw new S3Exception(ErrorCode.PUT_OBJECT_EXCEPTION); // 예외 발생 시 처리
        } finally {
            byteArrayInputStream.close(); // 메모리 절약을 위해 스트림 닫기
            is.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString(); // 업로드된 파일의 S3 URL 반환
    }
    public void deleteImageFromS3(String imageAddress){
        String key = getKeyFromImageAddress(imageAddress);
        try{
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        }catch (Exception e){
            throw new S3Exception(ErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }
    private void validateImageFileExtention(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new S3Exception(ErrorCode.NO_FILE_EXTENTION);
        }

        String extention = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extention)) {
            throw new S3Exception(ErrorCode.INVALID_FILE_EXTENTION);
        }
    }



    private String getKeyFromImageAddress(String imageAddress){
        try{
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        }catch (MalformedURLException | UnsupportedEncodingException e){
            throw new S3Exception(ErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    public List<String> getAllImagesFromS3() {
        List<String> imageUrls = new ArrayList<>();

        try {
            // S3 버킷에서 객체 목록 가져오기
            ObjectListing objectListing = amazonS3.listObjects(bucketName);

            // 각 객체에 대해 URL을 생성하고 목록에 추가
            for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
                // 파일 이름을 가져옴
                String fileName = os.getKey();

                // 이미지인지 확인 (jpg, png, gif 확장자만 허용)
                this.validateImageFileExtention(fileName);
                String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();
                imageUrls.add(fileUrl); // 이미지 URL을 리스트에 추가

            }

        } catch (Exception e) {
            // 예외 처리
            throw new S3Exception(ErrorCode.EXCEPTION_ON_LIST_IMAGES);
        }

        return imageUrls;
    }
}
