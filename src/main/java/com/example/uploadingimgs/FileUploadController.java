package com.example.uploadingimgs;

import java.io.IOException;

import com.example.uploadingimgs.storage.StorageFileNotFoundException;
import com.example.uploadingimgs.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("imgs")
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveImg(@PathVariable String filename) throws IOException {

        Resource file = storageService.loadAsResource(filename);
        MediaType mediaType = MediaType.IMAGE_JPEG;
        if (file.getFilename().toLowerCase().contains(".gif")) {
            mediaType = MediaType.IMAGE_GIF;

        } else if (file.getFilename().toLowerCase().contains(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + file.getFilename() + "\"")
                .contentType(mediaType)
                .contentLength(file.contentLength())
                .body(file);
    }

    @PostMapping
    public ResponseEntity handleFileUpload(@RequestParam("file") MultipartFile file,
                                           RedirectAttributes redirectAttributes) {

        storageService.store(file);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
