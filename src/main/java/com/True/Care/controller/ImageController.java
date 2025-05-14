package com.True.Care.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Paths;

@RestController
public class ImageController {
    @Value("${app.upload.directory}")
    private String uploadDirectory;
    // private static final String UPLOAD_DIR = uploadDirectory;

    /**
     * Dynamic endpoint to serve images.
     * 
     * @param filename the name of the image file to be fetched
     * @return ResponseEntity containing the image
     */
    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        // Construct the full path to the image file
        File file = new File(Paths.get(uploadDirectory, filename).toString());
        
        // Check if the file exists
        if (file.exists()) {
            // Create a resource from the file
            Resource resource = new FileSystemResource(file);
            
            // Return the resource (image) with appropriate headers
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);
        } else {
            // If the file is not found, return a 404 response
            return ResponseEntity.notFound().build();
        }
    }
}
