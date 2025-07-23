package com.farmer.Form.Service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    String storeFile(MultipartFile file, String subDirectory) throws IOException;
    String storeFileWithName(MultipartFile file, String subDirectory, String fileName) throws IOException;
    Resource loadFileAsResource(String fileName, String subDirectory);
    void deleteFile(String fileName, String subDirectory);
    String getFileUrl(String fileName, String subDirectory);
    boolean fileExists(String fileName, String subDirectory);
}