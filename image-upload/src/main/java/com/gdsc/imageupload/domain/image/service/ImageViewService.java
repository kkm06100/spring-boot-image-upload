package com.gdsc.imageupload.domain.image.service;

import java.util.List;

import com.gdsc.imageupload.domain.image.domain.Image;
import com.gdsc.imageupload.domain.image.domain.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageViewService {

    private final ImageRepository imageRepository;
    public List<String> excute(){
        return imageRepository.findAll().stream().map(Image::getImagePath).toList();
    }
}