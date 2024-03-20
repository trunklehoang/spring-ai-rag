package com.trunk.springairag.usecase.service;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class DocumentService {
    private final ChatClient chatClient;

    private static final String PROMPT = """ 
<|user|>
  Question:read the text and extract two value the date and total amount and response exactly the json format contain two field date and total_amount
""";
    private static final String PROMPT2 ="""
<|system|>Using the information contained in the context,
give a comprehensive answer to the question.
If the answer is contained in the context, also report the source URL.
If the answer cannot be deduced from the context, do not give an answer.</s>
Context:{ocr}
<|user|>
  Question:retrieve two value the date and total amount and response exactly the json format contain two field date and total_amount
  </s>
""";
    public Message getSystemMessage(String prompt) throws IOException {
        Resource resource = new ClassPathResource("ocr/" + prompt + ".json"); // Adjust folder path as needed
        String ocr = "";
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
        SystemPromptTemplate systemPromptTemplate =  new SystemPromptTemplate(PROMPT2);
        Prompt promptCommand = new Prompt(List.of(systemPromptTemplate.createMessage(Map.of("ocr", ocr))));
        ChatResponse response = chatClient.call(promptCommand);
        return response.getResult().getOutput();
    }

    public OllamaApi.Message getSystemMessageFromImage(MultipartFile image) throws IOException {
        OllamaApi ollamaApi =
                new OllamaApi("http://localhost:11434");
        var request = OllamaApi.ChatRequest.builder("llava")
                .withStream(false) // not streaming
                .withMessages(List.of(
                        OllamaApi.Message.builder(OllamaApi.Message.Role.USER)
                                .withContent(PROMPT)
                                .withImages(List.of(image.getBytes()))
                                .build())).withFormat("json").build();

        OllamaApi.ChatResponse response = ollamaApi.chat(request);
        return response.message();
    }
}
