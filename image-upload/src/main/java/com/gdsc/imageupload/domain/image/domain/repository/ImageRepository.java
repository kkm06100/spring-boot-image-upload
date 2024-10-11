package com.gdsc.imageupload.domain.image.domain.repository;

import com.gdsc.imageupload.domain.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
