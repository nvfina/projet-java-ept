import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <h2>Mon Profil Utilisateur</h2>
        <p class="subtitle">Consultez vos informations personnelles et modifiez votre mot de passe</p>
      </div>

      <div class="profile-grid">
        <!-- Informations personnelles -->
        <div class="card info-card">
          <div class="card-header">
            <h3>Informations Générales</h3>
          </div>
          <div class="card-body">
            <div class="user-avatar-large">
              {{ auth.currentUser()?.prenom?.charAt(0) }}{{ auth.currentUser()?.nom?.charAt(0) }}
            </div>
            
            <div class="info-list">
              <div class="info-item">
                <span class="label">Prénom & Nom :</span>
                <span class="value">{{ auth.currentUser()?.prenom }} {{ auth.currentUser()?.nom }}</span>
              </div>
              <div class="info-item">
                <span class="label">Adresse Email :</span>
                <span class="value">{{ auth.currentUser()?.email }}</span>
              </div>
              <div class="info-item">
                <span class="label">Rôle d'accès :</span>
                <span class="badge-role">{{ auth.currentUser()?.role }}</span>
              </div>
              <div class="info-item">
                <span class="label">Département / Service :</span>
                <span class="value">{{ auth.currentUser()?.serviceNom || 'Direction Générale / Non assigné' }}</span>
              </div>
              <div class="info-item">
                <span class="label">Statut du compte :</span>
                <span class="status-active">● Actif</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Modification du mot de passe -->
        <div class="card form-card">
          <div class="card-header">
            <h3>Sécurité & Mot de passe</h3>
          </div>
          <div class="card-body">
            <div *ngIf="successMessage" class="alert-success">
              {{ successMessage }}
            </div>
            <div *ngIf="errorMessage" class="alert-error">
              {{ errorMessage }}
            </div>

            <form [formGroup]="passwordForm" (ngSubmit)="changerMotDePasse()">
              <div class="form-group">
                <label>Mot de passe actuel</label>
                <input type="password" formControlName="ancienMotDePasse" placeholder="Entrez votre mot de passe actuel" />
                <span class="error-text" *ngIf="passwordForm.get('ancienMotDePasse')?.touched && passwordForm.get('ancienMotDePasse')?.invalid">
                  Le mot de passe actuel est requis.
                </span>
              </div>

              <div class="form-group">
                <label>Nouveau mot de passe</label>
                <input type="password" formControlName="nouveauMotDePasse" placeholder="Minimum 8 caractères" />
                <span class="error-text" *ngIf="passwordForm.get('nouveauMotDePasse')?.touched && passwordForm.get('nouveauMotDePasse')?.invalid">
                  Le mot de passe doit comporter au moins 8 caractères.
                </span>
              </div>

              <div class="form-group">
                <label>Confirmer le nouveau mot de passe</label>
                <input type="password" formControlName="confirmation" placeholder="Confirmez le nouveau mot de passe" />
                <span class="error-text" *ngIf="passwordForm.errors?.['mismatch'] && passwordForm.get('confirmation')?.touched">
                  Les deux mots de passe ne correspondent pas.
                </span>
              </div>

              <button type="submit" [disabled]="passwordForm.invalid || loading" class="btn-submit">
                {{ loading ? 'Modification en cours...' : 'Mettre à jour mon mot de passe' }}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-container { display: flex; flex-direction: column; gap: 20px; }
    .page-header h2 { margin: 0; font-size: 22px; color: #0f172a; }
    .subtitle { margin: 4px 0 0 0; font-size: 13px; color: #64748b; }
    .profile-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
    @media (max-width: 900px) { .profile-grid { grid-template-columns: 1fr; } }
    .card { background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; }
    .card-header { padding: 16px 20px; border-bottom: 1px solid #e2e8f0; background: #f8fafc; }
    .card-header h3 { margin: 0; font-size: 15px; color: #1e293b; }
    .card-body { padding: 24px; }
    .user-avatar-large {
      width: 64px; height: 64px; border-radius: 50%; background: #2563eb; color: #fff;
      display: flex; align-items: center; justify-content: center; font-size: 22px; font-weight: 800;
      margin-bottom: 20px;
    }
    .info-list { display: flex; flex-direction: column; gap: 14px; }
    .info-item { display: flex; justify-content: space-between; align-items: center; padding-bottom: 10px; border-bottom: 1px solid #f1f5f9; }
    .info-item .label { font-size: 13px; color: #64748b; font-weight: 500; }
    .info-item .value { font-size: 13px; color: #0f172a; font-weight: 600; }
    .badge-role { background: #eff6ff; color: #1d4ed8; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 700; }
    .status-active { color: #10b981; font-size: 13px; font-weight: 600; }
    .form-group { margin-bottom: 16px; display: flex; flex-direction: column; gap: 6px; }
    .form-group label { font-size: 13px; font-weight: 600; color: #334155; }
    .form-group input {
      padding: 10px 14px; border: 1px solid #cbd5e1; border-radius: 8px; font-size: 13px; outline: none;
    }
    .form-group input:focus { border-color: #2563eb; }
    .error-text { font-size: 11px; color: #dc2626; }
    .btn-submit {
      background: #2563eb; color: #fff; border: none; padding: 12px 20px; border-radius: 8px;
      font-size: 13px; font-weight: 600; cursor: pointer; width: 100%; transition: background 0.2s;
    }
    .btn-submit:hover:not(:disabled) { background: #1d4ed8; }
    .btn-submit:disabled { background: #94a3b8; cursor: not-allowed; }
    .alert-success { background: #d1fae5; color: #065f46; padding: 12px; border-radius: 8px; margin-bottom: 16px; font-size: 13px; }
    .alert-error { background: #fee2e2; color: #991b1b; padding: 12px; border-radius: 8px; margin-bottom: 16px; font-size: 13px; }
  `]
})
export class ProfilComponent implements OnInit {
  passwordForm!: FormGroup;
  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    public auth: AuthService
  ) {}

  ngOnInit(): void {
    this.passwordForm = this.fb.group({
      ancienMotDePasse: ['', [Validators.required]],
      nouveauMotDePasse: ['', [Validators.required, Validators.minLength(8)]],
      confirmation: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(g: FormGroup) {
    const p1 = g.get('nouveauMotDePasse')?.value;
    const p2 = g.get('confirmation')?.value;
    return p1 === p2 ? null : { mismatch: true };
  }

  changerMotDePasse(): void {
    if (this.passwordForm.invalid) return;

    this.loading = true;
    this.successMessage = '';
    this.errorMessage = '';

    const { ancienMotDePasse, nouveauMotDePasse } = this.passwordForm.value;

    this.auth.changePassword(ancienMotDePasse, nouveauMotDePasse).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.succes) {
          this.successMessage = 'Votre mot de passe a été modifié avec succès.';
          this.passwordForm.reset();
        } else {
          this.errorMessage = res.message || 'Échec de la modification du mot de passe.';
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Erreur lors de la mise à jour du mot de passe.';
      }
    });
  }
}
