export type RoleType = 'ADMIN' | 'CHEF_SERVICE' | 'SERVICE_ACHATS' | 'DIRECTION';

export interface LoginRequest {
  email: string;
  motDePasse: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  email: string;
  nom: string;
  prenom: string;
  role: RoleType;
  service: string;
  expiresIn: number;
}

export interface User {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  telephone?: string;
  actif: boolean;
  role: RoleType;
  serviceNom: string;
  dateCreation?: string;
  dateDerniereConnexion?: string;
}

export interface ServiceDepartement {
  id: number;
  nom: string;
  description?: string;
  actif: boolean;
}
