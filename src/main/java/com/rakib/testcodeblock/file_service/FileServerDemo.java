package com.rakib.testcodeblock.file_service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/file")
public class FileServerDemo {

    private final String filePath = "src/main/resources/files/";

    @PostMapping
    public ResponseEntity<?> saveFile(@RequestParam("file") MultipartFile file) throws Exception {
        String filename = UUID.randomUUID() + file.getOriginalFilename();
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

            return ResponseEntity.ok()
                    //.body(resource.getContentAsByteArray()); //if not pass header its works fine as byte array
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
