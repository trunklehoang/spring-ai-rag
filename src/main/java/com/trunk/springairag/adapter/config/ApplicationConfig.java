package com.trunk.springairag.adapter.config;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.transformers.TransformersEmbeddingClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
@Configuration
public class ApplicationConfig {
    @Primary
    @Bean
    public EmbeddingClient embeddingClient() {
        return new TransformersEmbeddingClient();
    }
}
