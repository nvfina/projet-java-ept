import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BesoinService } from '../../services/besoin.service';
import { AuthService } from '../../services/auth.service';
import { ExpressionBesoin, StatutBesoin } from '../../models/besoin.model';

@Component({
  selector: 'app-besoin-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h2>Expressions de Besoins</h2>
          <p class="subtitle">Gestion et suivi des demandes de matériels et équipements des départements</p>
        </div>
        <a *ngIf="auth.hasRole('CHEF_SERVICE')" routerLink="/besoins/nouveau" class="btn-primary">
          ➕ Nouvelle Demande
        </a>
      </div>

      <!-- Barre de filtres -->
      <div class="filter-bar">
        <div class="filter-group">
          <label>Filtrer par statut :</label>
          <select [(ngModel)]="selectedStatut" (change)="chargerBesoins()">
            <option value="">Tous les statuts</option>
            <option value="EN_ATTENTE">En attente de validation</option>
            <option value="VALIDE">Validées</option>
            <option value="REJETE">Rejetées</option>
            <option value="EN_COURS_ACQUISITION">En cours d'acquisition</option>
          </select>
        </div>
        <div class="filter-group">
          <label>Service :</label>
          <input type="text" [(ngModel)]="serviceFilter" (keyup.enter)="chargerBesoins()" placeholder="Rechercher par service..." />
        </div>
        <button (click)="chargerBesoins()" class="btn-filter">Filtrer</button>
      </div>

      <!-- Tableau des besoins -->
      <div class="card table-card">
        <div *ngIf="loading" class="loading-state">
          Chargement des demandes en cours...
        </div>

        <div *ngIf="!loading && besoins.length === 0" class="empty-state">
          <p>Aucune expression de besoin trouvée avec ces critères.</p>
        </div>

        <table *ngIf="!loading && besoins.length > 0" class="data-table">
          <thead>
            <tr>
              <th>Référence</th>
              <th>Service Demandeur</th>
              <th>Demandeur</th>
              <th>Description sommaire</th>
              <th>Date d'émission</th>
              <th>Statut</th>
              <th class="text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let b of besoins">
              <td><strong class="ref-code">{{ b.reference }}</strong></td>
              <td>{{ b.serviceDemandeur }}</td>
              <td>{{ b.demandeurNom }}</td>
              <td class="desc-cell">{{ b.description }}</td>
              <td>{{ b.dateCreation | date:'dd/MM/yyyy HH:mm' }}</td>
              <td>
                <span class="badge" [ngClass]="getStatutClass(b.statut)">
                  {{ getStatutLibelle(b.statut) }}
                </span>
                <span *ngIf="b.urgente" class="badge-urgent">URGENT</span>
              </td>
              <td class="text-right">
                <a [routerLink]="['/besoins', b.id]" class="btn-action">Consulter</a>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .page-container { display: flex; flex-direction: column; gap: 20px; }
    .page-header { display: flex; justify-content: space-between; align-items: center; }
    .page-header h2 { margin: 0; font-size: 22px; color: #0f172a; }
    .subtitle { margin: 4px 0 0 0; font-size: 13px; color: #64748b; }
    .btn-primary {
      background: #2563eb; color: #fff; padding: 10px 18px; border-radius: 8px;
      font-size: 13px; font-weight: 600; text-decoration: none;
    }
    .filter-bar {
      background: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px;
      padding: 14px 20px; display: flex; align-items: center; gap: 20px;
    }
    .filter-group { display: flex; align-items: center; gap: 8px; font-size: 13px; font-weight: 600; }
    .filter-group select, .filter-group input {
      padding: 8px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 13px; outline: none;
    }
    .btn-filter {
      background: #0f172a; color: #fff; border: none; padding: 8px 16px;
      border-radius: 6px; font-size: 13px; cursor: pointer;
    }
    .table-card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; }
    .data-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 13px; }
    .data-table th { background: #f8fafc; padding: 12px 16px; border-bottom: 1px solid #e2e8f0; color: #475569; font-weight: 700; }
    .data-table td { padding: 14px 16px; border-bottom: 1px solid #f1f5f9; color: #1e293b; }
    .ref-code { color: #2563eb; }
    .desc-cell { max-width: 250px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .text-right { text-align: right; }
    .badge {
      padding: 4px 10px; border-radius: 20px; font-size: 11px; font-weight: 700; display: inline-block;
    }
    .badge-attente { background: #fef3c7; color: #92400e; }
    .badge-valide { background: #d1fae5; color: #065f46; }
    .badge-rejete { background: #fee2e2; color: #991b1b; }
    .badge-acquisition { background: #e0e7ff; color: #3730a3; }
    .badge-urgent { background: #ef4444; color: #fff; padding: 2px 6px; border-radius: 4px; font-size: 9px; font-weight: 800; margin-left: 6px; }
    .btn-action {
      background: #f1f5f9; color: #0f172a; text-decoration: none; padding: 6px 12px; border-radius: 6px; font-weight: 600;
    }
    .btn-action:hover { background: #e2e8f0; }
    .loading-state, .empty-state { padding: 40px; text-align: center; color: #64748b; font-size: 14px; }
  `]
})
export class BesoinListComponent implements OnInit {
  besoins: ExpressionBesoin[] = [];
  loading = false;
  selectedStatut = '';
  serviceFilter = '';

  constructor(
    private besoinService: BesoinService,
    public auth: AuthService
  ) {}

  ngOnInit(): void {
    this.chargerBesoins();
  }

  chargerBesoins(): void {
    this.loading = true;
    const statut = this.selectedStatut ? this.selectedStatut as StatutBesoin : undefined;
    const service = this.serviceFilter ? this.serviceFilter : undefined;

    this.besoinService.lister(service, statut).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.succes && res.donnees) {
          this.besoins = res.donnees.contenu;
        }
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  getStatutClass(statut: StatutBesoin): string {
    switch (statut) {
      case 'EN_ATTENTE': return 'badge-attente';
      case 'VALIDE': return 'badge-valide';
      case 'REJETE': return 'badge-rejete';
      case 'EN_COURS_ACQUISITION': return 'badge-acquisition';
      default: return '';
    }
  }

  getStatutLibelle(statut: StatutBesoin): string {
    switch (statut) {
      case 'EN_ATTENTE': return 'En attente';
      case 'VALIDE': return 'Validée';
      case 'REJETE': return 'Rejetée';
      case 'EN_COURS_ACQUISITION': return 'En cours';
      case 'CLOTURE': return 'Clôturée';
      default: return statut;
    }
  }
}
