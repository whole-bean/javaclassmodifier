package com.example.javaclassmodifier.service;

import com.example.javaclassmodifier.util.FileModifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileService {

  public byte[] processFiles(List<MultipartFile> files) throws IOException {
    Path tempDir = Files.createTempDirectory("modified-classes");

    for (MultipartFile file : files) {
      Path tempFile = tempDir.resolve(file.getOriginalFilename());
      Files.write(tempFile, file.getBytes());
      FileModifier.modifyFile(tempFile.toFile());
    }

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
      for (Path filePath : Files.newDirectoryStream(tempDir)) {
        try (InputStream fileInputStream = Files.newInputStream(filePath)) {
          ZipEntry zipEntry = new ZipEntry(filePath.getFileName().toString());
          zipOut.putNextEntry(zipEntry);
          byte[] bytes = new byte[1024];
          int length;
          while ((length = fileInputStream.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
          }
          zipOut.closeEntry();
        }
      }
    }

    // 임시 디렉토리 및 파일 삭제
    Files.walk(tempDir)
        .map(Path::toFile)
        .forEach(File::delete);
    Files.deleteIfExists(tempDir);

    return byteArrayOutputStream.toByteArray();
  }
}
