package com.trunk.springairag.usecase.service;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.ChatClient;
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

    private static final String PROMPT = """ 
You are an AI visual assistant, and you are seeing a single receipt document. What you see are provided with five sentences, describing the same image you are looking at. Answer all questions as you are seeing the receipt.

Design a conversation between you and a person asking about this receipt document. The answers should be in a tone that a visual AI assistant is seeing the receipt document and answering the question.
Ask diverse questions and give corresponding answers.

Include questions asking about the visual content of the document, including the object types, counting the objects, object actions, object locations, relative positions between objects, etc. Only include questions that have definite answers:
(1) one can see the content in the receipt document that the question asks about and can answer confidently;
(2) one can determine confidently from the receipt information that it is not in the receipt document.
Do not ask any question that cannot be answered confidently.
Document:{document}
            """;
    private static final String USER_PROMPT = """
read the text and extract two value the date and total amount and response exactly the json format contain two field date and total_amount
""";

    public OllamaApi.Message getSystemMessage(String prompt) throws IOException {
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
        SystemPromptTemplate systemPromptTemplate =  new SystemPromptTemplate(PROMPT);
        Prompt promptCommand = new Prompt(List.of(systemPromptTemplate.createMessage(Map.of("document", prompt))));
        OllamaApi ollamaApi =
                new OllamaApi("http://localhost:11434");
        var request = OllamaApi.ChatRequest.builder("zephyr")
                .withStream(false) // not streaming
                .withMessages(List.of(
                        OllamaApi.Message.builder(OllamaApi.Message.Role.SYSTEM)
                                .withContent(promptCommand.getContents())
                                .build(),
                        OllamaApi.Message.builder(OllamaApi.Message.Role.USER)
                                .withContent(USER_PROMPT)
                                .build())).withFormat("json").build();

        OllamaApi.ChatResponse response = ollamaApi.chat(request);
        return response.message();
    }
}
