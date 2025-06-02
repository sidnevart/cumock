package com.example.cumock.util;

import com.example.cumock.model.ProblemExample;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class JsonConverter implements AttributeConverter<List<ProblemExample>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ProblemExample> attribute) {
        if (attribute == null) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Ошибка сериализации в JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ProblemExample> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(
                dbData,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ProblemExample.class)
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка десериализации из JSON: " + e.getMessage(), e);
        }
    }
}
