export interface ApiResponse<T> {
  succes: boolean;
  message?: string;
  donnees?: T;
  horodatage?: string;
}

export interface PageResponse<T> {
  contenu: T[];
  page: number;
  taille: number;
  totalElements: number;
  totalPages: number;
  premier: boolean;
  dernier: boolean;
}
