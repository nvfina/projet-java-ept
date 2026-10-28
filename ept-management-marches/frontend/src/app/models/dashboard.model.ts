export interface DashboardData {
  totalDemandes: number;
  demandesEnAttente: number;
  demandesValidees: number;
  demandesRejetees: number;
  demandesEnCours: number;
  totalMarches: number;
  marchesEnCours: number;
  marchesClotures: number;
  totalLots: number;
  lotsAttribues: number;
  lotsEnCoursLivraison: number;
  lotsClotures: number;
  tauxExecutionGlobal: number;
  montantTotalMarches: number;
  montantTotalDepenses: number;
  totalFournisseurs: number;
}

export interface DepenseService {
  serviceNom: string;
  nombreDemandes: number;
  montantTotal: number;
}

export interface DepenseMarche {
  marcheReference: string;
  marcheObjet: string;
  statut: string;
  budgetPrevisionnel: number;
  montantTotal: number;
}

export interface EvolutionPoint {
  periode: string;
  nombreDemandes: number;
  montantDepenses: number;
}

export interface TopFournisseur {
  fournisseurNom: string;
  nombreLots: number;
  montantTotal: number;
}
