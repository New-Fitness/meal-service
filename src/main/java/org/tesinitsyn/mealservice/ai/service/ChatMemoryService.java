package org.tesinitsyn.mealservice.ai.service;


import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ChatMemoryService {

    private final VectorStore vectorStore;

    public ChatMemoryService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void saveMessage(UUID userId, String role, String content) {
        Document doc = new Document(
                content,
                Map.of("userId", userId.toString(), "role", role)
        );
        this.vectorStore.add(List.of(doc)); // Spring AI сам создаст embedding и сохранит
    }

    public List<Document> findSimilar(UUID userId, String query) {
        return this.vectorStore.similaritySearch("train")
                .stream()
                .filter(document -> document.getScore() > 0.50)
                .toList();
    }
}
