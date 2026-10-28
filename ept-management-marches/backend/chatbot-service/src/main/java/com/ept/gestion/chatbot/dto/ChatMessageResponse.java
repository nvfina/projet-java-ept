package com.ept.gestion.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private String reponse;
    private String type; // TEXTE, LISTE, TABLEAU
    private Object donnees;
    @Builder.Default
    private List<String> suggestions = new ArrayList<>();
}
