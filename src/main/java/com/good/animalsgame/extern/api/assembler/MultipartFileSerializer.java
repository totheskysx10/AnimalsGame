package com.good.animalsgame.extern.api.assembler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

/**
 * Кастомный сериализатор MultipartFile
 */
public class MultipartFileSerializer extends JsonSerializer<MultipartFile> {

    @Override
    public void serialize(MultipartFile value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            byte[] bytes = value.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            gen.writeString(base64);
        }
    }
}

