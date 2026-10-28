export interface Fournisseur {
  id: number;
  raisonSociale: string;
  ninea?: string;
  adresse?: string;
  telephone: string;
  email?: string;
  domaineIntervention: string;
  siteWeb?: string;
  contactPrincipal?: string;
  actif: boolean;
  dateCreation: string;
  dateModification?: string;
  observations?: string;
}

export interface FournisseurCreateRequest {
  raisonSociale: string;
  ninea?: string;
  adresse?: string;
  telephone: string;
  email?: string;
  domaineIntervention: string;
  siteWeb?: string;
  contactPrincipal?: string;
  observations?: string;
}
