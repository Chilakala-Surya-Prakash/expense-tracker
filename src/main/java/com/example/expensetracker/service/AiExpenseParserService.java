package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseDto.ExpenseRequest;
import com.example.expensetracker.entity.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiExpenseParserService {

    @Value("${spring.ai.google.api-key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    //Adding the Ai parser method.

public ExpenseRequest parseExpenseFromText(String rawText) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;

        String prompt = "Extract expense details from the following text and return ONLY a valid JSON object with these exact keys: " +
                "\"title\" (String), \"amount\" (number), \"category\" (one of FOOD, TRANSPORT, UTILITIES, ENTERTAINMENT, SHOPPING, HEALTH, OTHER), " +
                "\"merchant\" (String or null), \"expenseDate\" (YYYY-MM-DD or today if not specified), \"description\" (String or null). " +
                "Do not include any markdown formatting like ```json, just return the raw JSON string. " +
                "Text: \"" + rawText + "\"";

        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);
            
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String aiJsonText = (String) parts.get(0).get("text");

            aiJsonText = aiJsonText.replace("```json", "").replace("```", "").trim();

            return objectMapper.readValue(aiJsonText, ExpenseRequest.class);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to parse expense using  AI: " + ex.getMessage(), ex);
        }
    }


    }
