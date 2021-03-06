package com.hfits.facerecognition;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hfits.facerecognition.face.mapper")
public class FaceRecognitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaceRecognitionApplication.class, args);
    }

}
