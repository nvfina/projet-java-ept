import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { BesoinCreateRequest, ExpressionBesoin, HistoriqueStatut, StatutBesoin } from '../models/besoin.model';

@Injectable({
  providedIn: 'root'
})
export class BesoinService {
  private readonly apiUrl = `${environment.apiUrl}/besoins`;

  constructor(private http: HttpClient) {}

  creer(request: BesoinCreateRequest): Observable<ApiResponse<ExpressionBesoin>> {
    return this.http.post<ApiResponse<ExpressionBesoin>>(this.apiUrl, request);
  }

  lister(service?: string, statut?: StatutBesoin, page = 0, size = 10): Observable<ApiResponse<PageResponse<ExpressionBesoin>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (service) params = params.set('service', service);
    if (statut) params = params.set('statut', statut);

    return this.http.get<ApiResponse<PageResponse<ExpressionBesoin>>>(this.apiUrl, { params });
  }

  mesBesoins(page = 0, size = 10): Observable<ApiResponse<PageResponse<ExpressionBesoin>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<PageResponse<ExpressionBesoin>>>(`${this.apiUrl}/mes-besoins`, { params });
  }

  listerValides(): Observable<ApiResponse<ExpressionBesoin[]>> {
    return this.http.get<ApiResponse<ExpressionBesoin[]>>(`${this.apiUrl}/valides`);
  }

  trouverParId(id: number): Observable<ApiResponse<ExpressionBesoin>> {
    return this.http.get<ApiResponse<ExpressionBesoin>>(`${this.apiUrl}/${id}`);
  }

  modifier(id: number, request: any): Observable<ApiResponse<ExpressionBesoin>> {
    return this.http.put<ApiResponse<ExpressionBesoin>>(`${this.apiUrl}/${id}`, request);
  }

  annuler(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }

  valider(id: number, commentaire?: string): Observable<ApiResponse<ExpressionBesoin>> {
    return this.http.put<ApiResponse<ExpressionBesoin>>(`${this.apiUrl}/${id}/valider`, { commentaire });
  }

  rejeter(id: number, commentaire?: string): Observable<ApiResponse<ExpressionBesoin>> {
    return this.http.put<ApiResponse<ExpressionBesoin>>(`${this.apiUrl}/${id}/rejeter`, { commentaire });
  }

  getHistorique(id: number): Observable<ApiResponse<HistoriqueStatut[]>> {
    return this.http.get<ApiResponse<HistoriqueStatut[]>>(`${this.apiUrl}/${id}/historique`);
  }
}
