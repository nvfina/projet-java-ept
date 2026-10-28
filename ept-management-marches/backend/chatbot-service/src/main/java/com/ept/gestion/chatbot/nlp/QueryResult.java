package com.ept.gestion.chatbot.nlp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResult {
    private IntentEnum intent;
    private double confidence;
    @Builder.Default
    private Map<String, String> parameters = new HashMap<>();
}
