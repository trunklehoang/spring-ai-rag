package com.trunk.springairag.usecase.service;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class DocumentService {
    private static final String TEXT = """
              Task: extract the date and total amount from the text.
              Instructions:
              1. summary the date and total amount from the extracted text in the Context and return JSON object with two field : date & total_amount
              2. If not recognized, return an empty JSON object.
            Context:{ocr}
              """;
    public String getSystemMessage(String prompt) throws IOException {
        Resource resource = new ClassPathResource("ocr/" + prompt + ".json"); // Adjust folder path as needed
        String ocr;
        // Check if the resource exists
        if (resource.exists()) {
            try (InputStream inputStream = resource.getInputStream()) {
                // Read the file content into a byte array
                byte[] fileBytes = FileCopyUtils.copyToByteArray(inputStream);
                // Convert byte array to String using UTF-8 encoding
                ocr = new String(fileBytes, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new IOException("have some error: " + e.getMessage(), e);
            }
        } else {
            throw new IOException("File not found: " + prompt);
        }


        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(TEXT);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("ocr", ocr));
        OllamaApi ollamaApi =
                new OllamaApi("http://localhost:11434");
        var request = OllamaApi.ChatRequest.builder("zephyr")
                .withStream(false) // not streaming
                .withMessages(List.of(
                        OllamaApi.Message.builder(OllamaApi.Message.Role.USER)
                                .withContent(systemMessage.getContent())
                                .build())).withFormat("json").build();

        OllamaApi.ChatResponse response = ollamaApi.chat(request);
        return response.message().content();
    }
}
