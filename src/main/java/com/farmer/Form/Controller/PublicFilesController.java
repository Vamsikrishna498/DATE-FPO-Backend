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
import java.util.Map;

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
            System.out.println("🔍 Logo request: companyId=" + companyId + ", filename=" + filename);
            System.out.println("🔍 Resolved file path: " + file.toString());
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists()) {
                System.out.println("❌ Logo file not found: " + file.toString());
                return ResponseEntity.notFound().build();
            }
            MediaType mediaType = detectMediaType(filename);
            System.out.println("✅ Logo file found: " + file.toString() + ", mediaType: " + mediaType);
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noCache())
                    .contentType(mediaType)
                    .body(resource);
        } catch (MalformedURLException e) {
            System.out.println("❌ Invalid file path: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Invalid file path");
        }
    }

    // Test endpoint to list available logos
    @GetMapping("/test/logos/{companyId}")
    public ResponseEntity<?> testLogos(@PathVariable Long companyId) {
        try {
            Path companyDir = Paths.get(System.getProperty("user.dir"))
                    .resolve("uploads")
                    .resolve("company-logos")
                    .resolve(String.valueOf(companyId));
            
            if (!companyDir.toFile().exists()) {
                return ResponseEntity.ok().body(Map.of("error", "Company directory not found", "path", companyDir.toString()));
            }
            
            java.util.List<String> files = java.util.Arrays.stream(companyDir.toFile().listFiles())
                    .map(f -> f.getName())
                    .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok().body(Map.of(
                "companyId", companyId,
                "directory", companyDir.toString(),
                "files", files,
                "logoUrls", files.stream().map(f -> "/api/public/uploads/company-logos/" + companyId + "/" + f).collect(java.util.stream.Collectors.toList())
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
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


