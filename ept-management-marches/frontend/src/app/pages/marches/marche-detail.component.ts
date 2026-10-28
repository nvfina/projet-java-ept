import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { MarcheService } from '../../services/marche.service';
import { AuthService } from '../../services/auth.service';
import { Marche, StatutMarche } from '../../models/marche.model';

@Component({
  selector: 'app-marche-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="page-container" *ngIf="marche">
      <div class="page-header">
        <div>
          <div class="header-badges">
            <span class="ref-badge">{{ marche.reference }}</span>
            <span class="status-badge" [ngClass]="marche.statut.toLowerCase()">{{ marche.statut }}</span>
          </div>
          <h2>{{ marche.objet }}</h2>
          <p class="subtitle">Créé par <strong>{{ marche.createurNom }}</strong> le {{ marche.dateCreation | date:'dd/MM/yyyy' }}</p>
        </div>
        <div class="header-actions">
          <a routerLink="/marches" class="btn-secondary">← Retour</a>
          <div class="status-actions" *ngIf="auth.hasRole('SERVICE_ACHATS') || auth.hasRole('ADMIN')">
            <button *ngIf="marche.statut === 'BROUILLON'" (click)="changerStatut('EN_COURS')" class="btn-primary">
              🚀 Lancer la passation
            </button>
            <button *ngIf="marche.statut === 'EN_EXECUTION' || marche.statut === 'ATTRIBUE'" (click)="changerStatut('CLOTURE')" class="btn-success">
              🏁 Clôturer le marché
            </button>
          </div>
        </div>
      </div>

      <!-- Métriques du marché -->
      <div class="metrics-row">
        <div class="metric-card">
          <div class="label">Budget Prévisionnel</div>
          <div class="value">{{ marche.budgetPrevisionnel ? (marche.budgetPrevisionnel | number) + ' FCFA' : 'Non spécifié' }}</div>
        </div>
        <div class="metric-card">
          <div class="label">Montant Total Engagé</div>
          <div class="value text-success">{{ marche.montantTotal ? (marche.montantTotal | number) + ' FCFA' : '0 FCFA' }}</div>
        </div>
        <div class="metric-card">
          <div class="label">Nombre de Lots</div>
          <div class="value">{{ marche.lots?.length || 0 }}</div>
        </div>
      </div>

      <!-- Description détaillée -->
      <div class="card" *ngIf="marche.description">
        <div class="card-header"><h3>Description & Modalités du Marché</h3></div>
        <div class="card-body"><p>{{ marche.description }}</p></div>
      </div>

      <!-- Liste des lots -->
      <div class="card">
        <div class="card-header">
          <h3>Lots Composant ce Marché</h3>
        </div>
        <div class="lots-list">
          <div *ngFor="let lot of marche.lots" class="lot-row">
            <div class="lot-main-info">
              <div class="lot-title">
                <span class="lot-num">Lot n°{{ lot.numero }}</span>
                <strong>{{ lot.designation }}</strong>
                <span class="badge" [ngClass]="lot.statut.toLowerCase()">{{ lot.statut }}</span>
              </div>

              <div class="lot-meta">
                <span>Prestataire : <strong>{{ lot.fournisseurNom || 'Non attribué' }}</strong></span> | 
                <span>Montant attribué : <strong>{{ lot.montantAttribue ? (lot.montantAttribue | number) + ' FCFA' : '—' }}</strong></span> | 
                <span>Livraison prévue : {{ lot.dateLivraisonPrevue ? (lot.dateLivraisonPrevue | date:'dd/MM/yyyy') : '—' }}</span>
              </div>

              <!-- Taux d'avancement -->
              <div class="progress-section">
                <div class="progress-meta">
                  <span>Taux de livraison :</span>
                  <strong>{{ lot.tauxExecution || 0 }}%</strong>
                </div>
                <div class="progress-bar">
                  <div class="progress-fill" [style.width.%]="lot.tauxExecution || 0"></div>
                </div>
              </div>
            </div>

            <div class="lot-actions">
              <a [routerLink]="['/lots', lot.id]" class="btn-lot-action">
                🔍 Suivi & Offres
              </a>
              <a *ngIf="lot.statut === 'EN_ATTENTE' && (auth.hasRole('SERVICE_ACHATS') || auth.hasRole('ADMIN'))"
                 [routerLink]="['/lots', lot.id, 'attribuer']" class="btn-lot-attrib">
                🏆 Attribuer
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-container { display: flex; flex-direction: column; gap: 20px; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; }
    .header-badges { display: flex; gap: 8px; margin-bottom: 8px; }
    .ref-badge { background: #fef3c7; color: #92400e; font-weight: 800; font-size: 13px; padding: 4px 10px; border-radius: 6px; }
    .status-badge { font-size: 11px; font-weight: 700; padding: 4px 10px; border-radius: 20px; }
    .brouillon { background: #f1f5f9; color: #475569; }
    .en_cours { background: #fef3c7; color: #92400e; }
    .attribue { background: #e0e7ff; color: #3730a3; }
    .en_execution { background: #dbeafe; color: #1e40af; }
    .cloture { background: #d1fae5; color: #065f46; }
    .page-header h2 { margin: 0 0 6px 0; font-size: 22px; color: #0f172a; }
    .subtitle { margin: 0; font-size: 13px; color: #64748b; }
    .header-actions { display: flex; gap: 10px; align-items: center; }
    .status-actions { display: flex; gap: 8px; }
    .btn-secondary { background: #f1f5f9; color: #334155; padding: 8px 16px; border-radius: 6px; font-size: 13px; text-decoration: none; font-weight: 600; }
    .btn-primary { background: #2563eb; color: #fff; border: none; padding: 8px 16px; border-radius: 6px; font-size: 13px; font-weight: 600; cursor: pointer; }
    .btn-success { background: #059669; color: #fff; border: none; padding: 8px 16px; border-radius: 6px; font-size: 13px; font-weight: 600; cursor: pointer; }
    .metrics-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
    .metric-card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 18px; }
    .metric-card .label { font-size: 12px; color: #64748b; font-weight: 600; }
    .metric-card .value { font-size: 20px; font-weight: 800; color: #0f172a; margin-top: 4px; }
    .text-success { color: #059669 !important; }
    .card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; }
    .card-header { padding: 16px 20px; border-bottom: 1px solid #f1f5f9; }
    .card-header h3 { margin: 0; font-size: 16px; color: #0f172a; }
    .card-body { padding: 20px; font-size: 13px; color: #334155; }
    .lots-list { display: flex; flex-direction: column; }
    .lot-row { display: flex; justify-content: space-between; align-items: center; padding: 20px; border-bottom: 1px solid #f1f5f9; }
    .lot-row:last-child { border-bottom: none; }
    .lot-main-info { flex: 1; }
    .lot-title { display: flex; align-items: center; gap: 10px; font-size: 15px; color: #0f172a; margin-bottom: 6px; }
    .lot-num { background: #f1f5f9; color: #334155; font-size: 11px; font-weight: 700; padding: 2px 8px; border-radius: 4px; }
    .badge { font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 20px; }
    .lot-meta { font-size: 12px; color: #64748b; margin-bottom: 10px; }
    .progress-section { max-width: 320px; }
    .progress-meta { display: flex; justify-content: space-between; font-size: 11px; margin-bottom: 4px; color: #475569; }
    .progress-bar { height: 6px; background: #e2e8f0; border-radius: 3px; overflow: hidden; }
    .progress-fill { height: 100%; background: #10b981; }
    .lot-actions { display: flex; gap: 8px; }
    .btn-lot-action { background: #f1f5f9; color: #0f172a; text-decoration: none; padding: 8px 14px; border-radius: 6px; font-size: 12px; font-weight: 600; }
    .btn-lot-attrib { background: #d97706; color: #fff; text-decoration: none; padding: 8px 14px; border-radius: 6px; font-size: 12px; font-weight: 700; }
  `]
})
export class MarcheDetailComponent implements OnInit {
  marche: Marche | null = null;

  constructor(
    private route: ActivatedRoute,
    private marcheService: MarcheService,
    public auth: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.chargerMarche(id);
    }
  }

  chargerMarche(id: number): void {
    this.marcheService.trouverMarcheParId(id).subscribe({
      next: (res) => {
        if (res.succes && res.donnees) {
          this.marche = res.donnees;
        }
      }
    });
  }

  changerStatut(statut: StatutMarche): void {
    if (!this.marche) return;
    this.marcheService.changerStatutMarche(this.marche.id, statut).subscribe({
      next: () => this.chargerMarche(this.marche!.id)
    });
  }
}
