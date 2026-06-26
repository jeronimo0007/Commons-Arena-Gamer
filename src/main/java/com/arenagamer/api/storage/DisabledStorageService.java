package com.arenagamer.api.storage;

import com.arenagamer.api.exception.BusinessException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementação usada quando o storage está desabilitado
 * ({@code arenagamer.storage.enabled=false} ou ausente). Mantém a aplicação
 * funcional, mas qualquer tentativa de upload retorna um erro claro.
 */
@Service
@ConditionalOnProperty(prefix = "arenagamer.storage", name = "enabled", havingValue = "false", matchIfMissing = true)
public class DisabledStorageService implements StorageService {

    private static final String MESSAGE =
            "Armazenamento de imagens não configurado. Defina arenagamer.storage.* para habilitar.";

    @Override
    public StoredFile upload(MultipartFile file, String folder) {
        throw new BusinessException(MESSAGE, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public void delete(String key) {
        // no-op
    }

    @Override
    public String publicUrl(String key) {
        return key;
    }
}
