import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';
import { MainLayoutComponent } from './layout/main-layout.component';
import { LoginComponent } from './pages/login/login.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { BesoinListComponent } from './pages/besoins/besoin-list.component';
import { BesoinCreateComponent } from './pages/besoins/besoin-create.component';
import { BesoinDetailComponent } from './pages/besoins/besoin-detail.component';
import { BesoinEditComponent } from './pages/besoins/besoin-edit.component';
import { MarcheListComponent } from './pages/marches/marche-list.component';
import { MarcheCreateComponent } from './pages/marches/marche-create.component';
import { MarcheDetailComponent } from './pages/marches/marche-detail.component';
import { LotDetailComponent } from './pages/lots/lot-detail.component';
import { LotAttributionComponent } from './pages/lots/lot-attribution.component';
import { FournisseurListComponent } from './pages/fournisseurs/fournisseur-list.component';
import { FournisseurCreateComponent } from './pages/fournisseurs/fournisseur-create.component';
import { RapportsComponent } from './pages/rapports/rapports.component';
import { ChatbotComponent } from './pages/chatbot/chatbot.component';
import { ProfilComponent } from './pages/profil/profil.component';
import { ForbiddenComponent } from './pages/forbidden/forbidden.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      
      // Expressions de besoins
      { path: 'besoins', component: BesoinListComponent },
      { path: 'besoins/mes-besoins', component: BesoinListComponent },
      {
        path: 'besoins/nouveau',
        component: BesoinCreateComponent,
        canActivate: [roleGuard],
        data: { roles: ['CHEF_SERVICE', 'ADMIN'] }
      },
      { path: 'besoins/:id', component: BesoinDetailComponent },
      {
        path: 'besoins/:id/modifier',
        component: BesoinEditComponent,
        canActivate: [roleGuard],
        data: { roles: ['CHEF_SERVICE', 'ADMIN'] }
      },

      // Marchés et Lots
      {
        path: 'marches',
        component: MarcheListComponent,
        canActivate: [roleGuard],
        data: { roles: ['SERVICE_ACHATS', 'DIRECTION', 'ADMIN'] }
      },
      {
        path: 'marches/nouveau',
        component: MarcheCreateComponent,
        canActivate: [roleGuard],
        data: { roles: ['SERVICE_ACHATS', 'ADMIN'] }
      },
      {
        path: 'marches/:id',
        component: MarcheDetailComponent,
        canActivate: [roleGuard],
        data: { roles: ['SERVICE_ACHATS', 'DIRECTION', 'ADMIN'] }
      },
      {
        path: 'lots/:id',
        component: LotDetailComponent,
        canActivate: [roleGuard],
        data: { roles: ['SERVICE_ACHATS', 'DIRECTION', 'ADMIN'] }
      },
      {
        path: 'lots/:id/attribution',
        component: LotAttributionComponent,
        canActivate: [roleGuard],
        data: { roles: ['SERVICE_ACHATS', 'ADMIN'] }
      },

      // Fournisseurs
      {
        path: 'fournisseurs',
        component: FournisseurListComponent,
        canActivate: [roleGuard],
        data: { roles: ['SERVICE_ACHATS', 'DIRECTION', 'ADMIN'] }
      },
      {
        path: 'fournisseurs/nouveau',
        component: FournisseurCreateComponent,
        canActivate: [roleGuard],
        data: { roles: ['SERVICE_ACHATS', 'ADMIN'] }
      },

      // Rapports & Décisionnel
      {
        path: 'rapports',
        component: RapportsComponent,
        canActivate: [roleGuard],
        data: { roles: ['DIRECTION', 'ADMIN'] }
      },

      // Assistant Chatbot
      { path: 'chatbot', component: ChatbotComponent },

      // Mon profil utilisateur
      { path: 'profil', component: ProfilComponent },

      // Page d'accès refusé (403)
      { path: 'interdit', component: ForbiddenComponent }
    ]
  },
  { path: '**', component: NotFoundComponent }
];
