import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-forbidden',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="forbidden-container">
      <div class="forbidden-card">
        <div class="forbidden-icon">🚫</div>
        <h1>403 — Accès Non Autorisé</h1>
        <p class="description">
          Vous n'avez pas les habilitations requises pour accéder à cette ressource.
          Le Système de Gestion des Marchés de l'EPT applique un contrôle strict des accès par profil métier.
        </p>
        <div class="actions">
          <a routerLink="/dashboard" class="btn-primary">Retour au tableau de bord</a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .forbidden-container {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 70vh;
      padding: 20px;
    }
    .forbidden-card {
      background: #ffffff;
      border: 1px solid #e2e8f0;
      border-radius: 16px;
      padding: 40px;
      max-width: 520px;
      text-align: center;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    }
    .forbidden-icon {
      font-size: 54px;
      margin-bottom: 16px;
    }
    h1 {
      font-size: 22px;
      color: #0f172a;
      margin: 0 0 12px 0;
    }
    .description {
      color: #64748b;
      font-size: 14px;
      line-height: 1.6;
      margin: 0 0 24px 0;
    }
    .btn-primary {
      display: inline-block;
      background: #2563eb;
      color: #ffffff;
      padding: 10px 22px;
      border-radius: 8px;
      font-weight: 600;
      font-size: 14px;
      text-decoration: none;
      transition: background 0.2s;
    }
    .btn-primary:hover {
      background: #1d4ed8;
    }
  `]
})
export class ForbiddenComponent {}
