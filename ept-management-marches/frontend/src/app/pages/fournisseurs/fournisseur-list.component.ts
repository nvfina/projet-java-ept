import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FournisseurService } from '../../services/fournisseur.service';
import { AuthService } from '../../services/auth.service';
import { Fournisseur } from '../../models/fournisseur.model';

@Component({
  selector: 'app-fournisseur-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h2>Référentiel des Fournisseurs</h2>
          <p class="subtitle">Annuaire et agrément des entreprises prestataires de l'EPT</p>
        </div>
        <a *ngIf="auth.hasRole('SERVICE_ACHATS') || auth.hasRole('ADMIN')" routerLink="/fournisseurs/nouveau" class="btn-primary">
          ➕ Nouveau Fournisseur
        </a>
      </div>

      <!-- Barre de recherche multicritères -->
      <div class="filter-bar">
        <input type="text" [(ngModel)]="rechercheNom" (keyup.enter)="rechercher()" placeholder="Raison sociale..." class="flex-2" />
        <input type="text" [(ngModel)]="rechercheNinea" (keyup.enter)="rechercher()" placeholder="NINEA..." class="flex-1" />
        <input type="text" [(ngModel)]="rechercheDomaine" (keyup.enter)="rechercher()" placeholder="Domaine (ex: Informatique, BTP...)" class="flex-1" />
        <button (click)="rechercher()" class="btn-filter">Rechercher</button>
      </div>

      <!-- Tableau des fournisseurs -->
      <div class="card table-card">
        <div *ngIf="loading" class="loading-state">Chargement des fournisseurs...</div>
        <div *ngIf="!loading && fournisseurs.length === 0" class="empty-state">Aucun fournisseur trouvé.</div>

        <table *ngIf="!loading && fournisseurs.length > 0" class="data-table">
          <thead>
            <tr>
              <th>Raison Sociale</th>
              <th>NINEA</th>
              <th>Domaine d'intervention</th>
              <th>Téléphone</th>
              <th>E-mail</th>
              <th>Statut</th>
              <th class="text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let f of fournisseurs">
              <td><strong>{{ f.raisonSociale }}</strong></td>
              <td><code>{{ f.ninea || '—' }}</code></td>
              <td><span class="badge-domaine">{{ f.domaineIntervention }}</span></td>
              <td>{{ f.telephone }}</td>
              <td>{{ f.email || '—' }}</td>
              <td>
                <span class="badge" [ngClass]="f.actif ? 'badge-actif' : 'badge-inactif'">
                  {{ f.actif ? 'ACTIF' : 'DÉSACTIVÉ' }}
                </span>
              </td>
              <td class="text-right">
                <button *ngIf="f.actif && (auth.hasRole('SERVICE_ACHATS') || auth.hasRole('ADMIN'))"
                        (click)="desactiver(f)" class="btn-toggle-off" title="Désactiver pour nouvelles attributions">
                  Désactiver
                </button>
                <button *ngIf="!f.actif && (auth.hasRole('SERVICE_ACHATS') || auth.hasRole('ADMIN'))"
                        (click)="activer(f)" class="btn-toggle-on" title="Réactiver le fournisseur">
                  Activer
                </button>
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
    .filter-bar { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 14px 20px; display: flex; gap: 12px; }
    .flex-1 { flex: 1; }
    .flex-2 { flex: 2; }
    .filter-bar input { padding: 8px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 13px; outline: none; }
    .btn-filter { background: #0f172a; color: #fff; border: none; padding: 8px 18px; border-radius: 6px; font-weight: 600; cursor: pointer; }
    .table-card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; }
    .data-table { width: 100%; border-collapse: collapse; font-size: 13px; text-align: left; }
    .data-table th { background: #f8fafc; padding: 12px 16px; border-bottom: 1px solid #e2e8f0; color: #475569; font-weight: 700; }
    .data-table td { padding: 14px 16px; border-bottom: 1px solid #f1f5f9; color: #1e293b; }
    .badge-domaine { background: #f1f5f9; color: #334155; padding: 2px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
    .badge { padding: 3px 8px; border-radius: 20px; font-size: 10px; font-weight: 800; }
    .badge-actif { background: #dcfce7; color: #15803d; }
    .badge-inactif { background: #fee2e2; color: #991b1b; }
    .text-right { text-align: right; }
    .btn-toggle-off { background: #fff1f2; color: #e11d48; border: 1px solid #fecdd3; padding: 5px 10px; border-radius: 6px; font-size: 11px; font-weight: 700; cursor: pointer; }
    .btn-toggle-on { background: #f0fdf4; color: #16a34a; border: 1px solid #bbf7d0; padding: 5px 10px; border-radius: 6px; font-size: 11px; font-weight: 700; cursor: pointer; }
    .loading-state, .empty-state { padding: 40px; text-align: center; color: #64748b; font-size: 14px; }
  `]
})
export class FournisseurListComponent implements OnInit {
  fournisseurs: Fournisseur[] = [];
  loading = false;
  rechercheNom = '';
  rechercheNinea = '';
  rechercheDomaine = '';

  constructor(
    private fournisseurService: FournisseurService,
    public auth: AuthService
  ) {}

  ngOnInit(): void {
    this.rechercher();
  }

  rechercher(): void {
    this.loading = true;
    const criteres = {
      raisonSociale: this.rechercheNom,
      ninea: this.rechercheNinea,
      domaineIntervention: this.rechercheDomaine
    };

    this.fournisseurService.rechercher(criteres).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.succes && res.donnees) {
          this.fournisseurs = res.donnees.contenu;
        }
      },
      error: () => this.loading = false
    });
  }

  activer(f: Fournisseur): void {
    this.fournisseurService.activer(f.id).subscribe({
      next: () => this.rechercher()
    });
  }

  desactiver(f: Fournisseur): void {
    if (!confirm(`Désactiver le fournisseur "${f.raisonSociale}" ? Il ne sera plus sélectionnable pour de nouvelles attributions.`)) return;
    this.fournisseurService.desactiver(f.id).subscribe({
      next: () => this.rechercher()
    });
  }
}
