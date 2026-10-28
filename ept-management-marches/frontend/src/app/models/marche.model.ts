export type StatutMarche = 'BROUILLON' | 'EN_COURS' | 'ATTRIBUE' | 'EN_EXECUTION' | 'CLOTURE' | 'ANNULE';
export type StatutLot = 'EN_ATTENTE' | 'ATTRIBUE' | 'EN_COURS_LIVRAISON' | 'LIVRE_PARTIELLEMENT' | 'LIVRE' | 'CLOTURE' | 'ANNULE';
export type StatutOffre = 'SOUMISE' | 'EN_EVALUATION' | 'RETENUE' | 'REJETEE';

export interface LigneLot {
  id?: number;
  ligneBesoinId?: number;
  designation: string;
  quantite: number;
  prixUnitaire?: number;
  montantTotal?: number;
}

export interface Lot {
  id: number;
  numero: number;
  designation: string;
  description?: string;
  marcheId: number;
  marcheReference?: string;
  statut: StatutLot;
  montantEstime?: number;
  montantAttribue?: number;
  fournisseurId?: number;
  fournisseurNom?: string;
  dateAttribution?: string;
  dateLivraisonPrevue?: string;
  dateLivraisonEffective?: string;
  tauxExecution: number;
  observations?: string;
  lignesLot: LigneLot[];
}

export interface Marche {
  id: number;
  reference: string;
  objet: string;
  description?: string;
  statut: StatutMarche;
  budgetPrevisionnel?: number;
  montantTotal?: number;
  dateCreation: string;
  dateLancement?: string;
  dateAttribution?: string;
  dateCloture?: string;
  createurNom?: string;
  observations?: string;
  lots: Lot[];
}

export interface LigneOffre {
  id?: number;
  designation: string;
  quantite: number;
  prixUnitaire: number;
  montantLigne?: number;
  observations?: string;
}

export interface OffreTarifaire {
  id: number;
  lotId: number;
  lotDesignation?: string;
  fournisseurId: number;
  fournisseurNom: string;
  dateOffre: string;
  statut: StatutOffre;
  montantTotal: number;
  devise: string;
  observations?: string;
  lignesOffre: LigneOffre[];
}

export interface Livraison {
  id: number;
  lotId: number;
  dateLivraison: string;
  quantiteLivree: number;
  designation?: string;
  observations?: string;
  receptionnaireNom?: string;
  conforme?: boolean;
}
