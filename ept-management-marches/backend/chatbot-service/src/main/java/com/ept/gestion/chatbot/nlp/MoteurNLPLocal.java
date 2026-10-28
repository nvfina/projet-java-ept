package com.ept.gestion.chatbot.nlp;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MoteurNLPLocal implements MoteurNLP {

    private static final Pattern PATTERN_LOT = Pattern.compile("lot\\s+(?:num[ée]ro\\s+|n[°o]\\s*)?(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_DOMAINE = Pattern.compile("domaine\\s+([a-zA-ZÀ-ÿ]+)", Pattern.CASE_INSENSITIVE);

    @Override
    public QueryResult analyser(String message) {
        if (message == null || message.trim().isBlank()) {
            return QueryResult.builder().intent(IntentEnum.INCONNU).confidence(0.0).build();
        }

        String msg = message.toLowerCase(Locale.FRENCH).trim();
        Map<String, String> params = new HashMap<>();

        // 1. Détection extraction numéro de lot
        Matcher matcherLot = PATTERN_LOT.matcher(msg);
        if (matcherLot.find()) {
            params.put("numeroLot", matcherLot.group(1));
            return QueryResult.builder()
                    .intent(IntentEnum.STATUT_LOT)
                    .confidence(0.95)
                    .parameters(params)
                    .build();
        }

        // 2. Détection demandes en attente
        if ((msg.contains("demande") || msg.contains("besoin")) && (msg.contains("attente") || msg.contains("en attente") || msg.contains("traiter"))) {
            return QueryResult.builder().intent(IntentEnum.DEMANDES_EN_ATTENTE).confidence(0.90).parameters(params).build();
        }

        // 3. Détection fournisseurs et domaine
        if (msg.contains("fournisseur") || msg.contains("fournisseurs") || msg.contains("prestataire")) {
            Matcher matcherDomaine = PATTERN_DOMAINE.matcher(msg);
            if (matcherDomaine.find()) {
                params.put("domaine", matcherDomaine.group(1));
            } else if (msg.contains("informatique")) {
                params.put("domaine", "informatique");
            } else if (msg.contains("btp") || msg.contains("construction") || msg.contains("civil")) {
                params.put("domaine", "btp");
            } else if (msg.contains("bureau") || msg.contains("fourniture")) {
                params.put("domaine", "fourniture");
            } else if (msg.contains("electr") || msg.contains("électr")) {
                params.put("domaine", "électrique");
            } else if (msg.contains("meca") || msg.contains("méca")) {
                params.put("domaine", "mécanique");
            }
            return QueryResult.builder().intent(IntentEnum.RECHERCHE_FOURNISSEUR).confidence(0.90).parameters(params).build();
        }

        // 4. Détection montants / budgets
        if (msg.contains("montant") || msg.contains("budget") || msg.contains("cout") || msg.contains("coût") || msg.contains("depense") || msg.contains("dépense")) {
            return QueryResult.builder().intent(IntentEnum.MONTANTS_MARCHES).confidence(0.85).parameters(params).build();
        }

        // 5. Détection marchés en cours ou tous marchés
        if (msg.contains("marché") || msg.contains("marche") || msg.contains("marches") || msg.contains("marchés")) {
            return QueryResult.builder().intent(IntentEnum.RECHERCHE_MARCHE).confidence(0.90).parameters(params).build();
        }

        // 6. Statistiques générales
        if (msg.contains("statistique") || msg.contains("kpi") || msg.contains("indicateur") || msg.contains("taux") || msg.contains("combien")) {
            return QueryResult.builder().intent(IntentEnum.STATISTIQUES).confidence(0.80).parameters(params).build();
        }

        // 7. Aide
        if (msg.contains("aide") || msg.contains("help") || msg.contains("bonjour") || msg.contains("salut") || msg.contains("que sais-tu")) {
            return QueryResult.builder().intent(IntentEnum.AIDE).confidence(0.95).parameters(params).build();
        }

        return QueryResult.builder().intent(IntentEnum.INCONNU).confidence(0.20).parameters(params).build();
    }
}
