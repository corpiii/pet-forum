package com.corpi.mong_nyang.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStoreService {
    private String uploadDir = "/Users/ijeongmin/uploads";

    public String saveImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath);

        return filePath.toUri().getPath();
    }

    public void removeImage(String path) throws IOException {
        Files.delete(Path.of(path));
    }

    public boolean isExist(String path) {
        return Files.exists(Path.of(path));
    }
}
