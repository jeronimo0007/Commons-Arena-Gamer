package com.arenagamer.api.controller.common;

import com.arenagamer.api.dto.response.ApiResponse;
import com.arenagamer.api.dto.response.ApiResponses;
import com.arenagamer.api.storage.StorageService;
import com.arenagamer.api.storage.StoredFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/common/uploads")
@RequiredArgsConstructor
@Tag(name = "Common / Uploads", description = "Envio de imagens (logos/banners de time, etc.) para o bucket S3 — JWT")
@SecurityRequirement(name = "Bearer")
public class CommonUploadController {

    private final StorageService storageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Enviar imagem",
            description = "Faz upload de uma imagem para o bucket e retorna a URL pública, que pode ser usada nos campos de imagem (ex.: logo/banner de time).")
    public ResponseEntity<ApiResponse<StoredFile>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "folder", defaultValue = "teams") String folder) {
        return ApiResponses.created(storageService.upload(file, folder));
    }
}
