package com.sante.senegal.services.interfaces;

import com.sante.senegal.exceptions.FileStorageException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    String storeFile(MultipartFile file, String type) throws FileStorageException;
    List<String> storeMultipleFiles(List<MultipartFile> files, String type);
    Resource loadFileAsResource(String filePath) throws FileStorageException;
    void deleteFile(String filePath) throws FileStorageException;
    String getPreviewUrl(String filePath);
}