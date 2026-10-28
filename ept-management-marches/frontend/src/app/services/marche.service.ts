import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Livraison, Lot, Marche, OffreTarifaire, StatutMarche } from '../models/marche.model';

@Injectable({
  providedIn: 'root'
})
export class MarcheService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // --- Marchés ---
  listerMarches(recherche?: string, statut?: StatutMarche, page = 0, size = 10): Observable<ApiResponse<PageResponse<Marche>>> {
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    if (recherche) params = params.set('recherche', recherche);
    if (statut) params = params.set('statut', statut);

    return this.http.get<ApiResponse<PageResponse<Marche>>>(`${this.baseUrl}/marches`, { params });
  }

  trouverMarcheParId(id: number): Observable<ApiResponse<Marche>> {
    return this.http.get<ApiResponse<Marche>>(`${this.baseUrl}/marches/${id}`);
  }

  creerMarche(request: any): Observable<ApiResponse<Marche>> {
    return this.http.post<ApiResponse<Marche>>(`${this.baseUrl}/marches`, request);
  }

  modifierMarche(id: number, request: any): Observable<ApiResponse<Marche>> {
    return this.http.put<ApiResponse<Marche>>(`${this.baseUrl}/marches/${id}`, request);
  }

  changerStatutMarche(id: number, statut: StatutMarche): Observable<ApiResponse<Marche>> {
    return this.http.put<ApiResponse<Marche>>(`${this.baseUrl}/marches/${id}/statut?statut=${statut}`, {});
  }

  // --- Lots ---
  creerLot(marcheId: number, request: any): Observable<ApiResponse<Lot>> {
    return this.http.post<ApiResponse<Lot>>(`${this.baseUrl}/lots/marche/${marcheId}`, request);
  }

  trouverLotParId(id: number): Observable<ApiResponse<Lot>> {
    return this.http.get<ApiResponse<Lot>>(`${this.baseUrl}/lots/${id}`);
  }

  attribuerLot(lotId: number, request: any): Observable<ApiResponse<Lot>> {
    return this.http.put<ApiResponse<Lot>>(`${this.baseUrl}/lots/${lotId}/attribuer`, request);
  }

  getOffresDuLot(lotId: number): Observable<ApiResponse<OffreTarifaire[]>> {
    return this.http.get<ApiResponse<OffreTarifaire[]>>(`${this.baseUrl}/lots/${lotId}/offres`);
  }

  comparerOffres(lotId: number): Observable<ApiResponse<OffreTarifaire[]>> {
    return this.http.get<ApiResponse<OffreTarifaire[]>>(`${this.baseUrl}/lots/${lotId}/offres/comparer`);
  }

  getLivraisonsDuLot(lotId: number): Observable<ApiResponse<Livraison[]>> {
    return this.http.get<ApiResponse<Livraison[]>>(`${this.baseUrl}/lots/${lotId}/livraisons`);
  }

  // --- Offres ---
  creerOffre(request: any): Observable<ApiResponse<OffreTarifaire>> {
    return this.http.post<ApiResponse<OffreTarifaire>>(`${this.baseUrl}/offres`, request);
  }

  evaluerOffre(offreId: number, statut: string, observations?: string): Observable<ApiResponse<OffreTarifaire>> {
    return this.http.put<ApiResponse<OffreTarifaire>>(`${this.baseUrl}/offres/${offreId}/evaluer`, { statut, observations });
  }

  retenirOffre(offreId: number): Observable<ApiResponse<OffreTarifaire>> {
    return this.http.put<ApiResponse<OffreTarifaire>>(`${this.baseUrl}/offres/${offreId}/retenir`, {});
  }

  // --- Livraisons ---
  enregistrerLivraison(request: any): Observable<ApiResponse<Livraison>> {
    return this.http.post<ApiResponse<Livraison>>(`${this.baseUrl}/livraisons`, request);
  }
}
