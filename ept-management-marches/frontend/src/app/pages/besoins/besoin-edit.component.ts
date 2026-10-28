import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { BesoinService } from '../../services/besoin.service';
import { ExpressionBesoin } from '../../models/besoin.model';

@Component({
  selector: 'app-besoin-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="page-container" *ngIf="besoin">
      <div class="page-header">
        <div>
          <h2>Modifier la Demande {{ besoin.reference }}</h2>
          <p class="subtitle">Mettez à jour les motifs et la liste des matériels avant validation</p>
        </div>
        <a [routerLink]="['/besoins', besoin.id]" class="btn-secondary">Annuler</a>
      </div>

      <div *ngIf="errorMessage" class="alert-error">
        {{ errorMessage }}
      </div>

      <form [formGroup]="besoinForm" (ngSubmit)="onSubmit()" class="card form-card">
        <div class="form-section">
          <h3>Informations Générales</h3>
          <div class="form-grid">
            <div class="form-group full-width">
              <label>Description synthétique *</label>
              <input type="text" formControlName="description" placeholder="Ex: Renouvellement du parc informatique" />
            </div>

            <div class="form-group full-width">
              <label>Motif de la demande</label>
              <textarea formControlName="motif" rows="3" placeholder="Justifiez l'opportunité de l'achat..."></textarea>
            </div>

            <div class="form-group checkbox-group">
              <label>
                <input type="checkbox" formControlName="urgente" />
                <span>Demande prioritaire / urgente</span>
              </label>
            </div>
          </div>
        </div>

        <div class="form-section">
          <div class="section-header">
            <h3>Articles et Équipements Demandés</h3>
            <button type="button" (click)="ajouterLigne()" class="btn-outline">➕ Ajouter un article</button>
          </div>

          <div formArrayName="lignes" class="lignes-container">
            <div *ngFor="let ligne of lignes.controls; let i = index" [formGroupName]="i" class="ligne-item">
              <div class="ligne-header">
                <span class="ligne-title">Article #{{ i + 1 }}</span>
                <button type="button" *ngIf="lignes.length > 1" (click)="supprimerLigne(i)" class="btn-remove">🗑️ Supprimer</button>
              </div>

              <div class="ligne-grid">
                <div class="form-group span-2">
                  <label>Désignation *</label>
                  <input type="text" formControlName="designation" placeholder="Ex: Ordinateur portable i7" />
                </div>
                <div class="form-group">
                  <label>Quantité *</label>
                  <input type="number" formControlName="quantiteDemandee" min="1" />
                </div>
                <div class="form-group">
                  <label>Unité</label>
                  <input type="text" formControlName="unite" placeholder="unité, lot, boîte..." />
                </div>
                <div class="form-group">
                  <label>Prix estimé (FCFA)</label>
                  <input type="number" formControlName="estimationPrix" min="0" placeholder="Prix unitaire" />
                </div>
                <div class="form-group full-width">
                  <label>Spécifications techniques</label>
                  <input type="text" formControlName="description" placeholder="Spécifications (RAM, processeur...)" />
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" [disabled]="besoinForm.invalid || submitting" class="btn-submit">
            {{ submitting ? 'Enregistrement en cours...' : 'Enregistrer les modifications' }}
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .page-container { display: flex; flex-direction: column; gap: 20px; }
    .page-header { display: flex; justify-content: space-between; align-items: center; }
    .page-header h2 { margin: 0; font-size: 22px; color: #0f172a; }
    .subtitle { margin: 4px 0 0 0; font-size: 13px; color: #64748b; }
    .btn-secondary { background: #f1f5f9; color: #0f172a; padding: 10px 18px; border-radius: 8px; text-decoration: none; font-size: 13px; font-weight: 600; }
    .card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 24px; }
    .form-section { margin-bottom: 24px; border-bottom: 1px solid #f1f5f9; padding-bottom: 24px; }
    .form-section h3 { margin: 0 0 16px 0; font-size: 16px; color: #1e293b; }
    .section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .full-width { grid-column: span 2; }
    .span-2 { grid-column: span 2; }
    .form-group { display: flex; flex-direction: column; gap: 6px; }
    .form-group label { font-size: 13px; font-weight: 600; color: #334155; }
    .form-group input, .form-group textarea {
      padding: 10px 14px; border: 1px solid #cbd5e1; border-radius: 8px; font-size: 13px; outline: none;
    }
    .checkbox-group label { display: flex; align-items: center; gap: 8px; cursor: pointer; }
    .lignes-container { display: flex; flex-direction: column; gap: 16px; }
    .ligne-item { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 16px; }
    .ligne-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
    .ligne-title { font-weight: 700; font-size: 14px; color: #0f172a; }
    .ligne-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
    .btn-outline { background: #eff6ff; color: #2563eb; border: 1px solid #bfdbfe; padding: 8px 14px; border-radius: 6px; font-size: 12px; font-weight: 600; cursor: pointer; }
    .btn-remove { background: #fee2e2; color: #dc2626; border: none; padding: 6px 12px; border-radius: 6px; font-size: 12px; cursor: pointer; }
    .form-actions { display: flex; justify-content: flex-end; }
    .btn-submit { background: #2563eb; color: #ffffff; border: none; padding: 12px 24px; border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer; }
    .alert-error { background: #fee2e2; color: #991b1b; padding: 12px; border-radius: 8px; margin-bottom: 16px; font-size: 13px; }
  `]
})
export class BesoinEditComponent implements OnInit {
  besoin: ExpressionBesoin | null = null;
  besoinForm!: FormGroup;
  submitting = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private besoinService: BesoinService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.initForm();
    if (id) {
      this.besoinService.trouverParId(id).subscribe({
        next: (res) => {
          if (res.succes && res.donnees) {
            this.besoin = res.donnees;
            this.remplirForm(res.donnees);
          }
        },
        error: () => {
          this.errorMessage = 'Impossible de charger la demande.';
        }
      });
    }
  }

  get lignes(): FormArray {
    return this.besoinForm.get('lignes') as FormArray;
  }

  private initForm(): void {
    this.besoinForm = this.fb.group({
      description: ['', [Validators.required]],
      motif: [''],
      urgente: [false],
      lignes: this.fb.array([])
    });
  }

  private remplirForm(b: ExpressionBesoin): void {
    this.besoinForm.patchValue({
      description: b.description,
      motif: b.motif,
      urgente: b.urgente
    });

    this.lignes.clear();
    b.lignes.forEach(l => {
      this.lignes.push(this.fb.group({
        designation: [l.designation, [Validators.required]],
        description: [l.description || ''],
        quantiteDemandee: [l.quantiteDemandee, [Validators.required, Validators.min(1)]],
        unite: [l.unite || 'unité'],
        estimationPrix: [l.estimationPrix || null]
      }));
    });
  }

  ajouterLigne(): void {
    this.lignes.push(this.fb.group({
      designation: ['', [Validators.required]],
      description: [''],
      quantiteDemandee: [1, [Validators.required, Validators.min(1)]],
      unite: ['unité'],
      estimationPrix: [null]
    }));
  }

  supprimerLigne(index: number): void {
    this.lignes.removeAt(index);
  }

  onSubmit(): void {
    if (this.besoinForm.invalid || !this.besoin) return;

    this.submitting = true;
    this.errorMessage = '';

    this.besoinService.modifier(this.besoin.id, this.besoinForm.value).subscribe({
      next: (res) => {
        this.submitting = false;
        if (res.succes) {
          this.router.navigate(['/besoins', this.besoin?.id]);
        } else {
          this.errorMessage = res.message || 'Erreur lors de la modification.';
        }
      },
      error: (err) => {
        this.submitting = false;
        this.errorMessage = err.error?.message || 'Erreur serveur lors de la modification.';
      }
    });
  }
}
