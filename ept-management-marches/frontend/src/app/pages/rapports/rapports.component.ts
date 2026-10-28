import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../services/dashboard.service';

@Component({
  selector: 'app-rapports',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <div class="page-header">
        <div>
          <h2>Génération et Exportation des Rapports</h2>
          <p class="subtitle">Extraction des données consolidées pour les audits, la Direction et les bailleurs</p>
        </div>
      </div>

      <div class="reports-grid">
        <!-- Rapport 1 : Marchés et Lots -->
        <div class="report-card">
          <div class="report-icon">🏛️</div>
          <div class="report-details">
            <h3>Rapport Exhaustif des Marchés Publics</h3>
            <p>Exportation de l'ensemble des marchés enregistrés, budgets alloués, prestataires retenus et montants engagés.</p>
            <div class="report-actions">
              <button (click)="telechargerMarchesExcel()" class="btn-excel">
                📊 Télécharger en Excel (.xlsx)
              </button>
              <button (click)="telechargerCSV('marches')" class="btn-csv">
                📄 Exporter en CSV
              </button>
            </div>
          </div>
        </div>

        <!-- Rapport 2 : Dépenses par Département -->
        <div class="report-card">
          <div class="report-icon">💰</div>
          <div class="report-details">
            <h3>Rapport Budgétaire et Dépenses par Département</h3>
            <p>Ventilation financière des acquisitions par service demandeur (Informatique, Génie Civil, Électrique, Mécanique).</p>
            <div class="report-actions">
              <button (click)="telechargerDepensesExcel()" class="btn-excel">
                📊 Télécharger en Excel (.xlsx)
              </button>
              <button (click)="telechargerCSV('depenses')" class="btn-csv">
                📄 Exporter en CSV
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Informations d'audit -->
      <div class="audit-notice">
        <h4>ℹ️ Note sur l'intégrité des rapports générés</h4>
        <p>
          Tous les rapports sont générés dynamiquement par le service <strong>dashboard-service</strong> à partir des données
          réelles persistées dans les bases de données MySQL de l'application. Aucune donnée fictive n'est injectée.
        </p>
      </div>
    </div>
  `,
  styles: [`
    .page-container { display: flex; flex-direction: column; gap: 24px; max-width: 900px; }
    .page-header h2 { margin: 0; font-size: 22px; color: #0f172a; }
    .subtitle { margin: 4px 0 0 0; font-size: 13px; color: #64748b; }
    .reports-grid { display: flex; flex-direction: column; gap: 20px; }
    .report-card {
      background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 24px;
      display: flex; gap: 20px; align-items: flex-start;
    }
    .report-icon {
      font-size: 36px; background: #f8fafc; width: 64px; height: 64px; border-radius: 12px;
      display: flex; align-items: center; justify-content: center;
    }
    .report-details { flex: 1; }
    .report-details h3 { margin: 0 0 6px 0; font-size: 16px; color: #0f172a; }
    .report-details p { margin: 0 0 16px 0; font-size: 13px; color: #64748b; line-height: 1.5; }
    .report-actions { display: flex; gap: 12px; }
    .btn-excel {
      background: #107c41; color: #ffffff; border: none; padding: 10px 18px; border-radius: 6px;
      font-size: 13px; font-weight: 700; cursor: pointer; display: flex; align-items: center; gap: 8px;
    }
    .btn-excel:hover { background: #0b5c30; }
    .btn-csv {
      background: #f1f5f9; color: #334155; border: 1px solid #cbd5e1; padding: 10px 18px;
      border-radius: 6px; font-size: 13px; font-weight: 600; cursor: pointer;
    }
    .btn-csv:hover { background: #e2e8f0; }
    .audit-notice {
      background: #eff6ff; border: 1px solid #bfdbfe; border-radius: 10px; padding: 16px 20px; font-size: 13px;
    }
    .audit-notice h4 { margin: 0 0 6px 0; font-size: 14px; color: #1e40af; }
    .audit-notice p { margin: 0; color: #1e3a8a; line-height: 1.5; }
  `]
})
export class RapportsComponent {
  constructor(private dashboardService: DashboardService) {}

  telechargerMarchesExcel(): void {
    this.dashboardService.telechargerRapportMarchesExcel().subscribe((blob) => {
      this.declencherTelechargement(blob, 'rapport-marches-ept.xlsx');
    });
  }

  telechargerDepensesExcel(): void {
    this.dashboardService.telechargerRapportDepensesExcel().subscribe((blob) => {
      this.declencherTelechargement(blob, 'rapport-depenses-ept.xlsx');
    });
  }

  telechargerCSV(type: string): void {
    this.dashboardService.telechargerRapportCSV(type).subscribe((blob) => {
      this.declencherTelechargement(blob, `rapport-ept-${type}.csv`);
    });
  }

  private declencherTelechargement(blob: Blob, nomFichier: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = nomFichier;
    a.click();
    window.URL.revokeObjectURL(url);
  }
}
