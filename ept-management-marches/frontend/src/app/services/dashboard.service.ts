import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { DashboardData, DepenseMarche, DepenseService, EvolutionPoint, TopFournisseur } from '../models/dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly apiUrl = `${environment.apiUrl}/dashboard`;
  private readonly reportsUrl = `${environment.apiUrl}/rapports`;

  constructor(private http: HttpClient) {}

  getIndicateurs(): Observable<ApiResponse<DashboardData>> {
    return this.http.get<ApiResponse<DashboardData>>(`${this.apiUrl}/indicateurs`);
  }

  getDepensesParService(): Observable<ApiResponse<DepenseService[]>> {
    return this.http.get<ApiResponse<DepenseService[]>>(`${this.apiUrl}/depenses/service`);
  }

  getDepensesParMarche(): Observable<ApiResponse<DepenseMarche[]>> {
    return this.http.get<ApiResponse<DepenseMarche[]>>(`${this.apiUrl}/depenses/marche`);
  }

  getEvolution(): Observable<ApiResponse<EvolutionPoint[]>> {
    return this.http.get<ApiResponse<EvolutionPoint[]>>(`${this.apiUrl}/evolution`);
  }

  getTopFournisseurs(): Observable<ApiResponse<TopFournisseur[]>> {
    return this.http.get<ApiResponse<TopFournisseur[]>>(`${this.apiUrl}/top-fournisseurs`);
  }

  telechargerRapportMarchesExcel(): Observable<Blob> {
    return this.http.get(`${this.reportsUrl}/marches/excel`, { responseType: 'blob' });
  }

  telechargerRapportDepensesExcel(): Observable<Blob> {
    return this.http.get(`${this.reportsUrl}/depenses/excel`, { responseType: 'blob' });
  }

  telechargerRapportCSV(type: string): Observable<Blob> {
    return this.http.get(`${this.reportsUrl}/csv?type=${type}`, { responseType: 'blob' });
  }
}
