package com.sante.senegal.controllers;

import com.sante.senegal.dto.FileUploadResponse;
import com.sante.senegal.entities.Medecin;
import com.sante.senegal.exceptions.FileStorageException;

import com.sante.senegal.services.interfaces.FileStorageService;
import com.sante.senegal.services.interfaces.MedecinService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final MedecinService inscriptionService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                                         @RequestParam("type") String type) throws FileStorageException {
        String path = fileStorageService.storeFile(file, type);
        String fileName = Paths.get(path).getFileName().toString();

        FileUploadResponse response = FileUploadResponse.builder()
                .success(true)
                .fileName(fileName)
                .filePath(path) // Stocker en BDD ce champ
                .fileUrl(fileStorageService.getPreviewUrl(path)) // Pour affichage
                .fileSize(file.getSize())
                .build();

        return ResponseEntity.ok(response);
    }
    @PostMapping("/medecin/{id}/documents")
    public ResponseEntity<FileUploadResponse> updateDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) throws FileStorageException {

        Medecin medecin = inscriptionService.updateMedecinDocument(id, file, type);

        String filePath = switch (type) {
            case "cv" -> medecin.getCvPath();
            case "diplome" -> medecin.getDiplomePath();
            case "carteOrdre" -> medecin.getCarteOrdrePath();
            default -> null;
        };

        FileUploadResponse response = FileUploadResponse.builder()
                .success(true)
                .fileName(file.getOriginalFilename())
                .filePath(filePath)
                .fileUrl(fileStorageService.getPreviewUrl(filePath) + filePath)
                .fileSize(file.getSize())
                .build();

        return ResponseEntity.ok(response);
    }
    @PostMapping("/upload-multiple")
    public ResponseEntity<?> uploadMultiple(@RequestParam("files") List<MultipartFile> files,
                                            @RequestParam("type") String type) {
        List<String> paths = fileStorageService.storeMultipleFiles(files, type);
        return ResponseEntity.ok(paths);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) throws FileStorageException {
        Resource resource = fileStorageService.loadFileAsResource(filePath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam String filePath) throws FileStorageException {
        fileStorageService.deleteFile(filePath);
        return ResponseEntity.ok(Map.of("message", "Fichier supprimé avec succès"));
    }

    @GetMapping("/preview")
    public ResponseEntity<Resource> preview(@RequestParam String filePath) throws FileStorageException {
        Resource resource = fileStorageService.loadFileAsResource(filePath);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
