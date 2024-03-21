package com.trunk.springairag.adapter.config;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ApplicationConfig {
@Bean
public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingClient embeddingClient) {
    return new PgVectorStore(jdbcTemplate, embeddingClient);
}
}
