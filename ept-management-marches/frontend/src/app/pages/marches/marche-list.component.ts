import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MarcheService } from '../../services/marche.service';
import { AuthService } from '../../services/auth.service';
import { Marche, StatutMarche } from '../../models/marche.model';

@Component({
  selector: 'app-marche-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h2>Marchés Publics & Lots</h2>
          <p class="subtitle">Gestion, passation et suivi des marchés d'acquisition de l'EPT</p>
        </div>
        <a *ngIf="auth.hasRole('SERVICE_ACHATS')" routerLink="/marches/nouveau" class="btn-primary">
          ➕ Nouveau Marché
        </a>
      </div>

      <!-- Filtres -->
      <div class="filter-bar">
        <input type="text" [(ngModel)]="recherche" (keyup.enter)="chargerMarches()" placeholder="Rechercher par référence, objet..." />
        <select [(ngModel)]="statutFilter" (change)="chargerMarches()">
          <option value="">Tous les statuts</option>
          <option value="BROUILLON">Brouillon</option>
          <option value="EN_COURS">En cours de passation</option>
          <option value="ATTRIBUE">Attribué</option>
          <option value="EN_EXECUTION">En exécution / livraison</option>
          <option value="CLOTURE">Clôturé</option>
        </select>
        <button (click)="chargerMarches()" class="btn-filter">Rechercher</button>
      </div>

      <!-- Liste des marchés -->
      <div class="card table-card">
        <div *ngIf="loading" class="loading-state">Chargement des marchés...</div>
        <div *ngIf="!loading && marches.length === 0" class="empty-state">Aucun marché trouvé.</div>

        <table *ngIf="!loading && marches.length > 0" class="data-table">
          <thead>
            <tr>
              <th>Référence</th>
              <th>Objet du Marché</th>
              <th>Lots</th>
              <th>Budget Prévisionnel</th>
              <th>Montant Engagé</th>
              <th>Statut</th>
              <th class="text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let m of marches">
              <td><strong class="ref-code">{{ m.reference }}</strong></td>
              <td class="obj-cell">{{ m.objet }}</td>
              <td><span class="badge-lots">{{ m.lots?.length || 0 }} lot(s)</span></td>
              <td>{{ m.budgetPrevisionnel ? (m.budgetPrevisionnel | number) + ' FCFA' : '—' }}</td>
              <td><strong class="text-amount">{{ m.montantTotal ? (m.montantTotal | number) + ' FCFA' : '0 FCFA' }}</strong></td>
              <td><span class="badge" [ngClass]="m.statut.toLowerCase()">{{ m.statut }}</span></td>
              <td class="text-right">
                <a [routerLink]="['/marches', m.id]" class="btn-action">Consulter les lots</a>
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
    .btn-primary { background: #2563eb; color: #fff; padding: 10px 18px; border-radius: 8px; font-size: 13px; font-weight: 600; text-decoration: none; }
    .filter-bar { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 14px 20px; display: flex; gap: 14px; }
    .filter-bar input { flex: 1; padding: 8px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 13px; }
    .filter-bar select { padding: 8px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 13px; }
    .btn-filter { background: #0f172a; color: #fff; border: none; padding: 8px 18px; border-radius: 6px; font-weight: 600; cursor: pointer; }
    .table-card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; }
    .data-table { width: 100%; border-collapse: collapse; font-size: 13px; text-align: left; }
    .data-table th { background: #f8fafc; padding: 12px 16px; border-bottom: 1px solid #e2e8f0; color: #475569; font-weight: 700; }
    .data-table td { padding: 14px 16px; border-bottom: 1px solid #f1f5f9; color: #1e293b; }
    .ref-code { color: #d97706; font-weight: 800; }
    .obj-cell { max-width: 280px; font-weight: 500; }
    .badge-lots { background: #f1f5f9; color: #334155; padding: 2px 8px; border-radius: 4px; font-weight: 600; }
    .text-amount { color: #059669; }
    .text-right { text-align: right; }
    .btn-action { background: #f1f5f9; color: #0f172a; text-decoration: none; padding: 6px 12px; border-radius: 6px; font-weight: 600; }
    .badge { padding: 4px 10px; border-radius: 20px; font-size: 11px; font-weight: 700; }
    .brouillon { background: #f1f5f9; color: #475569; }
    .en_cours { background: #fef3c7; color: #92400e; }
    .attribue { background: #e0e7ff; color: #3730a3; }
    .en_execution { background: #dbeafe; color: #1e40af; }
    .cloture { background: #d1fae5; color: #065f46; }
    .loading-state, .empty-state { padding: 40px; text-align: center; color: #64748b; }
  `]
})
export class MarcheListComponent implements OnInit {
  marches: Marche[] = [];
  loading = false;
  recherche = '';
  statutFilter = '';

  constructor(
    private marcheService: MarcheService,
    public auth: AuthService
  ) {}

  ngOnInit(): void {
    this.chargerMarches();
  }

  chargerMarches(): void {
    this.loading = true;
    const st = this.statutFilter ? this.statutFilter as StatutMarche : undefined;
    this.marcheService.listerMarches(this.recherche, st).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.succes && res.donnees) {
          this.marches = res.donnees.contenu;
        }
      },
      error: () => this.loading = false
    });
  }
}
