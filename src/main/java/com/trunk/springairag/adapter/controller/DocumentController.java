package com.trunk.springairag.adapter.controller;

import com.trunk.springairag.usecase.service.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("rest/document")
@AllArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    @GetMapping("/search/{prompt}")
    public String postDocumentSearch(@PathVariable("prompt") String prompt) throws IOException {
        return this.documentService.getSystemMessage(prompt);
    }
    @PostMapping("/ocr/")
    public String postDocumentSearch(@RequestBody MultipartFile image) throws IOException {
      //  return this.documentService.getSystemMessageFromImage(image).content();
        return null;
    }
}
