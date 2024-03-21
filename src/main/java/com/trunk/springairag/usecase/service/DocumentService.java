package com.trunk.springairag.usecase.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DocumentService {
    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private static final String PROMPT =
    """
    Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
    If unsure, simply state that you don't know.
    QUESTION:{input}
    DOCUMENTS:{documents}
            """;
    @Autowired
    public DocumentService(VectorStore vectorStore, ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }

    public Message getSystemMessage(String prompt) {
        // Retrieve documents similar to a query
        List<Document> results = vectorStore.similaritySearch(SearchRequest.query(prompt).withTopK(5));
        SystemPromptTemplate systemPromptTemplate =  new SystemPromptTemplate(PROMPT);
        Prompt promptCommand = new Prompt(List.of(systemPromptTemplate.createMessage(Map.of("input", prompt,"documents",results.toString()))));
        ChatResponse response = chatClient.call(promptCommand);
        return response.getResult().getOutput();
    }
}
