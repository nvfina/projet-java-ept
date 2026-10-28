import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { MarcheService } from '../../services/marche.service';
import { BesoinService } from '../../services/besoin.service';
import { ExpressionBesoin } from '../../models/besoin.model';

@Component({
  selector: 'app-marche-create',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h2>Création d'un Nouveau Marché Public</h2>
          <p class="subtitle">Regroupement des besoins validés et structuration en lots d'acquisition</p>
        </div>
        <a routerLink="/marches" class="btn-secondary">← Retour aux marchés</a>
      </div>

      <div *ngIf="errorMessage" class="alert-error">{{ errorMessage }}</div>

      <!-- Résumé des besoins validés en attente de passation -->
      <div class="info-box" *ngIf="besoinsValides.length > 0">
        <h4>Expressions de besoins validées disponibles ({{ besoinsValides.length }}) :</h4>
        <div class="besoins-chips">
          <span *ngFor="let b of besoinsValides" class="besoin-chip">
            <strong>{{ b.reference }}</strong> ({{ b.serviceDemandeur }} - {{ b.lignes.length }} art.)
          </span>
        </div>
      </div>

      <form [formGroup]="form" (ngSubmit)="onSubmit()" class="form-card">
        <div class="section-title">1. Informations Générales du Marché</div>

        <div class="form-group">
          <label>Objet du marché *</label>
          <input type="text" formControlName="objet" placeholder="Ex: Acquisition d'équipements informatiques et serveurs de calcul" />
        </div>

        <div class="form-row">
          <div class="form-group flex-1">
            <label>Budget prévisionnel global (FCFA)</label>
            <input type="number" formControlName="budgetPrevisionnel" placeholder="Ex: 35000000" />
          </div>
          <div class="form-group flex-1">
            <label>Date de lancement prévisionnelle</label>
            <input type="date" formControlName="dateLancement" />
          </div>
        </div>

        <div class="form-group">
          <label>Description / Cahier des charges sommaire</label>
          <textarea formControlName="description" rows="3" placeholder="Précisez le contexte du marché..."></textarea>
        </div>

        <div class="section-title lots-header">
          <span>2. Découpage en Lots</span>
          <button type="button" (click)="ajouterLot()" class="btn-add-lot">+ Ajouter un Lot</button>
        </div>

        <div formArrayName="lots" class="lots-container">
          <div *ngFor="let lot of lotsArray.controls; let lotIdx = index" [formGroupName]="lotIdx" class="lot-card">
            <div class="lot-header">
              <span class="lot-tag">Lot n°{{ lotIdx + 1 }}</span>
              <button *ngIf="lotsArray.length > 1" type="button" (click)="supprimerLot(lotIdx)" class="btn-del-lot">Supprimer ce lot</button>
            </div>

            <div class="form-row">
              <div class="form-group flex-2">
                <label>Désignation du lot *</label>
                <input type="text" formControlName="designation" placeholder="Ex: Lot 1 : Ordinateurs portables" />
              </div>
              <div class="form-group flex-1">
                <label>Montant estimé (FCFA)</label>
                <input type="number" formControlName="montantEstime" placeholder="Ex: 20000000" />
              </div>
            </div>

            <!-- Articles du lot -->
            <div class="lot-items-section">
              <div class="items-header">
                <label>Articles inclus dans ce lot :</label>
                <button type="button" (click)="ajouterLigneLot(lotIdx)" class="btn-add-item">+ Article</button>
              </div>

              <div [formArrayName]="'lignesLot'" class="items-list">
                <div *ngFor="let item of getLignesLotArray(lotIdx).controls; let itemIdx = index" [formGroupName]="itemIdx" class="item-row">
                  <input type="text" formControlName="designation" placeholder="Désignation article *" class="flex-3" />
                  <input type="number" formControlName="quantite" placeholder="Qté *" min="1" class="flex-1" />
                  <input type="number" formControlName="prixUnitaire" placeholder="Prix Unitaire (FCFA)" class="flex-2" />
                  <button *ngIf="getLignesLotArray(lotIdx).length > 1" type="button" (click)="supprimerLigneLot(lotIdx, itemIdx)" class="btn-del-item">✕</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" [disabled]="form.invalid || loading" class="btn-submit">
            {{ loading ? 'Création en cours...' : 'Créer et Enregistrer le Marché' }}
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .page-container { max-width: 1000px; margin: 0 auto; display: flex; flex-direction: column; gap: 20px; }
    .page-header { display: flex; justify-content: space-between; align-items: center; }
    .page-header h2 { margin: 0; font-size: 22px; color: #0f172a; }
    .subtitle { margin: 4px 0 0 0; font-size: 13px; color: #64748b; }
    .btn-secondary { background: #f1f5f9; color: #334155; padding: 8px 16px; border-radius: 6px; font-size: 13px; text-decoration: none; font-weight: 600; }
    .info-box { background: #eff6ff; border: 1px solid #bfdbfe; border-radius: 10px; padding: 16px 20px; }
    .info-box h4 { margin: 0 0 10px 0; font-size: 14px; color: #1e40af; }
    .besoins-chips { display: flex; flex-wrap: wrap; gap: 8px; }
    .besoin-chip { background: #ffffff; border: 1px solid #cbd5e1; padding: 4px 10px; border-radius: 20px; font-size: 12px; }
    .form-card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 28px; }
    .section-title { font-size: 15px; font-weight: 700; color: #0f172a; margin-bottom: 16px; padding-bottom: 8px; border-bottom: 1px solid #f1f5f9; }
    .lots-header { display: flex; justify-content: space-between; align-items: center; margin-top: 24px; }
    .btn-add-lot { background: #eff6ff; color: #2563eb; border: 1px dashed #bfdbfe; padding: 6px 14px; border-radius: 6px; font-weight: 700; cursor: pointer; }
    .form-row { display: flex; gap: 16px; }
    .flex-1 { flex: 1; }
    .flex-2 { flex: 2; }
    .flex-3 { flex: 3; }
    .form-group { margin-bottom: 16px; }
    .form-group label { display: block; font-size: 12px; font-weight: 600; color: #475569; margin-bottom: 6px; }
    .form-group input, .form-group textarea {
      width: 100%; padding: 10px 12px; font-size: 13px; border: 1px solid #cbd5e1; border-radius: 6px; outline: none; box-sizing: border-box;
    }
    .lots-container { display: flex; flex-direction: column; gap: 20px; margin-bottom: 24px; }
    .lot-card { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 10px; padding: 18px; }
    .lot-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
    .lot-tag { background: #d97706; color: #fff; font-weight: 800; font-size: 12px; padding: 4px 10px; border-radius: 4px; }
    .btn-del-lot { background: #fee2e2; color: #b91c1c; border: none; padding: 4px 10px; border-radius: 4px; font-size: 11px; font-weight: 700; cursor: pointer; }
    .lot-items-section { margin-top: 10px; }
    .items-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; font-size: 12px; font-weight: 600; color: #475569; }
    .btn-add-item { background: #e2e8f0; border: none; padding: 3px 8px; border-radius: 4px; font-size: 11px; cursor: pointer; font-weight: 600; }
    .items-list { display: flex; flex-direction: column; gap: 8px; }
    .item-row { display: flex; gap: 8px; align-items: center; }
    .item-row input { padding: 8px 10px; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 12px; }
    .btn-del-item { background: #fee2e2; border: none; color: #dc2626; width: 28px; height: 28px; border-radius: 4px; cursor: pointer; }
    .form-actions { display: flex; justify-content: flex-end; margin-top: 24px; }
    .btn-submit { background: #2563eb; color: #fff; border: none; padding: 12px 24px; border-radius: 8px; font-size: 14px; font-weight: 700; cursor: pointer; }
    .btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }
    .alert-error { background: #fee2e2; border-left: 4px solid #ef4444; color: #991b1b; padding: 12px; border-radius: 6px; margin-bottom: 16px; }
  `]
})
export class MarcheCreateComponent implements OnInit {
  form: FormGroup;
  besoinsValides: ExpressionBesoin[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private marcheService: MarcheService,
    private besoinService: BesoinService,
    private router: Router
  ) {
    this.form = this.fb.group({
      objet: ['', Validators.required],
      budgetPrevisionnel: [null],
      dateLancement: [null],
      description: [''],
      lots: this.fb.array([this.creerLotFormGroup(1)])
    });
  }

  ngOnInit(): void {
    this.besoinService.listerValides().subscribe({
      next: (res) => {
        if (res.succes && res.donnees) {
          this.besoinsValides = res.donnees;
        }
      }
    });
  }

  get lotsArray(): FormArray {
    return this.form.get('lots') as FormArray;
  }

  getLignesLotArray(lotIndex: number): FormArray {
    return this.lotsArray.at(lotIndex).get('lignesLot') as FormArray;
  }

  creerLotFormGroup(num: number): FormGroup {
    return this.fb.group({
      numero: [num, Validators.required],
      designation: ['', Validators.required],
      montantEstime: [null],
      lignesLot: this.fb.array([this.creerLigneLotFormGroup()])
    });
  }

  creerLigneLotFormGroup(): FormGroup {
    return this.fb.group({
      designation: ['', Validators.required],
      quantite: [1, [Validators.required, Validators.min(1)]],
      prixUnitaire: [null]
    });
  }

  ajouterLot(): void {
    const nextNum = this.lotsArray.length + 1;
    this.lotsArray.push(this.creerLotFormGroup(nextNum));
  }

  supprimerLot(index: number): void {
    this.lotsArray.removeAt(index);
  }

  ajouterLigneLot(lotIndex: number): void {
    this.getLignesLotArray(lotIndex).push(this.creerLigneLotFormGroup());
  }

  supprimerLigneLot(lotIndex: number, itemIndex: number): void {
    this.getLignesLotArray(lotIndex).removeAt(itemIndex);
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading = true;
    this.errorMessage = '';

    this.marcheService.creerMarche(this.form.value).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.succes) {
          this.router.navigate(['/marches', res.donnees?.id]);
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Erreur lors de la création du marché';
      }
    });
  }
}
