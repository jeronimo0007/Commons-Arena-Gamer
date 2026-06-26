package com.arenagamer.api.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstração de armazenamento de arquivos. A implementação padrão grava em um
 * bucket compatível com S3 (DigitalOcean Spaces, AWS S3, etc.), mas a interface
 * permite trocar o provedor sem afetar os controllers/serviços que a consomem.
 */
public interface StorageService {

    /**
     * Envia um arquivo para o storage.
     *
     * @param file   arquivo recebido (multipart)
     * @param folder subpasta lógica dentro do bucket (ex.: "avatars", "presets", "teams")
     * @return chave e URL pública do arquivo armazenado
     */
    StoredFile upload(MultipartFile file, String folder);

    /**
     * Remove um arquivo do storage pela sua chave.
     *
     * @param key chave do objeto (como retornado em {@link StoredFile#key()})
     */
    void delete(String key);

    /**
     * Monta a URL pública de uma chave existente.
     */
    String publicUrl(String key);
}
