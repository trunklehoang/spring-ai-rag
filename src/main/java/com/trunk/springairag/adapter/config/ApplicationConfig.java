package com.trunk.springairag.adapter.config;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.postgresml.PostgresMlEmbeddingClient;
import org.springframework.ai.postgresml.PostgresMlEmbeddingOptions;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ApplicationConfig {
@Bean
public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingClient embeddingClient) {
    return new PgVectorStore(jdbcTemplate, embeddingClient);
}
    @Primary
    @Bean
    public EmbeddingClient embeddingClient(JdbcTemplate jdbcTemplate) {
        return new PostgresMlEmbeddingClient(jdbcTemplate,
                PostgresMlEmbeddingOptions.builder() // huggingface transformer model name.
                        .withVectorType(PostgresMlEmbeddingClient.VectorType.PG_VECTOR) //vector type in PostgreSQL.
//                        .withKwargs(Map.of("device", "cpu")) // optional arguments.
                        .withMetadataMode(MetadataMode.EMBED) // Document metadata mode.
                        .build());    }
}
