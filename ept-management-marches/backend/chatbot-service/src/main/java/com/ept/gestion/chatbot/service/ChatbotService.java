package com.ept.gestion.chatbot.service;

import com.ept.gestion.chatbot.client.ProcurementClient;
import com.ept.gestion.chatbot.client.SupplierClient;
import com.ept.gestion.chatbot.dto.ChatMessageRequest;
import com.ept.gestion.chatbot.dto.ChatMessageResponse;
import com.ept.gestion.chatbot.nlp.IntentEnum;
import com.ept.gestion.chatbot.nlp.MoteurNLP;
import com.ept.gestion.chatbot.nlp.QueryResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {

    private final MoteurNLP moteurNLP;
    private final ProcurementClient procurementClient;
    private final SupplierClient supplierClient;

    public ChatbotService(MoteurNLP moteurNLP,
                          ProcurementClient procurementClient,
                          SupplierClient supplierClient) {
        this.moteurNLP = moteurNLP;
        this.procurementClient = procurementClient;
        this.supplierClient = supplierClient;
    }

    public ChatMessageResponse traiterMessage(ChatMessageRequest request) {
        QueryResult queryResult = moteurNLP.analyser(request.getMessage());
        IntentEnum intent = queryResult.getIntent();

        List<String> defaultSuggestions = List.of(
                "Quels sont les marchés actuellement en cours ?",
                "Quelles demandes sont encore en attente ?",
                "Quels fournisseurs interviennent dans le domaine informatique ?",
                "Quels sont les montants des marchés ?",
                "Donne-moi les statistiques générales"
        );

        switch (intent) {
            case RECHERCHE_MARCHE: {
                List<Map<String, Object>> marches = procurementClient.getMarches();
                if (marches.isEmpty()) {
                    return ChatMessageResponse.builder()
                            .reponse("Aucun marché n'est actuellement enregistré dans le système.")
                            .type("TEXTE")
                            .suggestions(defaultSuggestions)
                            .build();
                }
                StringBuilder sb = new StringBuilder("Voici les marchés enregistrés à l'EPT :\n\n");
                for (Map<String, Object> m : marches) {
                    sb.append(String.format("• **%s** : %s (Statut : `%s`, Montant : %s FCFA)\n",
                            m.get("reference"), m.get("objet"), m.get("statut"),
                            m.get("montantTotal") != null ? m.get("montantTotal") : "0"));
                }
                return ChatMessageResponse.builder()
                        .reponse(sb.toString())
                        .type("LISTE")
                        .donnees(marches)
                        .suggestions(List.of("Quelles demandes sont encore en attente ?", "Donne-moi les statistiques générales"))
                        .build();
            }

            case STATUT_LOT: {
                String numLotStr = queryResult.getParameters().get("numeroLot");
                Long lotId = null;
                try {
                    lotId = Long.parseLong(numLotStr);
                } catch (Exception ignored) {}

                if (lotId != null) {
                    Map<String, Object> lot = procurementClient.getLot(lotId);
                    if (lot != null) {
                        String rep = String.format("Le **Lot n°%s** (%s) est actuellement au statut **`%s`** avec un taux d'exécution de **%s%%**.\n" +
                                        "Fournisseur attributaire : **%s**\nMontant attribué : **%s FCFA**",
                                lot.get("numero"), lot.get("designation"), lot.get("statut"),
                                lot.get("tauxExecution"),
                                lot.get("fournisseurNom") != null ? lot.get("fournisseurNom") : "Non attribué",
                                lot.get("montantAttribue") != null ? lot.get("montantAttribue") : "0");
                        return ChatMessageResponse.builder()
                                .reponse(rep)
                                .type("TEXTE")
                                .donnees(lot)
                                .suggestions(defaultSuggestions)
                                .build();
                    }
                }
                return ChatMessageResponse.builder()
                        .reponse("Désolé, je n'ai pas trouvé d'information pour ce lot. Veuillez vérifier le numéro.")
                        .type("TEXTE")
                        .suggestions(defaultSuggestions)
                        .build();
            }

            case DEMANDES_EN_ATTENTE: {
                List<Map<String, Object>> demandes = procurementClient.getDemandesEnAttente();
                if (demandes.isEmpty()) {
                    return ChatMessageResponse.builder()
                            .reponse("Il n'y a actuellement aucune expression de besoin en attente de validation.")
                            .type("TEXTE")
                            .suggestions(defaultSuggestions)
                            .build();
                }
                StringBuilder sb = new StringBuilder(String.format("Il y a actuellement **%d** demande(s) en attente de validation :\n\n", demandes.size()));
                for (Map<String, Object> d : demandes) {
                    sb.append(String.format("• **%s** (Service : *%s*, Demandeur : %s) : %s\n",
                            d.get("reference"), d.get("serviceDemandeur"), d.get("demandeurNom"), d.get("description")));
                }
                return ChatMessageResponse.builder()
                        .reponse(sb.toString())
                        .type("LISTE")
                        .donnees(demandes)
                        .suggestions(List.of("Quels sont les marchés en cours ?", "Donne-moi les statistiques générales"))
                        .build();
            }

            case RECHERCHE_FOURNISSEUR: {
                String domaine = queryResult.getParameters().get("domaine");
                List<Map<String, Object>> fournisseurs;
                if (domaine != null && !domaine.isBlank()) {
                    fournisseurs = supplierClient.getFournisseursParDomaine(domaine);
                } else {
                    fournisseurs = supplierClient.getFournisseursActifs();
                }

                if (fournisseurs.isEmpty()) {
                    return ChatMessageResponse.builder()
                            .reponse(domaine != null ?
                                    "Aucun fournisseur actif n'a été trouvé pour le domaine : " + domaine :
                                    "Aucun fournisseur actif n'est référencé.")
                            .type("TEXTE")
                            .suggestions(defaultSuggestions)
                            .build();
                }

                StringBuilder sb = new StringBuilder(String.format("Voici les fournisseurs répertoriés%s :\n\n",
                        domaine != null ? " dans le domaine **" + domaine + "**" : ""));
                for (Map<String, Object> f : fournisseurs) {
                    sb.append(String.format("• **%s** (NINEA : `%s`, Domaine : %s, Tél : %s, Email : %s)\n",
                            f.get("raisonSociale"), f.get("ninea"), f.get("domaineIntervention"),
                            f.get("telephone"), f.get("email")));
                }

                return ChatMessageResponse.builder()
                        .reponse(sb.toString())
                        .type("LISTE")
                        .donnees(fournisseurs)
                        .suggestions(List.of("Quels sont les marchés actuellement en cours ?", "Quelles demandes sont encore en attente ?"))
                        .build();
            }

            case MONTANTS_MARCHES: {
                Map<String, Object> stats = procurementClient.getStatistiquesGlobales();
                Object totalDepenses = stats.get("montantTotalDepenses");
                String rep = String.format("Le montant total engagé sur les marchés de l'EPT s'élève à **%s FCFA**.\n" +
                                "Nombre total de marchés : **%s** (dont **%s** clôturé(s)).",
                        totalDepenses != null ? totalDepenses : "0",
                        stats.get("totalMarches"), stats.get("marchesClotures"));
                return ChatMessageResponse.builder()
                        .reponse(rep)
                        .type("TEXTE")
                        .donnees(stats)
                        .suggestions(defaultSuggestions)
                        .build();
            }

            case STATISTIQUES: {
                Map<String, Object> stats = procurementClient.getStatistiquesGlobales();
                StringBuilder sb = new StringBuilder("📊 **Synthèse statistique de la gestion des marchés EPT :**\n\n");
                sb.append(String.format("• Demandes de matériels : **%s** au total (En attente : %s, Validées : %s, Rejetées : %s)\n",
                        stats.get("totalDemandes"), stats.get("demandesEnAttente"), stats.get("demandesValidees"), stats.get("demandesRejetees")));
                sb.append(String.format("• Marchés publics : **%s** (dont %s en cours / attribués)\n",
                        stats.get("totalMarches"), stats.get("marchesEnCours")));
                sb.append(String.format("• Lots constitués : **%s** (dont %s attribués, %s en livraison)\n",
                        stats.get("totalLots"), stats.get("lotsAttribues"), stats.get("lotsEnCoursLivraison")));
                sb.append(String.format("• Taux d'exécution global des livraisons : **%s%%**\n",
                        stats.get("tauxExecutionGlobal")));
                sb.append(String.format("• Montant total des dépenses : **%s FCFA**\n",
                        stats.get("montantTotalDepenses")));

                return ChatMessageResponse.builder()
                        .reponse(sb.toString())
                        .type("TEXTE")
                        .donnees(stats)
                        .suggestions(List.of("Quels sont les marchés actuellement en cours ?", "Quels fournisseurs interviennent dans le domaine informatique ?"))
                        .build();
            }

            case AIDE:
            default: {
                String help = "Bonjour ! Je suis l'assistant conversationnel de la gestion des marchés de l'**École Polytechnique de Thiès** (EPT).\n\n" +
                        "Vous pouvez me poser des questions en français sur :\n" +
                        "1. **Les marchés** : *\"Quels sont les marchés actuellement en cours ?\"*\n" +
                        "2. **Les lots et livraisons** : *\"Quel est le statut du lot 1 ?\"*\n" +
                        "3. **Les fournisseurs** : *\"Quels fournisseurs interviennent dans le domaine informatique ?\"*\n" +
                        "4. **Les besoins** : *\"Quelles demandes sont encore en attente ?\"*\n" +
                        "5. **Les statistiques et budgets** : *\"Quels sont les montants des marchés ?\"*";
                return ChatMessageResponse.builder()
                        .reponse(help)
                        .type("TEXTE")
                        .suggestions(defaultSuggestions)
                        .build();
            }
        }
    }

    public List<String> getSuggestions() {
        return List.of(
                "Quels sont les marchés actuellement en cours ?",
                "Quelles demandes sont encore en attente ?",
                "Quel est le statut du lot 1 ?",
                "Quels fournisseurs interviennent dans le domaine informatique ?",
                "Quels sont les montants des marchés ?",
                "Donne-moi les statistiques générales"
        );
    }
}
