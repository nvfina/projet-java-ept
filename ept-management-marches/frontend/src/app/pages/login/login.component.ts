import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
    <div class="login-wrapper">
      <div class="login-card">
        <div class="login-header">
          <div class="ept-badge">EPT</div>
          <h1>Système de Management des Marchés</h1>
          <p class="subtitle">École Polytechnique de Thiès — Plateforme Officielle</p>
        </div>

        <div *ngIf="errorMessage" class="alert-error">
          <span>⚠️</span> {{ errorMessage }}
        </div>

        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="login-form">
          <div class="form-group">
            <label for="email">Adresse E-mail Universitaire</label>
            <input
              id="email"
              type="email"
              formControlName="email"
              placeholder="ex: chef.info@ept.sn"
              [class.invalid]="loginForm.get('email')?.invalid && loginForm.get('email')?.touched"
            />
            <small *ngIf="loginForm.get('email')?.invalid && loginForm.get('email')?.touched" class="field-error">
              Veuillez saisir une adresse email valide
            </small>
          </div>

          <div class="form-group">
            <label for="motDePasse">Mot de Passe</label>
            <input
              id="motDePasse"
              type="password"
              formControlName="motDePasse"
              placeholder="••••••••"
              [class.invalid]="loginForm.get('motDePasse')?.invalid && loginForm.get('motDePasse')?.touched"
            />
            <small *ngIf="loginForm.get('motDePasse')?.invalid && loginForm.get('motDePasse')?.touched" class="field-error">
              Le mot de passe est obligatoire
            </small>
          </div>

          <button type="submit" [disabled]="loginForm.invalid || loading" class="btn-submit">
            <span *ngIf="loading" class="spinner"></span>
            <span>{{ loading ? 'Connexion en cours...' : 'Se connecter' }}</span>
          </button>
        </form>

        <div class="demo-accounts-box">
          <div class="demo-title">⚡ Connexion rapide (Comptes de démonstration) :</div>
          <div class="demo-buttons">
            <button type="button" (click)="remplirCompte('chef.info@ept.sn', 'Chef@2024')">
              👨‍💼 Chef Informatique
            </button>
            <button type="button" (click)="remplirCompte('achats@ept.sn', 'Achats@2024')">
              🛒 Service Achats
            </button>
            <button type="button" (click)="remplirCompte('direction@ept.sn', 'Direction@2024')">
              🏛️ Direction EPT
            </button>
            <button type="button" (click)="remplirCompte('admin@ept.sn', 'Admin@2024')">
              ⚙️ Administrateur
            </button>
          </div>
        </div>

        <div class="login-footer">
          <span>Master 1 Ingénierie Logicielle & IA — EPT 2025/2026</span>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-wrapper {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #0f172a 0%, #1e3a8a 100%);
      padding: 20px;
    }
    .login-card {
      background: #ffffff;
      width: 100%;
      max-width: 480px;
      border-radius: 12px;
      box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.2), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
      padding: 36px;
    }
    .login-header {
      text-align: center;
      margin-bottom: 24px;
    }
    .ept-badge {
      display: inline-block;
      background: #d97706;
      color: #fff;
      font-weight: 900;
      font-size: 20px;
      padding: 6px 16px;
      border-radius: 6px;
      letter-spacing: 2px;
      margin-bottom: 12px;
    }
    .login-header h1 {
      font-size: 20px;
      font-weight: 700;
      color: #0f172a;
      margin: 0 0 6px 0;
    }
    .login-header .subtitle {
      font-size: 13px;
      color: #64748b;
      margin: 0;
    }
    .alert-error {
      background: #fee2e2;
      border-left: 4px solid #ef4444;
      color: #991b1b;
      padding: 10px 14px;
      border-radius: 6px;
      font-size: 13px;
      margin-bottom: 18px;
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .form-group {
      margin-bottom: 18px;
    }
    .form-group label {
      display: block;
      font-size: 13px;
      font-weight: 600;
      color: #334155;
      margin-bottom: 6px;
    }
    .form-group input {
      width: 100%;
      padding: 11px 14px;
      font-size: 14px;
      border: 1px solid #cbd5e1;
      border-radius: 8px;
      outline: none;
      box-sizing: border-box;
      transition: border-color 0.2s;
    }
    .form-group input:focus {
      border-color: #2563eb;
      box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
    }
    .form-group input.invalid {
      border-color: #ef4444;
    }
    .field-error {
      color: #ef4444;
      font-size: 11px;
      margin-top: 4px;
      display: block;
    }
    .btn-submit {
      width: 100%;
      background: #2563eb;
      color: #ffffff;
      border: none;
      padding: 12px;
      border-radius: 8px;
      font-size: 15px;
      font-weight: 600;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      transition: background 0.2s;
    }
    .btn-submit:hover:not(:disabled) {
      background: #1d4ed8;
    }
    .btn-submit:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
    .demo-accounts-box {
      margin-top: 24px;
      background: #f8fafc;
      border: 1px dashed #cbd5e1;
      border-radius: 8px;
      padding: 14px;
    }
    .demo-title {
      font-size: 12px;
      font-weight: 700;
      color: #475569;
      margin-bottom: 10px;
    }
    .demo-buttons {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 8px;
    }
    .demo-buttons button {
      background: #ffffff;
      border: 1px solid #e2e8f0;
      padding: 6px 10px;
      border-radius: 6px;
      font-size: 12px;
      font-weight: 600;
      color: #1e293b;
      cursor: pointer;
      text-align: left;
      transition: all 0.2s;
    }
    .demo-buttons button:hover {
      background: #f1f5f9;
      border-color: #cbd5e1;
    }
    .login-footer {
      text-align: center;
      margin-top: 24px;
      font-size: 11px;
      color: #94a3b8;
    }
  `]
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      motDePasse: ['', [Validators.required]]
    });
  }

  remplirCompte(email: string, mdp: string) {
    this.loginForm.patchValue({ email, motDePasse: mdp });
    this.errorMessage = '';
  }

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.loading = true;
    this.errorMessage = '';

    this.auth.login(this.loginForm.value).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.succes) {
          this.router.navigate(['/dashboard']);
        } else {
          this.errorMessage = res.message || 'Échec de connexion';
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Identifiants incorrects ou serveur indisponible';
      }
    });
  }
}
