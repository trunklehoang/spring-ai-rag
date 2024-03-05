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

    private static final String ollamaPrompt = """ 
			You're assisting with questions about documents in a catalog.
			Use the information from the DOCUMENTS section to provide accurate answers.
			If unsure, simply state that you don't know." + " {prompt}"
			""";

    public Message getSystemMessage(String prompt) {
        SystemPromptTemplate systemPromptTemplate =  new SystemPromptTemplate(this.ollamaPrompt);
        Prompt promptCommand = new Prompt(List.of(systemPromptTemplate.createMessage(Map.of("prompt", prompt))));
        ChatResponse response = chatClient.call(promptCommand);
        return response.getResult().getOutput();
    }
}
