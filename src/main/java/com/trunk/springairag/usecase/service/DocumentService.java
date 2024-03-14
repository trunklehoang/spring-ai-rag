package com.trunk.springairag.usecase.service;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
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
    private final ChatClient chatClient;

    private static final String PROMPT = """ 
             Use the following pieces of information to answer the user's question.
                 If you don't know the answer, just say that you don't know, don't try to make up an answer.
                        
                 Context: {context}
                 Question: from + {ocr} + retrieve two value the date and total amount in the json format
                 Only return the helpful answer below and nothing else.
                 Helpful answer:
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
}
