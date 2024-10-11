package com.gdsc.imageupload.domain.image.controller;

import com.gdsc.imageupload.domain.image.domain.repository.ImageRepository;
import com.gdsc.imageupload.domain.image.service.ImageUploadService;
import com.gdsc.imageupload.domain.image.service.ImageViewService;
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
    @PostMapping("")
    public void uploadImages(@RequestParam("images") List<MultipartFile> image) throws IOException {
        imageUploadService.excute(image);
    }

    @GetMapping("")
    public List<String> showImages(){
        return imageViewService.excute();
    }
}
