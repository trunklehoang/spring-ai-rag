package com.trunk.springairag.usecase.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DocumentService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    @Value("${app.resource}")
    private Resource pdfResource;

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
    @Autowired
    public DocumentService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

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
    public void loadDocuments() {
        TikaDocumentReader documentReader = new TikaDocumentReader(pdfResource);
        List<Document> documents = documentReader.get();
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> splitDocuments = textSplitter.apply(documents);
        vectorStore.add(splitDocuments);
    }

    public Document similaritySearch(String message) {
      return vectorStore.similaritySearch(SearchRequest.query(message).withTopK(1)).get(0);
    }
}
