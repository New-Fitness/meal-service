package org.tesinitsyn.mealservice.ai;

import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;
import org.tesinitsyn.mealservice.ai.service.ChatMemoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatMemoryService chatMemoryService;

    public ChatController(ChatMemoryService chatMemoryService) {
        this.chatMemoryService = chatMemoryService;
    }

    /**
     * Сохраняет сообщение пользователя или ИИ в память.
     *
     * Пример запроса:
     * POST /api/chat/2a31b9f5-bb25-4b8a-9b92-9e5c20a88d31/message?role=USER
     * Body: "Привет, как дела?"
     */
    @PostMapping("/{userId}/message")
    public void saveMessage(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "USER") String role,
            @RequestBody String content
    ) {
        chatMemoryService.saveMessage(userId, role, content);
    }

    /**
     * Находит похожие сообщения по смыслу.
     *
     * Пример запроса:
     * GET /api/chat/2a31b9f5-bb25-4b8a-9b92-9e5c20a88d31/similar?query=распорядок%20дня&limit=5
     */
    @GetMapping("/{userId}/similar")
    public List<Document> findSimilar(
            @PathVariable UUID userId,
            @RequestParam String query
    ) {
        return chatMemoryService.findSimilar(userId, query);
    }
}