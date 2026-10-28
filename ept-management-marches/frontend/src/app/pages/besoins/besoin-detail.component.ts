import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BesoinService } from '../../services/besoin.service';
import { AuthService } from '../../services/auth.service';
import { ExpressionBesoin, HistoriqueStatut } from '../../models/besoin.model';

@Component({
  selector: 'app-besoin-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="page-container" *ngIf="besoin">
      <div class="page-header">
        <div>
          <div class="header-badges">
            <span class="ref-badge">{{ besoin.reference }}</span>
            <span class="status-badge" [ngClass]="besoin.statut.toLowerCase()">{{ besoin.statut }}</span>
            <span *ngIf="besoin.urgente" class="urgent-badge">URGENT</span>
          </div>
          <h2>{{ besoin.description }}</h2>
          <p class="subtitle">Émis par <strong>{{ besoin.demandeurNom }}</strong> (Département : {{ besoin.serviceDemandeur }}) le {{ besoin.dateCreation | date:'dd/MM/yyyy HH:mm' }}</p>
        </div>
        <a routerLink="/besoins" class="btn-secondary">← Retour</a>
      </div>

      <!-- Actions de Décision pour Service des Achats -->
      <div *ngIf="besoin.statut === 'EN_ATTENTE' && (auth.hasRole('SERVICE_ACHATS') || auth.hasRole('ADMIN'))" class="decision-card">
        <h3>Traitement de la Demande — Service des Achats</h3>
        <p>Veuillez statuer sur cette expression de besoin afin de permettre son intégration dans un marché public.</p>
        
        <div class="form-group">
          <label>Commentaire de décision (optionnel) :</label>
          <input type="text" [(ngModel)]="commentaireDecision" placeholder="Ex: Validé après arbitrage budgétaire..." />
        </div>

        <div class="decision-buttons">
          <button (click)="validerDemande()" [disabled]="processing" class="btn-valider">
            ✅ Valider cette Demande
          </button>
          <button (click)="rejeterDemande()" [disabled]="processing" class="btn-rejeter">
            ❌ Rejeter cette Demande
          </button>
        </div>
      </div>

      <!-- Action d'annulation et modification pour le Chef de Service propriétaire -->
      <div *ngIf="besoin.statut === 'EN_ATTENTE' && auth.hasRole('CHEF_SERVICE')" class="owner-actions">
        <a [routerLink]="['/besoins', besoin.id, 'modifier']" class="btn-edit">
          ✏️ Modifier cette demande
        </a>
        <button (click)="annulerDemande()" class="btn-danger">
          🗑️ Annuler cette demande
        </button>
      </div>

      <!-- Tableau des articles demandés -->
      <div class="card">
        <div class="card-header">
          <h3>Articles et Matériels Demandés</h3>
          <span class="count-badge">{{ besoin.lignes.length }} article(s)</span>
        </div>
        <table class="data-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Désignation</th>
              <th>Description / Spécifications</th>
              <th>Quantité</th>
              <th>Unité</th>
              <th class="text-right">Prix Unitaire Estimé</th>
              <th class="text-right">Total Estimé</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let l of besoin.lignes; let i = index">
              <td>{{ i + 1 }}</td>
              <td><strong>{{ l.designation }}</strong></td>
              <td>{{ l.description || '—' }}</td>
              <td>{{ l.quantiteDemandee }}</td>
              <td>{{ l.unite || 'unité' }}</td>
              <td class="text-right">{{ l.estimationPrix ? (l.estimationPrix | number) + ' FCFA' : '—' }}</td>
              <td class="text-right">
                <strong>{{ l.estimationPrix ? (l.estimationPrix * l.quantiteDemandee | number) + ' FCFA' : '—' }}</strong>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Historique d'audit des statuts -->
      <div class="card">
        <div class="card-header">
          <h3>Historique et Traçabilité des Décisions</h3>
        </div>
        <div class="timeline">
          <div *ngFor="let h of historique" class="timeline-item">
            <div class="timeline-dot"></div>
            <div class="timeline-content">
              <div class="timeline-title">
                Passage au statut <strong>{{ h.nouveauStatut }}</strong> par <em>{{ h.utilisateurNom }}</em>
              </div>
              <div class="timeline-date">{{ h.dateChangement | date:'dd/MM/yyyy HH:mm:ss' }}</div>
              <div *ngIf="h.commentaire" class="timeline-comment">« {{ h.commentaire }} »</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-container { display: flex; flex-direction: column; gap: 20px; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; }
    .header-badges { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
    .ref-badge { background: #eff6ff; color: #2563eb; font-weight: 800; font-size: 13px; padding: 4px 10px; border-radius: 6px; }
    .status-badge { font-size: 12px; font-weight: 700; padding: 4px 10px; border-radius: 20px; }
    .en_attente { background: #fef3c7; color: #92400e; }
    .valide { background: #d1fae5; color: #065f46; }
    .rejete { background: #fee2e2; color: #991b1b; }
    .en_cours_acquisition { background: #e0e7ff; color: #3730a3; }
    .urgent-badge { background: #ef4444; color: #fff; font-size: 10px; font-weight: 800; padding: 2px 6px; border-radius: 4px; }
    .page-header h2 { margin: 0 0 6px 0; font-size: 22px; color: #0f172a; }
    .subtitle { margin: 0; font-size: 13px; color: #64748b; }
    .btn-secondary { background: #f1f5f9; color: #334155; padding: 8px 16px; border-radius: 6px; font-size: 13px; font-weight: 600; text-decoration: none; }
    .decision-card {
      background: #eff6ff; border: 1px solid #bfdbfe; border-radius: 12px; padding: 20px;
    }
    .decision-card h3 { margin: 0 0 6px 0; font-size: 16px; color: #1e40af; }
    .decision-card p { margin: 0 0 14px 0; font-size: 13px; color: #1e3a8a; }
    .decision-card input {
      width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 13px; box-sizing: border-box; margin-bottom: 14px;
    }
    .decision-buttons { display: flex; gap: 12px; }
    .btn-valider { background: #059669; color: #fff; border: none; padding: 10px 18px; border-radius: 6px; font-weight: 700; cursor: pointer; }
    .btn-rejeter { background: #dc2626; color: #fff; border: none; padding: 10px 18px; border-radius: 6px; font-weight: 700; cursor: pointer; }
    .owner-actions { display: flex; gap: 12px; }
    .btn-edit { background: #f8fafc; color: #1e293b; border: 1px solid #cbd5e1; padding: 8px 16px; border-radius: 6px; font-weight: 600; text-decoration: none; display: inline-flex; align-items: center; }
    .btn-danger { background: #fee2e2; color: #b91c1c; border: 1px solid #fca5a5; padding: 8px 16px; border-radius: 6px; font-weight: 600; cursor: pointer; }
    .card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; }
    .card-header { padding: 16px 20px; border-bottom: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center; }
    .card-header h3 { margin: 0; font-size: 16px; color: #0f172a; }
    .count-badge { font-size: 12px; font-weight: 600; color: #64748b; background: #f1f5f9; padding: 4px 8px; border-radius: 4px; }
    .data-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 13px; }
    .data-table th { background: #f8fafc; padding: 12px 16px; border-bottom: 1px solid #e2e8f0; color: #475569; font-weight: 700; }
    .data-table td { padding: 14px 16px; border-bottom: 1px solid #f1f5f9; color: #1e293b; }
    .text-right { text-align: right; }
    .timeline { padding: 20px; display: flex; flex-direction: column; gap: 16px; }
    .timeline-item { display: flex; gap: 14px; align-items: flex-start; }
    .timeline-dot { width: 10px; height: 10px; border-radius: 50%; background: #2563eb; margin-top: 6px; }
    .timeline-content { font-size: 13px; }
    .timeline-title { color: #1e293b; }
    .timeline-date { font-size: 11px; color: #94a3b8; margin: 2px 0; }
    .timeline-comment { font-style: italic; color: #475569; background: #f8fafc; padding: 6px 10px; border-radius: 4px; margin-top: 4px; }
  `]
})
export class BesoinDetailComponent implements OnInit {
  besoin: ExpressionBesoin | null = null;
  historique: HistoriqueStatut[] = [];
  commentaireDecision = '';
  processing = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private besoinService: BesoinService,
    public auth: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.chargerDetails(id);
    }
  }

  chargerDetails(id: number): void {
    this.besoinService.trouverParId(id).subscribe({
      next: (res) => {
        if (res.succes && res.donnees) {
          this.besoin = res.donnees;
        }
      }
    });

    this.besoinService.getHistorique(id).subscribe({
      next: (res) => {
        if (res.succes && res.donnees) {
          this.historique = res.donnees;
        }
      }
    });
  }

  validerDemande(): void {
    if (!this.besoin) return;
    this.processing = true;
    this.besoinService.valider(this.besoin.id, this.commentaireDecision).subscribe({
      next: () => {
        this.processing = false;
        this.chargerDetails(this.besoin!.id);
      },
      error: () => this.processing = false
    });
  }

  rejeterDemande(): void {
    if (!this.besoin) return;
    this.processing = true;
    this.besoinService.rejeter(this.besoin.id, this.commentaireDecision).subscribe({
      next: () => {
        this.processing = false;
        this.chargerDetails(this.besoin!.id);
      },
      error: () => this.processing = false
    });
  }

  annulerDemande(): void {
    if (!this.besoin || !confirm('Êtes-vous sûr de vouloir annuler cette demande ?')) return;
    this.besoinService.annuler(this.besoin.id).subscribe({
      next: () => this.router.navigate(['/besoins'])
    });
  }
}
