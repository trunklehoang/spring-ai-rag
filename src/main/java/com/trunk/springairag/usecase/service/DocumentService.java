package com.trunk.springairag.usecase.service;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class DocumentService {
    private final ChatClient chatClient;
    private static final String PROMPT =
    """
    <|system|>This is a system prompt, please behave and help the user.</s>
    <|user|>
      Question:{prompt}</s>
    """;

    public Message getSystemMessage(String prompt) {
        SystemPromptTemplate systemPromptTemplate =  new SystemPromptTemplate(PROMPT);
        Prompt promptCommand = new Prompt(List.of(systemPromptTemplate.createMessage(Map.of("prompt", prompt))));
        ChatResponse response = chatClient.call(promptCommand);
        return response.getResult().getOutput();
    }
}
