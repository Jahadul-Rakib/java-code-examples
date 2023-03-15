package com.rakib.testcodeblock.file_service;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/file")
public class FileServerDemo {
    private final String filePath = "src/main/resources/files/";

    @PostMapping
    public ResponseEntity<?> saveFile(@RequestParam("file") MultipartFile file) throws Exception {
        String filename = (UUID.randomUUID() + file.getOriginalFilename()).trim().strip().replace(" ", "");

        try {
            Path pathLocation = Path.of(filePath + filename);
            file.transferTo(pathLocation);
            return ResponseEntity.ok(filename);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @GetMapping("/{file-name}")
    public ResponseEntity<?> getFile(@PathVariable(value = "file-name") String fileName) throws Exception {
        try {
            Resource resource = new UrlResource(Path.of(filePath + fileName).toUri());
            if (!(resource.exists() || resource.isFile()))
                throw new Exception("file is not exist");

            return ResponseEntity.ok().body(resource.getContentAsByteArray());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    @GetMapping(value = "/stream/{file-name}")
    public CompletableFuture<Void> getFileStream(HttpServletResponse response, @PathVariable(value = "file-name") String fileName) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path filePath = Path.of(this.filePath + fileName);
                boolean exists = Files.exists(filePath);
                if (!exists) throw new Exception("file not found by " + fileName);

                System.out.println("----start----");
                InputStream input = new FileInputStream(filePath.toFile());

                response.addHeader("Content-disposition", "attachment;filename=" + fileName);
                response.setContentType(Files.probeContentType(filePath));

                IOUtils.copyLarge(input, response.getOutputStream());
                response.flushBuffer();
                input.close();

                System.out.println("----finish----");
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }
}

