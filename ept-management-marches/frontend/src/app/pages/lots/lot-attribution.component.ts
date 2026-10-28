import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MarcheService } from '../../services/marche.service';
import { FournisseurService } from '../../services/fournisseur.service';
import { Lot, OffreTarifaire } from '../../models/marche.model';
import { Fournisseur } from '../../models/fournisseur.model';

@Component({
  selector: 'app-lot-attribution',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  template: `
    <div class="page-container" *ngIf="lot">
      <div class="page-header">
        <div>
          <h2>Attribution Formelle du Lot n°{{ lot.numero }}</h2>
          <p class="subtitle">{{ lot.designation }} (Marché : {{ lot.marcheReference }})</p>
        </div>
        <a [routerLink]="['/lots', lot.id]" class="btn-secondary">← Annuler</a>
      </div>

      <div *ngIf="errorMessage" class="alert-error">{{ errorMessage }}</div>

      <form [formGroup]="form" (ngSubmit)="onSubmit()" class="form-card">
        <div class="form-group">
          <label>Sélectionner le Prestataire Attributaire (Actif uniquement) *</label>
          <select formControlName="fournisseurId" (change)="onFournisseurChange()">
            <option [ngValue]="null">-- Choisir un fournisseur agréé --</option>
            <option *ngFor="let f of fournisseurs" [value]="f.id">
              {{ f.raisonSociale }} (NINEA: {{ f.ninea || 'N/A' }} - {{ f.domaineIntervention }})
            </option>
          </select>
        </div>

        <div class="form-group" *ngIf="offresDuLot.length > 0">
          <label>Rattacher une offre tarifaire retenue (optionnel) :</label>
          <select formControlName="offreId" (change)="onOffreChange()">
            <option [ngValue]="null">-- Saisie manuelle du montant sans offre rattachée --</option>
            <option *ngFor="let o of offresDuLot" [value]="o.id">
              {{ o.fournisseurNom }} — {{ o.montantTotal | number }} FCFA (Statut: {{ o.statut }})
            </option>
          </select>
        </div>

        <div class="form-row">
          <div class="form-group flex-1">
            <label>Montant total d'attribution (FCFA) *</label>
            <input type="number" formControlName="montantAttribue" placeholder="Ex: 20500000" />
          </div>
          <div class="form-group flex-1">
            <label>Date limite de livraison prévue *</label>
            <input type="date" formControlName="dateLivraisonPrevue" />
          </div>
        </div>

        <div class="form-group">
          <label>Observations / Justification du choix</label>
          <textarea formControlName="observations" rows="3" placeholder="Ex: Offre la moins disante respectant l'ensemble du cahier des charges techniques..."></textarea>
        </div>

        <div class="form-actions">
          <button type="submit" [disabled]="form.invalid || loading" class="btn-submit">
            {{ loading ? 'Attribution en cours...' : 'Confirmer l\\'attribution du lot' }}
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .page-container { max-width: 800px; margin: 0 auto; display: flex; flex-direction: column; gap: 20px; }
    .page-header { display: flex; justify-content: space-between; align-items: center; }
    .page-header h2 { margin: 0; font-size: 22px; color: #0f172a; }
    .subtitle { margin: 4px 0 0 0; font-size: 13px; color: #64748b; }
    .btn-secondary { background: #f1f5f9; color: #334155; padding: 8px 16px; border-radius: 6px; font-size: 13px; text-decoration: none; font-weight: 600; }
    .form-card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 28px; }
    .form-group { margin-bottom: 18px; }
    .form-group label { display: block; font-size: 12px; font-weight: 700; color: #334155; margin-bottom: 6px; }
    .form-group select, .form-group input, .form-group textarea {
      width: 100%; padding: 10px 12px; font-size: 13px; border: 1px solid #cbd5e1; border-radius: 6px; box-sizing: border-box; outline: none;
    }
    .form-row { display: flex; gap: 16px; }
    .flex-1 { flex: 1; }
    .form-actions { display: flex; justify-content: flex-end; margin-top: 20px; }
    .btn-submit { background: #d97706; color: #fff; border: none; padding: 12px 24px; border-radius: 8px; font-size: 14px; font-weight: 700; cursor: pointer; }
    .btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }
    .alert-error { background: #fee2e2; border-left: 4px solid #ef4444; color: #991b1b; padding: 12px; border-radius: 6px; font-size: 13px; }
  `]
})
export class LotAttributionComponent implements OnInit {
  lot: Lot | null = null;
  fournisseurs: Fournisseur[] = [];
  offresDuLot: OffreTarifaire[] = [];
  form: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private marcheService: MarcheService,
    private fournisseurService: FournisseurService
  ) {
    this.form = this.fb.group({
      fournisseurId: [null, Validators.required],
      fournisseurNom: [''],
      offreId: [null],
      montantAttribue: [null, [Validators.required, Validators.min(1)]],
      dateLivraisonPrevue: ['', Validators.required],
      observations: ['']
    });
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
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
            this.offresDuLot = res.donnees;
          }
        }
      });
    }

    this.fournisseurService.listerActifs().subscribe({
      next: (res) => {
        if (res.succes && res.donnees) {
          this.fournisseurs = res.donnees;
        }
      }
    });
  }

  onFournisseurChange(): void {
    const fId = Number(this.form.get('fournisseurId')?.value);
    const found = this.fournisseurs.find(f => f.id === fId);
    if (found) {
      this.form.patchValue({ fournisseurNom: found.raisonSociale });
    }
  }

  onOffreChange(): void {
    const oId = Number(this.form.get('offreId')?.value);
    const found = this.offresDuLot.find(o => o.id === oId);
    if (found) {
      this.form.patchValue({
        fournisseurId: found.fournisseurId,
        fournisseurNom: found.fournisseurNom,
        montantAttribue: found.montantTotal
      });
    }
  }

  onSubmit(): void {
    if (this.form.invalid || !this.lot) return;

    this.loading = true;
    this.errorMessage = '';

    this.marcheService.attribuerLot(this.lot.id, this.form.value).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.succes) {
          this.router.navigate(['/lots', this.lot!.id]);
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || "Erreur lors de l'attribution du lot";
      }
    });
  }
}
