package com.sante.senegal.services.implementations;

import com.sante.senegal.config.FileUploadConfig;
import com.sante.senegal.exceptions.FileStorageException;
import com.sante.senegal.services.interfaces.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileUploadConfig fileUploadConfig;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "jpg", "jpeg", "png", "doc", "docx");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        try {
            this.fileStorageLocation = Paths.get(fileUploadConfig.getUploadDir()).toAbsolutePath().normalize();
            Files.createDirectories(this.fileStorageLocation);
            log.info("Répertoire de stockage initialisé : {}", this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Impossible d'initialiser le répertoire de stockage", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String type) throws FileStorageException {
        validateFile(file);
        String fileName = generateSecureFileName(file.getOriginalFilename());
        Path targetLocation = getSecureTargetLocation(type, fileName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Fichier enregistré avec succès : {}", targetLocation);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new FileStorageException("Échec de l'enregistrement du fichier", ex);
        }
    }

    @Override
    public List<String> storeMultipleFiles(List<MultipartFile> files, String type) {
        return files.stream()
                .map(file -> {
                    try {
                        return storeFile(file, type);
                    } catch (FileStorageException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public Resource loadFileAsResource(String filePath) throws FileStorageException {
        try {
            Path file = validateFilePath(filePath);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException("Fichier introuvable ou inaccessible");
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("URL de fichier invalide", ex);
        }
    }

    @Override
    public void deleteFile(String filePath) throws FileStorageException {
        try {
            Path file = validateFilePath(filePath);
            Files.deleteIfExists(file);
            log.info("Fichier supprimé : {}", filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Erreur lors de la suppression du fichier", ex);
        }
    }

    @Override
    public String getPreviewUrl(String filePath) {
        return "/api/v1/files/preview?filePath=" + filePath;
    }

    // Méthodes privées utilitaires
    private void validateFile(MultipartFile file) throws FileStorageException {
        if (file.isEmpty()) {
            throw new FileStorageException("Le fichier est vide");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileStorageException("La taille du fichier dépasse la limite autorisée");
        }
    }

    private String generateSecureFileName(String originalFilename) throws FileStorageException {
        String cleanedFilename = StringUtils.cleanPath(Objects.requireNonNull(originalFilename));
        String fileExtension = getFileExtension(cleanedFilename).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new FileStorageException("Type de fichier non autorisé");
        }

        return UUID.randomUUID() + "." + fileExtension;
    }

    private String getFileExtension(String filename) throws FileStorageException {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            throw new FileStorageException("Le fichier doit avoir une extension valide");
        }
        return filename.substring(lastDotIndex + 1);
    }

    private Path getSecureTargetLocation(String type, String fileName) throws FileStorageException {
        try {
            Path typeDirectory = this.fileStorageLocation.resolve(type).normalize();
            Files.createDirectories(typeDirectory);

            Path targetLocation = typeDirectory.resolve(fileName).normalize();
            validatePathSecurity(targetLocation);

            return targetLocation;
        } catch (IOException ex) {
            throw new FileStorageException("Impossible de créer le répertoire", ex);
        }
    }

    private Path validateFilePath(String filePath) throws FileStorageException {
        try {
            Path file = Paths.get(filePath).normalize();
            validatePathSecurity(file);
            return file;
        } catch (InvalidPathException ex) {
            throw new FileStorageException("Chemin de fichier invalide", ex);
        }
    }

    private void validatePathSecurity(Path path) throws FileStorageException {
        if (!path.startsWith(this.fileStorageLocation)) {
            throw new FileStorageException("Accès non autorisé au fichier");
        }
    }
}