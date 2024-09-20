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
        Task: Extract the date and total amount from the OCR text and categorize the expense.
        Instructions:
        1. Summarize the extracted date and total amount from the provided OCR text in the Context.
            Analyze the extracted text for keywords or phrases related to the following categories:
            a. Travelling: Look for keywords like "taxi," "flight," "car rental," "bus," "airport."
            b. Lodging and Living: Look for keywords like "hotel," "accommodation," "stay," "rental."
            c. Telecom: Look for keywords like "phone," "internet," "mobile," "data," "SIM."
            d. IT Equipment: Look for keywords like "laptop," "tablet," "hardware," "keyboard."
            e. Train Ticket: Look for keywords like "train," "rail," "ticket," "station.","Terminal-ID"
            f. Plane: Look for "flight," "boarding pass," "airfare," "airport."
            g. Meals: Look for keywords like "food," "restaurant," "dining," "meal," "breakfast," "lunch," "dinner."
            h. Team Events: Look for "team," "event," "outing," "celebration."
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
