import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <header class="header">
      <div class="header-left">
        <span class="institution-name">République du Sénégal — Ministère de l'Enseignement Supérieur, de la Recherche et de l'Innovation</span>
      </div>
      <div class="header-right">
        <div class="service-pill" *ngIf="auth.currentUser()?.serviceNom">
          <span class="dot"></span>
          {{ auth.currentUser()?.serviceNom }}
        </div>
        <a routerLink="/chatbot" class="btn-assistant" title="Ouvrir l'assistant IA">
          🤖 Assistant Chatbot
        </a>
        <button (click)="auth.logout()" class="btn-logout" title="Se déconnecter">
          <span>Déconnexion</span>
          <span class="icon">🚪</span>
        </button>
      </div>
    </header>
  `,
  styles: [`
    .header {
      height: 60px;
      background: #ffffff;
      border-bottom: 1px solid #e2e8f0;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 24px;
      position: sticky;
      top: 0;
      z-index: 90;
    }
    .institution-name {
      font-size: 12px;
      font-weight: 600;
      color: #64748b;
      letter-spacing: 0.3px;
    }
    .header-right {
      display: flex;
      align-items: center;
      gap: 14px;
    }
    .service-pill {
      display: flex;
      align-items: center;
      gap: 6px;
      background: #f1f5f9;
      color: #334155;
      font-size: 12px;
      font-weight: 600;
      padding: 6px 12px;
      border-radius: 20px;
    }
    .service-pill .dot {
      width: 8px;
      height: 8px;
      background: #10b981;
      border-radius: 50%;
    }
    .btn-assistant {
      background: #fffbeb;
      color: #b45309;
      border: 1px solid #fde68a;
      padding: 6px 12px;
      border-radius: 6px;
      font-size: 13px;
      font-weight: 600;
      text-decoration: none;
      transition: all 0.2s ease;
    }
    .btn-assistant:hover {
      background: #fef3c7;
    }
    .btn-logout {
      background: transparent;
      border: 1px solid #cbd5e1;
      color: #475569;
      padding: 6px 12px;
      border-radius: 6px;
      font-size: 13px;
      font-weight: 500;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 6px;
      transition: all 0.2s ease;
    }
    .btn-logout:hover {
      background: #fee2e2;
      border-color: #fca5a5;
      color: #b91c1c;
    }
  `]
})
export class HeaderComponent {
  constructor(public auth: AuthService) {}
}
