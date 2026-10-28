import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { DashboardService } from '../../services/dashboard.service';
import { DashboardData, DepenseService, TopFournisseur } from '../../models/dashboard.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="dashboard-page">
      <!-- En-tête de bienvenue personnalisé -->
      <div class="welcome-banner">
        <div class="welcome-text">
          <h1>Bienvenue, {{ auth.currentUser()?.prenom }} {{ auth.currentUser()?.nom }}</h1>
          <p>
            Rôle : <strong>{{ auth.currentUser()?.role }}</strong> | 
            Service : <strong>{{ auth.currentUser()?.serviceNom || 'Direction' }}</strong>
          </p>
        </div>
        <div class="welcome-actions">
          <a *ngIf="auth.hasRole('CHEF_SERVICE')" routerLink="/besoins/nouveau" class="btn-primary">
            ➕ Nouvelle Expression de Besoin
          </a>
          <a *ngIf="auth.hasRole('SERVICE_ACHATS')" routerLink="/marches" class="btn-primary">
            🏛️ Gérer les Marchés
          </a>
          <a *ngIf="auth.hasRole('DIRECTION')" routerLink="/rapports" class="btn-primary">
            📑 Exporter les Rapports
          </a>
        </div>
      </div>

      <!-- Indicateurs décisionnels (KPIs) -->
      <div class="kpi-grid">
        <div class="kpi-card">
          <div class="kpi-icon blue">📋</div>
          <div class="kpi-content">
            <div class="kpi-value">{{ data?.totalDemandes ?? 0 }}</div>
            <div class="kpi-label">Demandes de matériels</div>
            <div class="kpi-subtext">
              <span class="text-warning">{{ data?.demandesEnAttente ?? 0 }} en attente</span> | 
              <span class="text-success">{{ data?.demandesValidees ?? 0 }} validées</span>
            </div>
          </div>
        </div>

        <div class="kpi-card">
          <div class="kpi-icon amber">🏛️</div>
          <div class="kpi-content">
            <div class="kpi-value">{{ data?.totalMarches ?? 0 }}</div>
            <div class="kpi-label">Marchés publics</div>
            <div class="kpi-subtext">
              <span>{{ data?.marchesEnCours ?? 0 }} en cours</span> | 
              <span>{{ data?.marchesClotures ?? 0 }} clôturés</span>
            </div>
          </div>
        </div>

        <div class="kpi-card">
          <div class="kpi-icon emerald">📦</div>
          <div class="kpi-content">
            <div class="kpi-value">{{ data?.totalLots ?? 0 }}</div>
            <div class="kpi-label">Lots constitués</div>
            <div class="kpi-subtext">
              <span>{{ data?.lotsAttribues ?? 0 }} attribués</span> | 
              <span>{{ data?.lotsEnCoursLivraison ?? 0 }} en livraison</span>
            </div>
          </div>
        </div>

        <div class="kpi-card">
          <div class="kpi-icon purple">📈</div>
          <div class="kpi-content">
            <div class="kpi-value">{{ data?.tauxExecutionGlobal ?? 0 }}%</div>
            <div class="kpi-label">Taux d'exécution global</div>
            <div class="progress-bar">
              <div class="progress-fill" [style.width.%]="data?.tauxExecutionGlobal || 0"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Graphiques et Répartitions -->
      <div class="analytics-row">
        <!-- Répartition des dépenses par service -->
        <div class="analytics-card">
          <div class="card-header">
            <h3>Dépenses & Besoins par Département</h3>
            <span class="card-tag">Données certifiées</span>
          </div>
          <div class="card-body">
            <div *ngIf="depensesServices.length === 0" class="empty-state">
              Aucune dépense enregistrée.
            </div>
            <div *ngFor="let s of depensesServices" class="service-bar-item">
              <div class="service-bar-meta">
                <span class="service-name">{{ s.serviceNom }}</span>
                <span class="service-amount">{{ s.montantTotal | number }} FCFA ({{ s.nombreDemandes }} dem.)</span>
              </div>
              <div class="bar-container">
                <div class="bar-fill" [style.width.%]="calculerPourcentage(s.montantTotal)"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Principaux Prestataires -->
        <div class="analytics-card">
          <div class="card-header">
            <h3>Principaux Fournisseurs Attributaires</h3>
            <span class="card-tag">Volume d'affaires</span>
          </div>
          <div class="card-body">
            <div class="supplier-list">
              <div *ngFor="let f of topFournisseurs" class="supplier-item">
                <div class="supplier-avatar">🏢</div>
                <div class="supplier-info">
                  <div class="name">{{ f.fournisseurNom }}</div>
                  <div class="lots">{{ f.nombreLots }} lot(s) attribué(s)</div>
                </div>
                <div class="supplier-amount">{{ f.montantTotal | number }} FCFA</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Raccourci vers le Chatbot IA -->
      <div class="chatbot-callout">
        <div class="callout-content">
          <div class="callout-icon">🤖</div>
          <div>
            <h4>Une question sur les marchés ou un fournisseur ?</h4>
            <p>Interrogez l'assistant conversationnel intelligent en langage naturel.</p>
          </div>
        </div>
        <a routerLink="/chatbot" class="btn-callout">Poser une question</a>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-page {
      display: flex;
      flex-direction: column;
      gap: 24px;
    }
    .welcome-banner {
      background: #ffffff;
      border: 1px solid #e2e8f0;
      border-radius: 12px;
      padding: 24px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .welcome-text h1 {
      font-size: 22px;
      font-weight: 700;
      color: #0f172a;
      margin: 0 0 6px 0;
    }
    .welcome-text p {
      margin: 0;
      font-size: 13px;
      color: #64748b;
    }
    .btn-primary {
      background: #2563eb;
      color: #fff;
      padding: 10px 18px;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 600;
      text-decoration: none;
      display: inline-flex;
      align-items: center;
      gap: 8px;
      transition: background 0.2s;
    }
    .btn-primary:hover {
      background: #1d4ed8;
    }
    .kpi-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: 20px;
    }
    .kpi-card {
      background: #ffffff;
      border: 1px solid #e2e8f0;
      border-radius: 12px;
      padding: 20px;
      display: flex;
      align-items: center;
      gap: 16px;
    }
    .kpi-icon {
      width: 52px;
      height: 52px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
    }
    .kpi-icon.blue { background: #eff6ff; }
    .kpi-icon.amber { background: #fffbeb; }
    .kpi-icon.emerald { background: #ecfdf5; }
    .kpi-icon.purple { background: #faf5ff; }

    .kpi-content {
      flex: 1;
    }
    .kpi-value {
      font-size: 26px;
      font-weight: 800;
      color: #0f172a;
      line-height: 1.1;
    }
    .kpi-label {
      font-size: 13px;
      font-weight: 600;
      color: #64748b;
      margin: 4px 0 6px 0;
    }
    .kpi-subtext {
      font-size: 11px;
      font-weight: 500;
      color: #94a3b8;
    }
    .text-warning { color: #d97706; font-weight: 600; }
    .text-success { color: #059669; font-weight: 600; }

    .progress-bar {
      height: 6px;
      background: #e2e8f0;
      border-radius: 4px;
      overflow: hidden;
      margin-top: 6px;
    }
    .progress-fill {
      height: 100%;
      background: #8b5cf6;
      border-radius: 4px;
    }

    .analytics-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 24px;
    }
    .analytics-card {
      background: #ffffff;
      border: 1px solid #e2e8f0;
      border-radius: 12px;
      padding: 24px;
    }
    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 20px;
    }
    .card-header h3 {
      font-size: 16px;
      font-weight: 700;
      color: #0f172a;
      margin: 0;
    }
    .card-tag {
      font-size: 11px;
      font-weight: 600;
      background: #f1f5f9;
      color: #475569;
      padding: 4px 8px;
      border-radius: 4px;
    }
    .service-bar-item {
      margin-bottom: 14px;
    }
    .service-bar-meta {
      display: flex;
      justify-content: space-between;
      font-size: 13px;
      margin-bottom: 6px;
    }
    .service-name { font-weight: 600; color: #1e293b; }
    .service-amount { color: #64748b; }
    .bar-container {
      height: 8px;
      background: #f1f5f9;
      border-radius: 4px;
      overflow: hidden;
    }
    .bar-fill {
      height: 100%;
      background: #3b82f6;
      border-radius: 4px;
    }

    .supplier-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 12px 0;
      border-bottom: 1px solid #f1f5f9;
    }
    .supplier-item:last-child { border-bottom: none; }
    .supplier-avatar {
      font-size: 20px;
      margin-right: 12px;
    }
    .supplier-info { flex: 1; }
    .supplier-info .name { font-size: 13px; font-weight: 600; color: #0f172a; }
    .supplier-info .lots { font-size: 11px; color: #64748b; }
    .supplier-amount { font-size: 13px; font-weight: 700; color: #059669; }

    .chatbot-callout {
      background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
      border: 1px solid #fcd34d;
      border-radius: 12px;
      padding: 20px 24px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .callout-content {
      display: flex;
      align-items: center;
      gap: 16px;
    }
    .callout-icon { font-size: 32px; }
    .callout-content h4 { margin: 0 0 4px 0; font-size: 15px; color: #78350f; }
    .callout-content p { margin: 0; font-size: 13px; color: #92400e; }
    .btn-callout {
      background: #78350f;
      color: #fff;
      padding: 10px 18px;
      border-radius: 8px;
      font-size: 13px;
      font-weight: 600;
      text-decoration: none;
      white-space: nowrap;
    }
    .btn-callout:hover { background: #451a03; }
  `]
})
export class DashboardComponent implements OnInit {
  data: DashboardData | null = null;
  depensesServices: DepenseService[] = [];
  topFournisseurs: TopFournisseur[] = [];

  constructor(
    public auth: AuthService,
    private dashboardService: DashboardService
  ) {}

  ngOnInit(): void {
    this.chargerIndicateurs();
  }

  chargerIndicateurs(): void {
    this.dashboardService.getIndicateurs().subscribe({
      next: (res) => {
        if (res.succes && res.donnees) {
          this.data = res.donnees;
        }
      },
      error: () => {}
    });

    this.dashboardService.getDepensesParService().subscribe({
      next: (res) => {
        if (res.succes && res.donnees) {
          this.depensesServices = res.donnees;
        }
      },
      error: () => {}
    });

    this.dashboardService.getTopFournisseurs().subscribe({
      next: (res) => {
        if (res.succes && res.donnees) {
          this.topFournisseurs = res.donnees;
        }
      },
      error: () => {}
    });
  }

  calculerPourcentage(montant: number): number {
    const total = this.depensesServices.reduce((acc, s) => acc + s.montantTotal, 0);
    if (!total || total === 0) return 0;
    return Math.min(100, Math.round((montant / total) * 100));
  }
}
