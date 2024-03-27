package com.trunk.springairag.usecase.service;

import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Base64;
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
You are an AI visual assistant, and you are seeing a single image. What you see are provided with five sentences, describing the same image you are looking at. Answer all questions as you are seeing the image.

Design a conversation between you and a person asking about this photo. The answers should be in a tone that a visual AI assistant is seeing the image and answering the question.
Ask diverse questions and give corresponding answers.

Include questions asking about the visual content of the image, including the object types, counting the objects, object actions, object locations, relative positions between objects, etc. Only include questions that have definite answers:
(1) one can see the content in the image that the question asks about and can answer confidently;
(2) one can determine confidently from the image that it is not in the image.
Do not ask any question that cannot be answered confidently.

Also include complex questions that are relevant to the content in the image, for example, asking about background knowledge of the objects in the image, asking to discuss about events happening in the image, etc. Again, do not ask about uncertain details.
Provide detailed answers when answering complex questions. For example, give detailed examples or reasoning steps to make the content more convincing and well-organized.  You can include multiple paragraphs if necessary.
            """;
    private static final String USER_PROMPT = """
read the text and extract two value the date and total amount and response exactly the json format contain two field date and total_amount.
Return empty json if not recognize that value
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

    public String getSystemMessageFromImage(MultipartFile image) throws IOException {
        OllamaApi ollamaApi =
                new OllamaApi("http://localhost:11434");
        var request = OllamaApi.ChatRequest.builder("llava")
                .withStream(false) // not streaming
                .withMessages(List.of(
                        OllamaApi.Message.builder(OllamaApi.Message.Role.SYSTEM)
                                .withContent(PROMPT)
//                                .withImages(List.of(Base64.encodeBase64String(image.getBytes())))
                                .build(),
                        OllamaApi.Message.builder(OllamaApi.Message.Role.USER)
                                .withContent(USER_PROMPT)
                                .withImages(List.of(Base64.encodeBase64String(image.getBytes())))
                                .build())).withFormat("json").build();

        OllamaApi.ChatResponse response = ollamaApi.chat(request);
        return response.message().content();
    }
}
