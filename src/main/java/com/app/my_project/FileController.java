package com.app.my_project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// This class is a REST controller for handling file upload and removal operations
@RestController
public class FileController {

    // Endpoint to upload a file
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Convert the uploaded file to a byte array
            byte[] bytes = file.getBytes();
            // Get the original file name
            String fileName = file.getOriginalFilename();
            // Define the upload directory
            String uploadDir = "my-project/src/main/resources/static/uploads/";
            // Define the target location for the file
            String targetLocation = uploadDir + fileName;
            // Create a Path object for the upload directory
            Path uploadPath = Paths.get(uploadDir);

            // Check if the upload directory exists, if not, create it
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Create a Path object for the target file
            Path path = Paths.get(targetLocation);

            // Write the file bytes to the target location
            Files.write(path, bytes);

            return "File uploaded successfully";
        } catch (IOException e) {
            // Handle any IO exceptions during file upload
            return "Failed to upload file: " + e.getMessage();
        }
    }

    // Endpoint to remove a file by its name
    @DeleteMapping("remove/{fileName}")
    public String removeFile(@PathVariable String fileName) {
        // Define the upload directory
        String uploadDir = "my-project/src/main/resources/static/uploads/";
        // Create a Path object for the file to be removed
        Path uploadPath = Paths.get(uploadDir + fileName);

        // Check if the file exists
        if (Files.exists(uploadPath)) {
            try {
                // Delete the file
                Files.delete(uploadPath);

                return "File removed successfully";
            } catch (IOException e) {
                // Handle any IO exceptions during file removal
                return "Failed to remove file: " + e.getMessage();
            }
        }
        // Return a message if the file is not found
        return "File not found";
    }
}