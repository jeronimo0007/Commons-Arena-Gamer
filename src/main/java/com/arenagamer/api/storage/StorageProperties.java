package com.arenagamer.api.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuração do armazenamento de arquivos (imagens) em bucket compatível com S3.
 *
 * Compatível com DigitalOcean Spaces, AWS S3, MinIO e qualquer storage S3-compatible.
 * Basta ajustar {@code endpoint} e {@code region} para migrar de provedor:
 *
 * <pre>
 * # DigitalOcean Spaces (ex.: região nyc3)
 * arenagamer.storage.endpoint = https://nyc3.digitaloceanspaces.com
 * arenagamer.storage.region   = nyc3
 *
 * # AWS S3 (ex.: us-east-1) — endpoint vazio usa o padrão da AWS
 * arenagamer.storage.endpoint =
 * arenagamer.storage.region   = us-east-1
 * </pre>
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "arenagamer.storage")
public class StorageProperties {

    /** Liga/desliga o storage. Se false, qualquer upload retorna erro amigável. */
    private boolean enabled = false;

    /** Identificador do provedor apenas para log/diagnóstico (ex.: "digitalocean", "aws"). */
    private String provider = "digitalocean";

    /**
     * Endpoint do serviço S3. Vazio = endpoint padrão da AWS para a região.
     * Ex. DigitalOcean: https://nyc3.digitaloceanspaces.com
     */
    private String endpoint;

    /** Região do bucket (ex.: "nyc3", "us-east-1"). */
    private String region = "us-east-1";

    /** Nome do bucket/space. */
    private String bucket;

    /** Access key (DO Spaces key / AWS access key id). */
    private String accessKey;

    /** Secret key (DO Spaces secret / AWS secret access key). */
    private String secretKey;

    /**
     * URL base pública para montar o link final do arquivo.
     * Útil quando há CDN na frente do bucket.
     * Ex.: https://cdn.arenagamer.com  ou  https://meu-space.nyc3.digitaloceanspaces.com
     * Se vazio, a URL é derivada de endpoint + bucket.
     */
    private String publicBaseUrl;

    /** Prefixo de pasta aplicado a todas as chaves (ex.: "uploads"). */
    private String keyPrefix = "uploads";

    /** ACL aplicada aos objetos enviados. "public-read" deixa a imagem acessível por URL. */
    private String acl = "public-read";

    /**
     * Path-style access (bucket no path) em vez de virtual-hosted (bucket no subdomínio).
     * AWS recomenda virtual-hosted (false). Alguns S3-compatible exigem true.
     */
    private boolean pathStyleAccess = false;

    /** Tamanho máximo de upload em bytes (default 5 MB). */
    private long maxFileSize = 5L * 1024 * 1024;
}
