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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1/file")
public class FileServerDemo {
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
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
    public void getFileStream(@PathVariable(value = "file-name") String fileName, HttpServletResponse response) throws Exception {
        Path filePath = Path.of(this.filePath + fileName);
        boolean exists = Files.exists(filePath);
        if (!exists) throw new Exception("file not found");

        Thread thread = new Thread(() -> sendFileByteStreamToResponse(fileName, response, filePath));
        thread.start();
        thread.join();
    }

    private static void sendFileByteStreamToResponse(String fileName, HttpServletResponse response, Path filePath) {
        try (InputStream input = new FileInputStream(filePath.toFile())) {
            System.out.println("start");

            response.addHeader("Content-disposition", "attachment;filename=" + fileName);
            response.setContentType(Files.probeContentType(filePath));

            IOUtils.copy(input, response.getOutputStream());
            response.flushBuffer();

            System.out.println("done");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
