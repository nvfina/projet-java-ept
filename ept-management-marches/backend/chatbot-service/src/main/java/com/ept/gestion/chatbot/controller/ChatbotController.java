package com.ept.gestion.chatbot.controller;

import com.ept.gestion.chatbot.dto.ChatMessageRequest;
import com.ept.gestion.chatbot.dto.ChatMessageResponse;
import com.ept.gestion.chatbot.service.ChatbotService;
import com.ept.gestion.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
@Tag(name = "Chatbot", description = "Recherche conversationnelle en langage naturel sur les marchés, fournisseurs et besoins")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/message")
    @Operation(summary = "Poser une question en langage naturel", description = "Interroge les données réelles de l'application selon l'intention reconnue")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> poserQuestion(@Valid @RequestBody ChatMessageRequest request) {
        ChatMessageResponse response = chatbotService.traiterMessage(request);
        return ResponseEntity.ok(ApiResponse.succes(response));
    }

    @GetMapping("/suggestions")
    @Operation(summary = "Obtenir des suggestions de questions à poser au chatbot")
    public ResponseEntity<ApiResponse<List<String>>> getSuggestions() {
        return ResponseEntity.ok(ApiResponse.succes(chatbotService.getSuggestions()));
    }
}
