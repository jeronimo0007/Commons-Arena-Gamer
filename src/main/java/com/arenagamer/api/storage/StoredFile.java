package com.arenagamer.api.storage;

/**
 * Resultado de um upload: a chave (caminho no bucket) e a URL pública.
 *
 * @param key chave do objeto no bucket (ex.: "uploads/avatars/uuid.png")
 * @param url URL pública para acessar o arquivo
 */
public record StoredFile(String key, String url) {
}
