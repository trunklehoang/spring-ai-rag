package com.trunk.springairag.adapter.controller;

import com.trunk.springairag.usecase.service.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("rest/document")
@AllArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    @GetMapping("/search/{prompt}")
    public String postDocumentSearch(@PathVariable("prompt") String prompt) throws IOException {
        return this.documentService.getSystemMessage(prompt).getContent();
    }
}
