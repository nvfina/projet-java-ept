import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <aside class="sidebar">
      <div class="sidebar-brand">
        <div class="brand-logo">EPT</div>
        <div class="brand-text">
          <div class="title">MARCHÉS & ÉQUIPEMENTS</div>
          <div class="subtitle">École Polytechnique de Thiès</div>
        </div>
      </div>

      <nav class="sidebar-nav">
        <div class="nav-section-title">MENU PRINCIPAL</div>

        <!-- Tableau de bord -->
        <a routerLink="/dashboard" routerLinkActive="active" class="nav-item">
          <span class="nav-icon">📊</span>
          <span class="nav-label">Tableau de bord</span>
        </a>

        <!-- CHEF_SERVICE et ADMIN -->
        <ng-container *ngIf="auth.hasRole('CHEF_SERVICE') || auth.hasRole('ADMIN')">
          <div class="nav-section-title">EXPRESSIONS DE BESOINS</div>
          <a routerLink="/besoins/nouveau" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">➕</span>
            <span class="nav-label">Nouvelle demande</span>
          </a>
          <a routerLink="/besoins/mes-besoins" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">📋</span>
            <span class="nav-label">Mes demandes</span>
          </a>
        </ng-container>

        <!-- SERVICE_ACHATS, DIRECTION et ADMIN -->
        <ng-container *ngIf="auth.hasRole('SERVICE_ACHATS') || auth.hasRole('ADMIN') || auth.hasRole('DIRECTION')">
          <div class="nav-section-title">GESTION DES MARCHÉS</div>
          <a routerLink="/besoins" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}" class="nav-item">
            <span class="nav-icon">📥</span>
            <span class="nav-label">Toutes les demandes</span>
          </a>
          <a routerLink="/marches" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">🏛️</span>
            <span class="nav-label">Marchés & Lots</span>
          </a>
          <a routerLink="/fournisseurs" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">🏢</span>
            <span class="nav-label">Fournisseurs</span>
          </a>
        </ng-container>

        <!-- DIRECTION et ADMIN -->
        <ng-container *ngIf="auth.hasRole('DIRECTION') || auth.hasRole('ADMIN')">
          <div class="nav-section-title">DÉCISIONNEL & AUDIT</div>
          <a routerLink="/rapports" routerLinkActive="active" class="nav-item">
            <span class="nav-icon">📑</span>
            <span class="nav-label">Rapports & Exports</span>
          </a>
        </ng-container>

        <!-- ASSISTANT CHATBOT POUR TOUS -->
        <div class="nav-section-title">INTELLIGENCE ARTIFICIELLE</div>
        <a routerLink="/chatbot" routerLinkActive="active" class="nav-item nav-highlight">
          <span class="nav-icon">🤖</span>
          <span class="nav-label">Assistant Chatbot</span>
        </a>

        <!-- PROFIL -->
        <div class="nav-section-title">PARAMÈTRES</div>
        <a routerLink="/profil" routerLinkActive="active" class="nav-item">
          <span class="nav-icon">👤</span>
          <span class="nav-label">Mon Profil</span>
        </a>
      </nav>

      <div class="sidebar-footer">
        <div class="user-chip">
          <div class="user-avatar">{{ auth.currentUser()?.prenom?.charAt(0) }}{{ auth.currentUser()?.nom?.charAt(0) }}</div>
          <div class="user-meta">
            <div class="user-name">{{ auth.currentUser()?.prenom }} {{ auth.currentUser()?.nom }}</div>
            <div class="user-badge">{{ auth.currentUser()?.role }}</div>
          </div>
        </div>
      </div>
    </aside>
  `,
  styles: [`
    .sidebar {
      width: 260px;
      height: 100vh;
      background: #0f172a;
      color: #e2e8f0;
      display: flex;
      flex-direction: column;
      border-right: 1px solid #1e293b;
      position: fixed;
      left: 0;
      top: 0;
      z-index: 100;
    }
    .sidebar-brand {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 20px 16px;
      background: #1e293b;
      border-bottom: 1px solid #334155;
    }
    .brand-logo {
      background: #d97706;
      color: #ffffff;
      font-weight: 900;
      font-size: 18px;
      padding: 6px 10px;
      border-radius: 6px;
      letter-spacing: 1px;
    }
    .brand-text .title {
      font-size: 13px;
      font-weight: 700;
      letter-spacing: 0.5px;
      color: #f8fafc;
    }
    .brand-text .subtitle {
      font-size: 11px;
      color: #94a3b8;
    }
    .sidebar-nav {
      flex: 1;
      overflow-y: auto;
      padding: 16px 12px;
      display: flex;
      flex-direction: column;
      gap: 4px;
    }
    .nav-section-title {
      font-size: 10px;
      font-weight: 700;
      color: #64748b;
      margin: 16px 8px 6px 8px;
      letter-spacing: 0.8px;
    }
    .nav-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 10px 12px;
      border-radius: 8px;
      color: #cbd5e1;
      text-decoration: none;
      font-size: 14px;
      font-weight: 500;
      transition: all 0.2s ease;
    }
    .nav-item:hover {
      background: #1e293b;
      color: #ffffff;
    }
    .nav-item.active {
      background: #2563eb;
      color: #ffffff;
    }
    .nav-highlight {
      background: rgba(217, 119, 6, 0.15);
      border: 1px solid rgba(217, 119, 6, 0.3);
      color: #f59e0b;
    }
    .nav-highlight:hover {
      background: rgba(217, 119, 6, 0.25);
    }
    .nav-icon {
      font-size: 16px;
    }
    .sidebar-footer {
      padding: 14px 16px;
      background: #1e293b;
      border-top: 1px solid #334155;
    }
    .user-chip {
      display: flex;
      align-items: center;
      gap: 10px;
    }
    .user-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: #3b82f6;
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 700;
      font-size: 13px;
    }
    .user-meta .user-name {
      font-size: 13px;
      font-weight: 600;
      color: #f8fafc;
    }
    .user-meta .user-badge {
      font-size: 10px;
      font-weight: 600;
      background: #334155;
      color: #cbd5e1;
      padding: 2px 6px;
      border-radius: 4px;
      display: inline-block;
      margin-top: 2px;
    }
  `]
})
export class SidebarComponent {
  constructor(public auth: AuthService) {}
}
