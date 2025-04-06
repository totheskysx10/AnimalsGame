package com.good.animalsgame.extern.api.assembler;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Кастомный файл
 */
@AllArgsConstructor
public class CustomMultipartFile implements MultipartFile {

    private final byte[] fileBytes;
    private final String fileName;
    private final String contentType;

    @Override
    @NonNull
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return fileBytes == null || fileBytes.length == 0;
    }

    @Override
    public long getSize() {
        return fileBytes.length;
    }

    @Override
    public byte @NonNull [] getBytes() throws IOException {
        return fileBytes;
    }

    @Override
    @NonNull
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(fileBytes);
    }

    @Override
    public void transferTo(@NonNull java.io.File dest) throws IllegalStateException {
    }
}
