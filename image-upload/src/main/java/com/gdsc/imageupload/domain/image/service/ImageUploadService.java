package com.gdsc.imageupload.domain.image.service;

import com.gdsc.imageupload.domain.image.domain.Image;
import com.gdsc.imageupload.domain.image.domain.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ImageUploadService {
    private final ImageRepository imageRepository;
    private static final String uploadsDir = "src/main/resources/static/uploads/images";

    public void excute(List<MultipartFile> images) throws IOException {
        for(MultipartFile image : images){
            // 이미지 파일 경로를 저장
            String dbFilePath = saveImage(image, uploadsDir);

            // ProductThumbnail 엔티티 생성 및 저장
            Image save = new Image(dbFilePath);
            imageRepository.save(save);
        }
    }

    private String saveImage(MultipartFile image, String uploadsDir) throws IOException {
        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + image.getOriginalFilename();
        // 실제 파일이 저장될 경로
        String filePath = uploadsDir + fileName;
        // DB에 저장할 경로 문자열
        String dbFilePath = "/uploads/thumbnails/" + fileName;

        // 파일로 변환 후 저장
        Path path = Paths.get(filePath); // Path 객체 생성
        Files.createDirectories(path.getParent()); // 디렉토리 생성
        Files.write(path, image.getBytes()); // 디렉토리에 파일 저장

        return dbFilePath;
    }
}
