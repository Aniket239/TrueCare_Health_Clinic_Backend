package com.True.Care.helpers;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
public class FileUploadHelper {
    private static final String UPLOAD_DIR = "uploads/";  // You can change the upload directory

    /**
     * This method handles the file upload and returns the saved file path or URL.
     * 
     * @param file The file to be uploaded.
     * @param uploadBaseUrl The base URL for the uploaded files.
     * @return The URL of the uploaded file.
     * @throws IOException If an I/O error occurs during file upload.
     */
    public static String uploadFile(MultipartFile file, String uploadBaseUrl) throws IOException {
        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            // Save the file to the server
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return the file URL or path (base URL + file name)
            return uploadBaseUrl + fileName;
        }
        return null;
    }
}
