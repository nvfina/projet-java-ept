export interface ChatMessage {
  id: string;
  texte: string;
  emetteur: 'utilisateur' | 'bot';
  date: Date;
  type?: 'TEXTE' | 'LISTE' | 'TABLEAU';
  donnees?: any;
  suggestions?: string[];
}

export interface ChatResponse {
  reponse: string;
  type: string;
  donnees?: any;
  suggestions: string[];
}
