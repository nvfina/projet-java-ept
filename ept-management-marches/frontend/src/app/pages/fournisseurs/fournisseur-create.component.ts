import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { FournisseurService } from '../../services/fournisseur.service';

@Component({
  selector: 'app-fournisseur-create',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h2>Référencer un Nouveau Fournisseur</h2>
          <p class="subtitle">Enregistrement d'un prestataire dans le catalogue officiel de l'EPT</p>
        </div>
        <a routerLink="/fournisseurs" class="btn-secondary">← Retour à la liste</a>
      </div>

      <div *ngIf="errorMessage" class="alert-error">{{ errorMessage }}</div>

      <form [formGroup]="form" (ngSubmit)="onSubmit()" class="form-card">
        <div class="form-row">
          <div class="form-group flex-2">
            <label>Raison Sociale (Nom de l'entreprise) *</label>
            <input type="text" formControlName="raisonSociale" placeholder="Ex: TechnoPlus SARL" />
          </div>
          <div class="form-group flex-1">
            <label>Numéro NINEA</label>
            <input type="text" formControlName="ninea" placeholder="Ex: SN-DKR-2024-B-1234" />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group flex-1">
            <label>Domaine d'intervention principal *</label>
            <select formControlName="domaineIntervention">
              <option value="">-- Choisir un domaine --</option>
              <option value="Informatique">Informatique & Télécoms</option>
              <option value="BTP & Travaux">BTP & Génie Civil</option>
              <option value="Génie Électrique">Génie Électrique & Énergie</option>
              <option value="Génie Mécanique">Génie Mécanique & Outillage</option>
              <option value="Fournitures de bureau">Fournitures de bureau & Mobilier</option>
              <option value="Laboratoire & Recherche">Équipements de Laboratoire</option>
            </select>
          </div>
          <div class="form-group flex-1">
            <label>Téléphone principal *</label>
            <input type="text" formControlName="telephone" placeholder="Ex: +221 33 825 00 11" />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group flex-1">
            <label>Adresse E-mail</label>
            <input type="email" formControlName="email" placeholder="contact@entreprise.sn" />
          </div>
          <div class="form-group flex-1">
            <label>Site Web</label>
            <input type="text" formControlName="siteWeb" placeholder="https://entreprise.sn" />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group flex-1">
            <label>Nom du Contact Principal</label>
            <input type="text" formControlName="contactPrincipal" placeholder="Nom et prénom du responsable commercial" />
          </div>
          <div class="form-group flex-2">
            <label>Adresse postale / Siège</label>
            <input type="text" formControlName="adresse" placeholder="Adresse, Ville, Sénégal" />
          </div>
        </div>

        <div class="form-group">
          <label>Observations / Notes d'agrément</label>
          <textarea formControlName="observations" rows="3" placeholder="Antécédents, garanties bancaires, références techniques..."></textarea>
        </div>

        <div class="form-actions">
          <button type="submit" [disabled]="form.invalid || loading" class="btn-submit">
            {{ loading ? 'Enregistrement en cours...' : 'Enregistrer le Fournisseur' }}
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
    .form-row { display: flex; gap: 16px; }
    .flex-1 { flex: 1; }
    .flex-2 { flex: 2; }
    .form-group { margin-bottom: 16px; }
    .form-group label { display: block; font-size: 12px; font-weight: 700; color: #475569; margin-bottom: 6px; }
    .form-group input, .form-group select, .form-group textarea {
      width: 100%; padding: 10px 12px; font-size: 13px; border: 1px solid #cbd5e1; border-radius: 6px; outline: none; box-sizing: border-box;
    }
    .form-group input:focus, .form-group select:focus, .form-group textarea:focus { border-color: #2563eb; }
    .form-actions { display: flex; justify-content: flex-end; margin-top: 20px; }
    .btn-submit { background: #2563eb; color: #fff; border: none; padding: 12px 24px; border-radius: 8px; font-size: 14px; font-weight: 700; cursor: pointer; }
    .btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }
    .alert-error { background: #fee2e2; border-left: 4px solid #ef4444; color: #991b1b; padding: 12px; border-radius: 6px; font-size: 13px; }
  `]
})
export class FournisseurCreateComponent {
  form: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private fournisseurService: FournisseurService,
    private router: Router
  ) {
    this.form = this.fb.group({
      raisonSociale: ['', Validators.required],
      ninea: [''],
      domaineIntervention: ['', Validators.required],
      telephone: ['', Validators.required],
      email: ['', Validators.email],
      siteWeb: [''],
      contactPrincipal: [''],
      adresse: [''],
      observations: ['']
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading = true;
    this.errorMessage = '';

    this.fournisseurService.creer(this.form.value).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.succes) {
          this.router.navigate(['/fournisseurs']);
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Erreur lors de la création du fournisseur';
      }
    });
  }
}
