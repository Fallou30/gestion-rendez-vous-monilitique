// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';
import { TypeUtilisateur } from './core/models/utilisateur/utilisateur.module';

// Components
import { HomeComponent } from './pages/home/home.component';

import { PatientRegistrationComponent } from './features/auth/patient-registration/patient-registration.component';
import { UserManagementComponent } from './components/user-management/user-management.component';

import { LoginComponent } from './features/auth/login/login/login.component';
import { UnauthorizedComponent } from './pages/unauthorized/unauthorized.component';

// Dashboards
import { PatientDashboardComponent } from './dashboards/patient/patient-dashboard/patient-dashboard.component';

import { ReceptionnisteDashboardComponent } from './dashboards/receptionniste-dashboard/receptionniste-dashboard.component';
import { AdminDashboardComponent } from './dashboards/admin/admin-dashboard/admin-dashboard.component';
import { SuperAdminDashboardComponent } from './dashboards/super-admin-dashboard/super-admin-dashboard.component';
import { MedecinDashboardComponent } from './dashboards/medecin/medecien-dashboard/medecien-dashboard.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AdminFormComponent } from './components/admin-form/admin-form.component';
import { MedecinFormComponent } from './components/medecin-form/medecin-form.component';
import { PatientFormComponent } from './components/patient-form/patient-form.component';
import { ReceptionnisteFormComponent } from './components/receptionniste-form/receptionniste-form.component';
import { HopitalFormComponent } from './components/hopital-form/hopital-form.component';
import { HopitalServicesComponent } from './components/hopital-services/hopital-services.component';
import { MedecinDemandeComponent } from './components/medecin-demande/medecin-demande.component';
import { ValidationDemandesComponent } from './components/validation-demandes/validation-demandes.component';
import { HopitalListComponent } from './components/hopital-list/hopital-list.component';
import { ServiceListComponent } from './components/service-list/service-list.component';
import { PriseRendezVousComponent } from './dashboards/patient/prise-rendez-vous/prise-rendez-vous.component';
import { CalendrierMedecinComponent } from './dashboards/medecin/calendrier/calendrier.component';
import { AdminRendezVousComponent } from './dashboards/admin/admin-rendez-vous/admin-rendez-vous.component';

export const routes: Routes = [
  // Public routes
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register/patient', component: PatientRegistrationComponent },
  { path: 'auth/register/medecin', component: MedecinDemandeComponent },
  { path: 'unauthorized', component: UnauthorizedComponent },

  // Patient routes
  {
    path: 'patient/dashboard',
    component: PatientDashboardComponent,
    canActivate: [AuthGuard],
    data: { roles: [TypeUtilisateur.PATIENT] },
  },
  {
    path: 'prendre/rendez-vous',
    component:PriseRendezVousComponent,
    canActivate: [AuthGuard],
    data: { roles: [TypeUtilisateur.PATIENT] },
  },
 

  // Médecin routes
  {
    path: 'medecin/dashboard',
    component: MedecinDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [TypeUtilisateur.MEDECIN] },
  },
   {
    path: 'medecin/calendrier',
    component: CalendrierMedecinComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [TypeUtilisateur.MEDECIN] },
  },

  // Réceptionniste routes
  {
    path: 'receptionniste/dashboard',
    component: ReceptionnisteDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [TypeUtilisateur.RECEPTIONNISTE] },
  },

  // Admin routes
  {
    path: 'admin/dashboard',
    component: AdminDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [TypeUtilisateur.ADMIN] },
  },

  // Super Admin routes
  {
    path: 'super-admin/dashboard',
    component: SuperAdminDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [TypeUtilisateur.SUPER_ADMIN] },
  },

  // Validation des médecins
  {
    path: 'admin/validate-medecins',
    component: ValidationDemandesComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [TypeUtilisateur.ADMIN, TypeUtilisateur.SUPER_ADMIN] },
  },

  // Gestion des utilisateurs
  {
    path: 'admin/users',
    component: UserManagementComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [TypeUtilisateur.ADMIN, TypeUtilisateur.SUPER_ADMIN] },
  },

  // Gestion du profil
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [AuthGuard],
  },
  {
    path:'admin/patient/nouveau',
    component:PatientFormComponent,
    canActivate: [AuthGuard],
  },
   {
    path: 'admin/medecins/nouveau',
    component: MedecinFormComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'admin/medecins/modifier/:id',
    component: MedecinFormComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'demande/medecin',
    component: MedecinDemandeComponent,
  },
  {
    path: 'admin/patients/modifier/:id',
    component: PatientFormComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'admin/receptionnistes/modifier/:id',
    component: ReceptionnisteFormComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'admin/administrateurs/modifier/:id',
    component: AdminFormComponent,
    canActivate: [AuthGuard],
  },
   {
    path: 'admin/rendez-vous',
    component: AdminRendezVousComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'editer/hopital/:id',
    component: HopitalFormComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'hopitaux/nouveau',
    component: HopitalFormComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'hopitaux/:id/services',
    component: HopitalServicesComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'hopitaux',
    component: HopitalListComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'hopitaux/personnel',
    component: HopitalListComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'services',
    component: ServiceListComponent,
    canActivate: [AuthGuard],
  },

  // Wildcard route (doit être la dernière)
  { path: '**', redirectTo: '/home' },
];
