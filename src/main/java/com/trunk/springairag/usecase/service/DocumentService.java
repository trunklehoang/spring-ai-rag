package com.trunk.springairag.usecase.service;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class DocumentService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    private static final String PROMPT = """ 
			You're assisting with questions about documents in a catalog.
			Use the information from the DOCUMENTS section to provide accurate answers.
			If unsure, simply state that you don't know." + " {prompt}"
			""";
    private static final String RAG_PROMPT = """ 
			You're assisting with questions about documents in a catalog.
			Use the information from the DOCUMENTS section to provide accurate answers.
			If unsure, simply state that you don't know." + " {prompt}" + "DOCUMENTS:" + "{documents}
			""";

    public Message getSystemMessage(String prompt) {
        SystemPromptTemplate systemPromptTemplate =  new SystemPromptTemplate(PROMPT);
        Prompt promptCommand = new Prompt(List.of(systemPromptTemplate.createMessage(Map.of("prompt", prompt))));
        ChatResponse response = chatClient.call(promptCommand);
        return response.getResult().getOutput();
    }
    public Message getRAGSystemMessage(String prompt) {
        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.query(prompt).withTopK(2));
        var documentStr = similarDocuments.stream().map(Document::getContent).reduce("", (a, b) -> a + "\n" + b);
        SystemPromptTemplate systemPromptTemplate =  new SystemPromptTemplate(RAG_PROMPT);
        Prompt promptCommand = new Prompt(List.of(systemPromptTemplate.createMessage(Map.of("documents", documentStr, "prompt", prompt))));
        ChatResponse response = chatClient.call(promptCommand);
        return response.getResult().getOutput();
    }
}
