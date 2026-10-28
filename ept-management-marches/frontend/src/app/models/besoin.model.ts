export type StatutBesoin = 'EN_ATTENTE' | 'VALIDE' | 'REJETE' | 'EN_COURS_ACQUISITION' | 'CLOTURE';

export interface LigneBesoin {
  id?: number;
  designation: string;
  description?: string;
  quantiteDemandee: number;
  unite?: string;
  estimationPrix?: number;
  observations?: string;
}

export interface ExpressionBesoin {
  id: number;
  reference: string;
  serviceDemandeur: string;
  demandeurId: number;
  demandeurNom?: string;
  description: string;
  motif?: string;
  statut: StatutBesoin;
  dateCreation: string;
  dateModification?: string;
  dateValidation?: string;
  validateurId?: number;
  validateurNom?: string;
  commentaireDecision?: string;
  urgente: boolean;
  lignes: LigneBesoin[];
}

export interface BesoinCreateRequest {
  description: string;
  motif?: string;
  urgente: boolean;
  lignes: LigneBesoin[];
}

export interface HistoriqueStatut {
  id: number;
  entiteType: string;
  entiteId: number;
  ancienStatut?: string;
  nouveauStatut: string;
  dateChangement: string;
  utilisateurId?: number;
  utilisateurNom?: string;
  commentaire?: string;
}
