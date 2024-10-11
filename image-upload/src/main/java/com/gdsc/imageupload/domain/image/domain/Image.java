package com.gdsc.imageupload.domain.image.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="tbl_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Image {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_path")
    private String imagePath;


//    @Column(nullable = false, unique = true) // unique: 하나만 있어야함
//    private String fileName;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Builder
    public Image(String imagePath) {
        this.imagePath = imagePath;
        this.createDate = LocalDateTime.now();
    }
}
