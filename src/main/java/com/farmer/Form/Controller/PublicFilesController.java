package com.farmer.Form.Controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/public")
public class PublicFilesController {

    @GetMapping("/uploads/company-logos/{companyId}/{filename:.+}")
    public ResponseEntity<?> getCompanyLogo(
            @PathVariable Long companyId,
            @PathVariable String filename
    ) {
        try {
            Path file = Paths.get(System.getProperty("user.dir"))
                    .resolve("uploads")
                    .resolve("company-logos")
                    .resolve(String.valueOf(companyId))
                    .resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            MediaType mediaType = detectMediaType(filename);
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noCache())
                    .contentType(mediaType)
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().body("Invalid file path");
        }
    }

    private MediaType detectMediaType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF;
        if (lower.endsWith(".svg")) return MediaType.valueOf("image/svg+xml");
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}


