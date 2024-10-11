package com.gdsc.imageupload.domain.image.controller;

import com.gdsc.imageupload.domain.image.domain.repository.ImageRepository;
import com.gdsc.imageupload.domain.image.service.ImageUploadService;
import com.gdsc.imageupload.domain.image.service.ImageViewService;
import com.gdsc.imageupload.domain.image.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImagesController {
    private final ImageUploadService imageUploadService;
    private final ImageViewService imageViewService;
    private final S3ImageService s3ImageService;
    @PostMapping("")
    public void uploadImages(@RequestParam("images") List<MultipartFile> image) throws IOException {
        imageUploadService.excute(image);
    }

    @GetMapping("")
    public List<String> showImages(){
        return imageViewService.excute();
    }

    @PostMapping("/s3")
    public void uploadImageToS3(@RequestParam("image") MultipartFile image){
        s3ImageService.upload(image);
    }

    @GetMapping("/s3")
    public void showImagesToS3(){
        s3ImageService.getAllImagesFromS3();
    }

    @DeleteMapping("/s3")
    public void deleteImageToS3(String imageAddress){
        s3ImageService.deleteImageFromS3(imageAddress);
    }
}
