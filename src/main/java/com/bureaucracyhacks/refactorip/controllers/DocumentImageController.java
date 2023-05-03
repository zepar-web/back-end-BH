package com.bureaucracyhacks.refactorip.controllers;

import com.bureaucracyhacks.refactorip.services.DocumentImageService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
public class DocumentImageController {
    @Autowired
    private DocumentImageService documentImageService;

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/upload")
    public void uploadImage(@RequestParam("image")MultipartFile file) throws IOException {
        documentImageService.uploadImage(file);
    }

    @GetMapping("/download/{imageName}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable String imageName) {
        byte[] image = documentImageService.downloadImage(imageName);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(image);
    }
}