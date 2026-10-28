import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Fournisseur, FournisseurCreateRequest } from '../models/fournisseur.model';

@Injectable({
  providedIn: 'root'
})
export class FournisseurService {
  private readonly apiUrl = `${environment.apiUrl}/fournisseurs`;

  constructor(private http: HttpClient) {}

  lister(page = 0, size = 10): Observable<ApiResponse<PageResponse<Fournisseur>>> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<ApiResponse<PageResponse<Fournisseur>>>(this.apiUrl, { params });
  }

  rechercher(criteres: any, page = 0, size = 10): Observable<ApiResponse<PageResponse<Fournisseur>>> {
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    if (criteres.raisonSociale) params = params.set('raisonSociale', criteres.raisonSociale);
    if (criteres.ninea) params = params.set('ninea', criteres.ninea);
    if (criteres.domaineIntervention) params = params.set('domaineIntervention', criteres.domaineIntervention);

    return this.http.get<ApiResponse<PageResponse<Fournisseur>>>(`${this.apiUrl}/recherche`, { params });
  }

  listerActifs(): Observable<ApiResponse<Fournisseur[]>> {
    return this.http.get<ApiResponse<Fournisseur[]>>(`${this.apiUrl}/actifs`);
  }

  trouverParId(id: number): Observable<ApiResponse<Fournisseur>> {
    return this.http.get<ApiResponse<Fournisseur>>(`${this.apiUrl}/${id}`);
  }

  creer(request: FournisseurCreateRequest): Observable<ApiResponse<Fournisseur>> {
    return this.http.post<ApiResponse<Fournisseur>>(this.apiUrl, request);
  }

  modifier(id: number, request: any): Observable<ApiResponse<Fournisseur>> {
    return this.http.put<ApiResponse<Fournisseur>>(`${this.apiUrl}/${id}`, request);
  }

  activer(id: number): Observable<ApiResponse<Fournisseur>> {
    return this.http.put<ApiResponse<Fournisseur>>(`${this.apiUrl}/${id}/activer`, {});
  }

  desactiver(id: number): Observable<ApiResponse<Fournisseur>> {
    return this.http.put<ApiResponse<Fournisseur>>(`${this.apiUrl}/${id}/desactiver`, {});
  }
}
