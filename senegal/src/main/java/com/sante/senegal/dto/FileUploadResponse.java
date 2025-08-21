package com.sante.senegal.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponse {
    private boolean success;
    private String fileName;
    private String filePath; // à stocker dans l'entité Medecin
    private String fileUrl;  // à utiliser côté front pour prévisualiser
    private long fileSize;
    private String message;
}
