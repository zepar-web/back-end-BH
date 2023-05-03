package com.bureaucracyhacks.refactorip.services;

import java.io.IOException;
import java.util.Optional;

import com.bureaucracyhacks.refactorip.models.DocumentImageJPA;
import com.bureaucracyhacks.refactorip.repositories.ImageRepository;
import com.bureaucracyhacks.refactorip.utils.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentImageService {
    @Autowired
    private ImageRepository imageRepository;

    public DocumentImageJPA uploadImage(MultipartFile file) throws IOException {
        DocumentImageJPA documentImage = new DocumentImageJPA();
        documentImage.setName(file.getOriginalFilename());
        documentImage.setType(file.getContentType());
        documentImage.setImage(ImageUtil.compressImage(file.getBytes()));
        return imageRepository.save(documentImage);
    }

    public byte[] downloadImage(String imageName) {
        Optional<DocumentImageJPA> image = imageRepository.findByName(imageName);
        return ImageUtil.decompressImage(image.get().getImage());
    }
}