import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { BesoinService } from '../../services/besoin.service';

@Component({
  selector: 'app-besoin-create',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h2>Nouvelle Expression de Besoin</h2>
          <p class="subtitle">Saisie des besoins en matériels et équipements pour votre département</p>
        </div>
        <a routerLink="/besoins/mes-besoins" class="btn-secondary">← Retour à mes demandes</a>
      </div>

      <div *ngIf="errorMessage" class="alert-error">{{ errorMessage }}</div>

      <form [formGroup]="form" (ngSubmit)="onSubmit()" class="form-card">
        <div class="section-title">1. Informations Générales de la Demande</div>

        <div class="form-row">
          <div class="form-group flex-2">
            <label>Description globale du besoin *</label>
            <input type="text" formControlName="description" placeholder="Ex: Équipement de la salle informatique et serveurs" />
          </div>

          <div class="form-group flex-1 checkbox-group">
            <label class="checkbox-label">
              <input type="checkbox" formControlName="urgente" />
              <span>Demande urgente</span>
            </label>
          </div>
        </div>

        <div class="form-group">
          <label>Motif de la demande / Justification</label>
          <textarea formControlName="motif" rows="3" placeholder="Précisez le contexte, l'utilité pédagogique ou de recherche..."></textarea>
        </div>

        <div class="section-title lines-header">
          <span>2. Articles et Matériels Demandés</span>
          <button type="button" (click)="ajouterLigne()" class="btn-add-line">+ Ajouter un article</button>
        </div>

        <div formArrayName="lignes" class="lines-container">
          <div *ngFor="let l of lignesArray.controls; let i = index" [formGroupName]="i" class="line-row">
            <div class="line-index">{{ i + 1 }}</div>

            <div class="form-group flex-3">
              <label>Désignation *</label>
              <input type="text" formControlName="designation" placeholder="Ex: PC Portable Core i7" />
            </div>

            <div class="form-group flex-1">
              <label>Quantité *</label>
              <input type="number" formControlName="quantiteDemandee" min="1" />
            </div>

            <div class="form-group flex-1">
              <label>Unité</label>
              <input type="text" formControlName="unite" placeholder="unité, lot, mètre..." />
            </div>

            <div class="form-group flex-2">
              <label>Prix unitaire estimé (FCFA)</label>
              <input type="number" formControlName="estimationPrix" placeholder="Ex: 850000" />
            </div>

            <button *ngIf="lignesArray.length > 1" type="button" (click)="supprimerLigne(i)" class="btn-remove-line" title="Supprimer cet article">
              🗑️
            </button>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" [disabled]="form.invalid || loading" class="btn-submit">
            {{ loading ? 'Enregistrement en cours...' : 'Soumettre l\\'expression de besoin' }}
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .page-container { display: flex; flex-direction: column; gap: 20px; max-width: 1000px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; align-items: center; }
    .page-header h2 { margin: 0; font-size: 22px; color: #0f172a; }
    .subtitle { margin: 4px 0 0 0; font-size: 13px; color: #64748b; }
    .btn-secondary {
      background: #f1f5f9; color: #334155; padding: 8px 16px; border-radius: 6px;
      font-size: 13px; font-weight: 600; text-decoration: none;
    }
    .form-card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 28px; }
    .section-title {
      font-size: 15px; font-weight: 700; color: #0f172a; margin-bottom: 16px; padding-bottom: 8px; border-bottom: 1px solid #f1f5f9;
    }
    .lines-header { display: flex; justify-content: space-between; align-items: center; margin-top: 24px; }
    .btn-add-line {
      background: #eff6ff; color: #2563eb; border: 1px dashed #bfdbfe; padding: 6px 14px;
      border-radius: 6px; font-size: 12px; font-weight: 700; cursor: pointer;
    }
    .btn-add-line:hover { background: #dbeafe; }
    .form-row { display: flex; gap: 16px; }
    .flex-1 { flex: 1; }
    .flex-2 { flex: 2; }
    .flex-3 { flex: 3; }
    .form-group { margin-bottom: 16px; }
    .form-group label { display: block; font-size: 12px; font-weight: 600; color: #475569; margin-bottom: 6px; }
    .form-group input, .form-group textarea {
      width: 100%; padding: 10px 12px; font-size: 13px; border: 1px solid #cbd5e1;
      border-radius: 6px; outline: none; box-sizing: border-box;
    }
    .form-group input:focus, .form-group textarea:focus { border-color: #2563eb; }
    .checkbox-group { display: flex; align-items: center; padding-top: 24px; }
    .checkbox-label { display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 13px; font-weight: 600; color: #b91c1c; }
    .lines-container { display: flex; flex-direction: column; gap: 12px; margin-bottom: 24px; }
    .line-row {
      display: flex; gap: 12px; align-items: flex-end; background: #f8fafc;
      padding: 12px; border-radius: 8px; border: 1px solid #e2e8f0;
    }
    .line-index {
      font-weight: 800; color: #94a3b8; font-size: 14px; padding-bottom: 18px; width: 20px; text-align: center;
    }
    .btn-remove-line {
      background: #fee2e2; border: 1px solid #fca5a5; padding: 8px 10px; border-radius: 6px;
      cursor: pointer; font-size: 14px; margin-bottom: 16px;
    }
    .form-actions { display: flex; justify-content: flex-end; margin-top: 20px; }
    .btn-submit {
      background: #2563eb; color: #fff; border: none; padding: 12px 24px; border-radius: 8px;
      font-size: 14px; font-weight: 700; cursor: pointer;
    }
    .btn-submit:hover:not(:disabled) { background: #1d4ed8; }
    .btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }
    .alert-error {
      background: #fee2e2; border-left: 4px solid #ef4444; color: #991b1b; padding: 12px; border-radius: 6px;
      font-size: 13px; margin-bottom: 16px;
    }
  `]
})
export class BesoinCreateComponent {
  form: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private besoinService: BesoinService,
    private router: Router
  ) {
    this.form = this.fb.group({
      description: ['', Validators.required],
      motif: [''],
      urgente: [false],
      lignes: this.fb.array([this.creerLigneFormGroup()])
    });
  }

  get lignesArray(): FormArray {
    return this.form.get('lignes') as FormArray;
  }

  creerLigneFormGroup(): FormGroup {
    return this.fb.group({
      designation: ['', Validators.required],
      quantiteDemandee: [1, [Validators.required, Validators.min(1)]],
      unite: ['unité'],
      estimationPrix: [null],
      observations: ['']
    });
  }

  ajouterLigne(): void {
    this.lignesArray.push(this.creerLigneFormGroup());
  }

  supprimerLigne(index: number): void {
    this.lignesArray.removeAt(index);
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading = true;
    this.errorMessage = '';

    this.besoinService.creer(this.form.value).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.succes) {
          this.router.navigate(['/besoins', res.donnees?.id]);
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Erreur lors de la création de la demande';
      }
    });
  }
}
