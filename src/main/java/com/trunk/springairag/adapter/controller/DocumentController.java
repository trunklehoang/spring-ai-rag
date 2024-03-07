package com.trunk.springairag.adapter.controller;

import com.trunk.springairag.usecase.service.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rest/document")
@AllArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    @GetMapping("/search/{prompt}")
    public String postDocumentSearch(@PathVariable("prompt") String prompt) {
        return this.documentService.getSystemMessage(prompt).getContent();
    }
    @GetMapping("/rag/{prompt}")
    public String postRAGDocumentSearch(@PathVariable("prompt") String prompt) {
        return this.documentService.getRAGSystemMessage(prompt).getContent();
    }


    @GetMapping("/search/similaritySearch/{message}")
    public String similaritySearch(@PathVariable("message") String message) {
        return this.documentService.similaritySearch(message).getContent();
    }

    @GetMapping("load")
    public String postDocumentSearch() {
         this.documentService.loadDocuments();
         return "success";
    }
}
