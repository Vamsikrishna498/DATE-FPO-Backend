package com.farmer.Form.Service.Impl;

import com.farmer.Form.Service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif"
    );

    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
        "application/pdf", "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "image/jpeg", "image/jpg", "image/png"
    );

    @Override
    public String storeFile(MultipartFile file, String subDirectory) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        validateFileType(file, subDirectory);
        Path uploadPath = Paths.get(uploadDir, subDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = System.currentTimeMillis() + extension;
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    @Override
    public String storeFileWithName(MultipartFile file, String subDirectory, String fileName) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        validateFileType(file, subDirectory);
        Path uploadPath = Paths.get(uploadDir, subDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    @Override
    public Resource loadFileAsResource(String fileName, String subDirectory) {
        try {
            Path filePath = Paths.get(uploadDir, subDirectory, fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found " + fileName, e);
        }
    }

    @Override
    public void deleteFile(String fileName, String subDirectory) {
        try {
            Path filePath = Paths.get(uploadDir, subDirectory, fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file " + fileName, e);
        }
    }

    @Override
    public String getFileUrl(String fileName, String subDirectory) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        return "/uploads/" + subDirectory + "/" + fileName;
    }

    private void validateFileType(MultipartFile file, String subDirectory) {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("File content type cannot be determined");
        }
        switch (subDirectory.toLowerCase()) {
            case "photos":
                if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
                    throw new IllegalArgumentException("Invalid image file type. Allowed types: JPEG, JPG, PNG, GIF");
                }
                break;
            case "documents":
            case "passbooks":
                if (!ALLOWED_DOCUMENT_TYPES.contains(contentType)) {
                    throw new IllegalArgumentException("Invalid document file type. Allowed types: PDF, DOC, DOCX, JPEG, JPG, PNG");
                }
                break;
            default:
                if (!ALLOWED_IMAGE_TYPES.contains(contentType) && !ALLOWED_DOCUMENT_TYPES.contains(contentType)) {
                    throw new IllegalArgumentException("Invalid file type for directory: " + subDirectory);
                }
        }
    }

    @Override
    public boolean fileExists(String fileName, String subDirectory) {
        try {
            Path filePath = Paths.get(uploadDir, subDirectory, fileName);
            return Files.exists(filePath);
        } catch (Exception e) {
            return false;
        }
    }
} 