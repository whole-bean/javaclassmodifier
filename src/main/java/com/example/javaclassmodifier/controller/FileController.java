package com.example.javaclassmodifier.controller;

import com.example.javaclassmodifier.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
public class FileController {

  @Autowired
  private FileService fileService;

  @PostMapping("/upload")
  public ResponseEntity<InputStreamResource> handleFileUpload(@RequestParam("files") List<MultipartFile> files) throws IOException {
    byte[] zipData = fileService.processFiles(files);
    InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(zipData));

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=modified_files.zip")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }
}
