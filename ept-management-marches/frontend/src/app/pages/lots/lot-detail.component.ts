import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MarcheService } from '../../services/marche.service';
import { AuthService } from '../../services/auth.service';
import { Lot, OffreTarifaire, Livraison } from '../../models/marche.model';

@Component({
  selector: 'app-lot-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="page-container" *ngIf="lot">
      <div class="page-header">
        <div>
          <div class="header-badges">
            <span class="marche-badge">Marché : {{ lot.marcheReference }}</span>
            <span class="status-badge" [ngClass]="lot.statut.toLowerCase()">{{ lot.statut }}</span>
          </div>
          <h2>Lot n°{{ lot.numero }} : {{ lot.designation }}</h2>
          <p class="subtitle">Prestataire attributaire : <strong>{{ lot.fournisseurNom || 'En attente d\\'attribution' }}</strong></p>
        </div>
        <div class="header-actions">
          <a [routerLink]="['/marches', lot.marcheId]" class="btn-secondary">← Retour au Marché</a>
          <a *ngIf="lot.statut === 'EN_ATTENTE' && auth.hasRole('SERVICE_ACHATS')"
             [routerLink]="['/lots', lot.id, 'attribuer']" class="btn-attrib">
            🏆 Attribuer ce Lot
          </a>
        </div>
      </div>

      <!-- Taux d'exécution et indicateurs -->
      <div class="progress-card">
        <div class="progress-header">
          <div>
            <span class="progress-title">Avancement global des livraisons du lot</span>
            <div class="progress-pct">{{ lot.tauxExecution || 0 }}%</div>
          </div>
          <div class="financials">
            <div>Montant estimé : <strong>{{ lot.montantEstime ? (lot.montantEstime | number) + ' FCFA' : '—' }}</strong></div>
            <div>Montant attribué : <strong class="text-success">{{ lot.montantAttribue ? (lot.montantAttribue | number) + ' FCFA' : '—' }}</strong></div>
          </div>
        </div>
        <div class="bar-outer">
          <div class="bar-inner" [style.width.%]="lot.tauxExecution || 0"></div>
        </div>
      </div>

      <!-- Formulaire d'enregistrement d'une livraison -->
      <div *ngIf="lot.statut !== 'EN_ATTENTE' && lot.statut !== 'LIVRE' && lot.statut !== 'CLOTURE' && (auth.hasRole('SERVICE_ACHATS') || auth.hasRole('ADMIN'))" class="card delivery-form-card">
        <div class="card-header">
          <h3>📦 Enregistrer une Réception de Matériels</h3>
        </div>
        <div class="card-body">
          <div *ngIf="livraisonMsg" class="alert-info">{{ livraisonMsg }}</div>
          <div class="form-row">
            <div class="form-group flex-2">
              <label>Désignation de la tranche livrée *</label>
              <input type="text" [(ngModel)]="nouvelleLivraison.designation" placeholder="Ex: 1ère tranche de 15 ordinateurs" />
            </div>
            <div class="form-group flex-1">
              <label>Quantité reçue *</label>
              <input type="number" [(ngModel)]="nouvelleLivraison.quantiteLivree" min="1" placeholder="Qté" />
            </div>
            <div class="form-group flex-2">
              <label>Observations / Procès-verbal</label>
              <input type="text" [(ngModel)]="nouvelleLivraison.observations" placeholder="Matériel conforme, testé..." />
            </div>
            <div class="form-group btn-col">
              <button (click)="enregistrerLivraison()" [disabled]="!nouvelleLivraison.quantiteLivree || savingDelivery" class="btn-primary">
                Enregistrer Réception
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Historique des livraisons réelles -->
      <div class="card" *ngIf="livraisons.length > 0">
        <div class="card-header">
          <h3>Historique des Livraisons Effectuées ({{ livraisons.length }})</h3>
        </div>
        <table class="data-table">
          <thead>
            <tr>
              <th>Date Réception</th>
              <th>Désignation</th>
              <th>Quantité Livrée</th>
              <th>Réceptionnaire</th>
              <th>Observations</th>
              <th>Conformité</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let l of livraisons">
              <td>{{ l.dateLivraison | date:'dd/MM/yyyy HH:mm' }}</td>
              <td><strong>{{ l.designation }}</strong></td>
              <td><span class="badge-qty">+{{ l.quantiteLivree }}</span></td>
              <td>{{ l.receptionnaireNom }}</td>
              <td>{{ l.observations || '—' }}</td>
              <td><span class="badge-conform">CONFORME</span></td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Tableau des articles du lot -->
      <div class="card">
        <div class="card-header">
          <h3>Articles Programmés dans ce Lot</h3>
        </div>
        <table class="data-table">
          <thead>
            <tr>
              <th>Désignation</th>
              <th>Quantité Programmée</th>
              <th class="text-right">Prix Unitaire</th>
              <th class="text-right">Total</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let item of lot.lignesLot">
              <td><strong>{{ item.designation }}</strong></td>
              <td>{{ item.quantite }}</td>
              <td class="text-right">{{ item.prixUnitaire ? (item.prixUnitaire | number) + ' FCFA' : '—' }}</td>
              <td class="text-right"><strong>{{ item.montantTotal ? (item.montantTotal | number) + ' FCFA' : '—' }}</strong></td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Offres reçues pour ce lot -->
      <div class="card">
        <div class="card-header">
          <h3>Offres Tarifaires des Fournisseurs ({{ offres.length }})</h3>
        </div>
        <div *ngIf="offres.length === 0" class="empty-state">
          Aucune offre tarifaire n'a encore été enregistrée pour ce lot.
        </div>
        <table *ngIf="offres.length > 0" class="data-table">
          <thead>
            <tr>
              <th>Fournisseur</th>
              <th>Date de Soumission</th>
              <th>Montant Total Proposé</th>
              <th>Statut de l'Offre</th>
              <th>Observations</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let o of offres">
              <td><strong>{{ o.fournisseurNom }}</strong></td>
              <td>{{ o.dateOffre | date:'dd/MM/yyyy' }}</td>
              <td><strong class="text-amount">{{ o.montantTotal | number }} FCFA</strong></td>
              <td>
                <span class="badge" [ngClass]="o.statut.toLowerCase()">{{ o.statut }}</span>
              </td>
              <td>{{ o.observations || '—' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .page-container { display: flex; flex-direction: column; gap: 20px; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; }
    .header-badges { display: flex; gap: 8px; margin-bottom: 8px; }
    .marche-badge { background: #f1f5f9; color: #334155; font-weight: 700; font-size: 12px; padding: 4px 10px; border-radius: 4px; }
    .status-badge { font-size: 11px; font-weight: 700; padding: 4px 10px; border-radius: 20px; }
    .page-header h2 { margin: 0 0 6px 0; font-size: 22px; color: #0f172a; }
    .subtitle { margin: 0; font-size: 13px; color: #64748b; }
    .header-actions { display: flex; gap: 10px; }
    .btn-secondary { background: #f1f5f9; color: #334155; padding: 8px 16px; border-radius: 6px; font-size: 13px; font-weight: 600; text-decoration: none; }
    .btn-attrib { background: #d97706; color: #fff; padding: 8px 16px; border-radius: 6px; font-size: 13px; font-weight: 700; text-decoration: none; }
    .progress-card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 22px; }
    .progress-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
    .progress-title { font-size: 13px; font-weight: 600; color: #64748b; }
    .progress-pct { font-size: 28px; font-weight: 800; color: #10b981; }
    .financials { text-align: right; font-size: 13px; color: #475569; }
    .financials strong { color: #0f172a; }
    .text-success { color: #059669 !important; }
    .bar-outer { height: 10px; background: #f1f5f9; border-radius: 5px; overflow: hidden; }
    .bar-inner { height: 100%; background: #10b981; border-radius: 5px; transition: width 0.3s; }
    .delivery-form-card { background: #f0fdf4; border: 1px solid #bbf7d0; }
    .card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; }
    .card-header { padding: 16px 20px; border-bottom: 1px solid #f1f5f9; }
    .card-header h3 { margin: 0; font-size: 16px; color: #0f172a; }
    .card-body { padding: 20px; }
    .form-row { display: flex; gap: 12px; align-items: flex-end; }
    .flex-1 { flex: 1; }
    .flex-2 { flex: 2; }
    .btn-col { padding-bottom: 2px; }
    .form-group label { display: block; font-size: 11px; font-weight: 700; color: #334155; margin-bottom: 6px; }
    .form-group input { width: 100%; padding: 9px 12px; font-size: 13px; border: 1px solid #cbd5e1; border-radius: 6px; box-sizing: border-box; }
    .btn-primary { background: #059669; color: #fff; border: none; padding: 10px 18px; border-radius: 6px; font-weight: 700; cursor: pointer; white-space: nowrap; }
    .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
    .data-table { width: 100%; border-collapse: collapse; font-size: 13px; text-align: left; }
    .data-table th { background: #f8fafc; padding: 12px 16px; border-bottom: 1px solid #e2e8f0; color: #475569; font-weight: 700; }
    .data-table td { padding: 14px 16px; border-bottom: 1px solid #f1f5f9; color: #1e293b; }
    .text-right { text-align: right; }
    .badge-qty { background: #dcfce7; color: #15803d; font-weight: 800; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
    .badge-conform { background: #dcfce7; color: #166534; font-size: 10px; font-weight: 800; padding: 2px 6px; border-radius: 4px; }
    .text-amount { color: #059669; }
    .badge { padding: 3px 8px; border-radius: 20px; font-size: 11px; font-weight: 700; }
    .retenue { background: #dcfce7; color: #166534; }
    .rejetee { background: #fee2e2; color: #991b1b; }
    .soumise { background: #fef3c7; color: #92400e; }
    .en_attente { background: #fef3c7; color: #92400e; }
    .attribue { background: #e0e7ff; color: #3730a3; }
    .en_cours_livraison { background: #dbeafe; color: #1e40af; }
    .livre_partiellement { background: #fed7aa; color: #9a3412; }
    .livre { background: #dcfce7; color: #15803d; }
    .empty-state { padding: 30px; text-align: center; color: #64748b; font-size: 13px; }
    .alert-info { background: #eff6ff; color: #1e40af; padding: 10px; border-radius: 6px; margin-bottom: 12px; font-size: 13px; }
  `]
})
export class LotDetailComponent implements OnInit {
  lot: Lot | null = null;
  offres: OffreTarifaire[] = [];
  livraisons: Livraison[] = [];
  nouvelleLivraison = { designation: '', quantiteLivree: 1, observations: '' };
  savingDelivery = false;
  livraisonMsg = '';

  constructor(
    private route: ActivatedRoute,
    private marcheService: MarcheService,
    public auth: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.chargerLot(id);
    }
  }

  chargerLot(id: number): void {
    this.marcheService.trouverLotParId(id).subscribe({
      next: (res) => {
        if (res.succes && res.donnees) {
          this.lot = res.donnees;
        }
      }
    });

    this.marcheService.comparerOffres(id).subscribe({
      next: (res) => {
        if (res.succes && res.donnees) {
          this.offres = res.donnees;
        }
      }
    });

    this.marcheService.getLivraisonsDuLot(id).subscribe({
      next: (res) => {
        if (res.succes && res.donnees) {
          this.livraisons = res.donnees;
        }
      }
    });
  }

  enregistrerLivraison(): void {
    if (!this.lot || !this.nouvelleLivraison.quantiteLivree) return;

    this.savingDelivery = true;
    this.livraisonMsg = '';

    const payload = {
      lotId: this.lot.id,
      designation: this.nouvelleLivraison.designation,
      quantiteLivree: this.nouvelleLivraison.quantiteLivree,
      observations: this.nouvelleLivraison.observations,
      conforme: true
    };

    this.marcheService.enregistrerLivraison(payload).subscribe({
      next: () => {
        this.savingDelivery = false;
        this.livraisonMsg = "Livraison enregistrée avec succès. Taux d'exécution actualisé !";
        this.nouvelleLivraison = { designation: '', quantiteLivree: 1, observations: '' };
        this.chargerLot(this.lot!.id);
      },
      error: (err) => {
        this.savingDelivery = false;
        this.livraisonMsg = err.error?.message || "Erreur lors de l'enregistrement de la livraison";
      }
    });
  }
}
