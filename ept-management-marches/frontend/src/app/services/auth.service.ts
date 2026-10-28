import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { LoginRequest, LoginResponse, User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;
  
  currentUser = signal<User | null>(this.getStoredUser());

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/login`, credentials).pipe(
      tap(res => {
        if (res.succes && res.donnees) {
          localStorage.setItem('ept_token', res.donnees.token);
          const user: User = {
            id: 0,
            nom: res.donnees.nom,
            prenom: res.donnees.prenom,
            email: res.donnees.email,
            role: res.donnees.role,
            serviceNom: res.donnees.service,
            actif: true
          };
          localStorage.setItem('ept_user', JSON.stringify(user));
          this.currentUser.set(user);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('ept_token');
    localStorage.removeItem('ept_user');
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getProfile(): Observable<ApiResponse<User>> {
    return this.http.get<ApiResponse<User>>(`${this.apiUrl}/me`).pipe(
      tap(res => {
        if (res.succes && res.donnees) {
          localStorage.setItem('ept_user', JSON.stringify(res.donnees));
          this.currentUser.set(res.donnees);
        }
      })
    );
  }

  changePassword(ancienMotDePasse: string, nouveauMotDePasse: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/change-password`, {
      ancienMotDePasse,
      nouveauMotDePasse
    });
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('ept_token');
  }

  hasRole(role: string): boolean {
    const user = this.currentUser();
    if (!user) return false;
    return user.role === 'ADMIN' || user.role === role;
  }

  private getStoredUser(): User | null {
    const u = localStorage.getItem('ept_user');
    return u ? JSON.parse(u) : null;
  }
}
