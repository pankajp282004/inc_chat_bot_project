package com.inc.chatbot.service;

import com.inc.chatbot.entity.Incident;
import com.inc.chatbot.entity.IncidentAttachment;
import com.inc.chatbot.repository.AttachmentRepository;
import com.inc.chatbot.repository.IncidentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final AttachmentRepository attachmentRepository;
    private final IncidentRepository incidentRepository;

    @Value("${app.file-storage.path:./uploads}")
    private String fileStoragePath;

    public FileStorageService(AttachmentRepository attachmentRepository,
                              IncidentRepository incidentRepository) {
        this.attachmentRepository = attachmentRepository;
        this.incidentRepository = incidentRepository;
    }

    @Transactional
    public IncidentAttachment storeFile(Long incidentId, MultipartFile file, String uploadedBy) throws IOException {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new NoSuchElementException("Incident not found: " + incidentId));

        String originalFileName = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown");
        String extension = getExtension(originalFileName);
        String uniqueFileName = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);

        Path uploadDir = Paths.get(fileStoragePath, "incidents", String.valueOf(incidentId));
        Files.createDirectories(uploadDir);

        Path targetPath = uploadDir.resolve(uniqueFileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        String relativeFilePath = "incidents/" + incidentId + "/" + uniqueFileName;

        IncidentAttachment attachment = IncidentAttachment.builder()
                .incident(incident)
                .fileName(originalFileName)
                .filePath(relativeFilePath)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedBy(uploadedBy)
                .build();

        log.info("Stored file {} for incident {}", originalFileName, incidentId);
        return attachmentRepository.save(attachment);
    }

    public Path getFilePath(String relativePath) {
        return Paths.get(fileStoragePath).resolve(relativePath).toAbsolutePath();
    }

    private String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot >= 0 ? fileName.substring(lastDot + 1) : "";
    }

    public void ensureUploadDirectoryExists() throws IOException {
        Path path = Paths.get(fileStoragePath);
        Files.createDirectories(path);
    }
}
