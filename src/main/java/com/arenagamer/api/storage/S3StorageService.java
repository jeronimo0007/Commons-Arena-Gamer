package com.arenagamer.api.storage;

import com.arenagamer.api.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * Implementação de {@link StorageService} para buckets compatíveis com S3
 * (DigitalOcean Spaces, AWS S3, MinIO...). O bean só é criado quando o storage
 * está habilitado ({@code arenagamer.storage.enabled=true}).
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "arenagamer.storage", name = "enabled", havingValue = "true")
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final StorageProperties properties;

    public S3StorageService(S3Client s3Client, StorageProperties properties) {
        this.s3Client = s3Client;
        this.properties = properties;
    }

    @Override
    public StoredFile upload(MultipartFile file, String folder) {
        validate(file);

        String key = buildKey(folder, file.getOriginalFilename());
        try {
            PutObjectRequest.Builder request = PutObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize());

            if (StringUtils.hasText(properties.getAcl())) {
                request.acl(properties.getAcl());
            }

            s3Client.putObject(request.build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw BusinessException.badRequest("Falha ao ler o arquivo enviado");
        } catch (RuntimeException e) {
            log.error("Falha ao enviar arquivo para o storage S3 (bucket={}, key={})",
                    properties.getBucket(), key, e);
            throw new BusinessException("Falha ao armazenar o arquivo", org.springframework.http.HttpStatus.BAD_GATEWAY);
        }

        return new StoredFile(key, publicUrl(key));
    }

    @Override
    public void delete(String key) {
        if (!StringUtils.hasText(key)) {
            return;
        }
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(normalizeKey(key))
                    .build());
        } catch (RuntimeException e) {
            log.warn("Falha ao remover arquivo do storage (key={})", key, e);
        }
    }

    @Override
    public String publicUrl(String key) {
        String normalized = normalizeKey(key);
        String base = StringUtils.hasText(properties.getPublicBaseUrl())
                ? trimTrailingSlash(properties.getPublicBaseUrl())
                : defaultBaseUrl();
        return base + "/" + normalized;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.badRequest("Arquivo não enviado ou vazio");
        }
        if (file.getSize() > properties.getMaxFileSize()) {
            throw BusinessException.badRequest(
                    "Arquivo excede o tamanho máximo de " + (properties.getMaxFileSize() / (1024 * 1024)) + " MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw BusinessException.badRequest("Apenas imagens são permitidas");
        }
    }

    private String buildKey(String folder, String originalFilename) {
        String extension = "";
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
        }
        StringBuilder key = new StringBuilder();
        if (StringUtils.hasText(properties.getKeyPrefix())) {
            key.append(trimSlashes(properties.getKeyPrefix())).append('/');
        }
        if (StringUtils.hasText(folder)) {
            key.append(trimSlashes(folder)).append('/');
        }
        key.append(UUID.randomUUID()).append(extension);
        return key.toString();
    }

    private String defaultBaseUrl() {
        String endpoint = trimTrailingSlash(properties.getEndpoint());
        if (!StringUtils.hasText(endpoint)) {
            endpoint = "https://s3." + properties.getRegion() + ".amazonaws.com";
        }
        if (properties.isPathStyleAccess()) {
            return endpoint + "/" + properties.getBucket();
        }
        // virtual-hosted: bucket vira subdomínio do endpoint
        String withoutScheme = endpoint.replaceFirst("^https?://", "");
        String scheme = endpoint.startsWith("http://") ? "http://" : "https://";
        return scheme + properties.getBucket() + "." + withoutScheme;
    }

    private String normalizeKey(String key) {
        return key.startsWith("/") ? key.substring(1) : key;
    }

    private String trimSlashes(String value) {
        return value.replaceAll("^/+", "").replaceAll("/+$", "");
    }

    private String trimTrailingSlash(String value) {
        return value == null ? "" : value.replaceAll("/+$", "");
    }
}
