package com.trunk.springairag.adapter.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Configuration
public class ApplicationConfig {
@Bean
public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingClient embeddingClient) {
    return new PgVectorStore(jdbcTemplate, embeddingClient);
}
    @Bean
    TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }

    static void init(VectorStore vectorStore, JdbcTemplate template)
            throws Exception {
        template.update("delete from vector_store");
        List<Document> documents = List.of(
                new Document("Bao Doan is the handsome guy with black skin"),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future."));
        var textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(documents));

    }

    @Bean
    ApplicationRunner applicationRunner(
            VectorStore vectorStore,
            JdbcTemplate jdbcTemplate) {
        return args -> {
            init(vectorStore, jdbcTemplate);
        };
    }
}
