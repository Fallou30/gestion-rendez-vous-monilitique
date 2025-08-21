import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-patient-info-header',
  template: `
    <div class="patient-header">
      <div class="patient-header-left">
        <div class="patient-avatar">
          <i class="fas fa-user-injured"></i>
        </div>
        
        <div class="patient-info">
          <h2>{{ patient?.prenom }} {{ patient?.nom }}</h2>
          <div class="patient-details">
            <span class="detail-item">
              <i class="fas fa-birthday-cake"></i>
              {{ calculateAge(patient?.dateNaissance) }} ans ({{ formatDate(patient?.dateNaissance) }})
            </span>
            <span class="detail-item">
              <i class="fas fa-venus-mars"></i>
              {{ patient?.sexe }}
            </span>
            <span class="detail-item">
              <i class="fas fa-phone"></i>
              {{ patient?.telephone }}
            </span>
            <span class="detail-item">
              <i class="fas fa-envelope"></i>
              {{ patient?.email }}
            </span>
          </div>
        </div>
      </div>

      <div class="patient-header-center" *ngIf="rendezVousActuel">
        <div class="rdv-info">
          <h3><i class="fas fa-calendar-check"></i> Rendez-vous en cours</h3>
          <p>{{ rendezVousActuel.typeConsultation }}</p>
          <p>{{ formatTime(rendezVousActuel.dateHeureDebut) }} - {{ formatTime(rendezVousActuel.dateHeureFin) }}</p>
        </div>
      </div>

      <div class="patient-header-right">
        <div class="header-actions">
          <button (click)="onImprimerDossier.emit()" class="btn btn-outline">
            <i class="fas fa-print"></i> Imprimer
          </button>
          <button (click)="onRetourDashboard.emit()" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Retour
          </button>
        </div>
      </div>
    </div>

    <!-- Alertes importantes -->
    <div class="patient-alerts" *ngIf="patient?.alertes?.length > 0">
      <div 
        *ngFor="let alerte of patient.alertes" 
        class="alert-item"
        [class]="'alert-' + alerte.type">
        <i class="fas fa-exclamation-triangle"></i>
        <span>{{ alerte.message }}</span>
      </div>
    </div>
  `,
  styles: [`
    .patient-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 1.5rem;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-radius: 10px;
      margin-bottom: 1rem;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }

    .patient-header-left {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .patient-avatar {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.2);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 2rem;
    }

    .patient-info h2 {
      margin: 0 0 0.5rem 0;
      font-size: 1.8rem;
      font-weight: 600;
    }

    .patient-details {
      display: flex;
      flex-wrap: wrap;
      gap: 1rem;
      opacity: 0.9;
    }

    .detail-item {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 0.9rem;
    }

    .detail-item i {
      opacity: 0.8;
    }

    .patient-header-center {
      text-align: center;
    }

    .rdv-info h3 {
      margin: 0 0 0.5rem 0;
      font-size: 1.2rem;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      justify-content: center;
    }

    .rdv-info p {
      margin: 0.25rem 0;
      opacity: 0.9;
    }

    .header-actions {
      display: flex;
      gap: 0.5rem;
    }

    .btn {
      padding: 0.5rem 1rem;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      font-size: 0.9rem;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      transition: all 0.3s ease;
    }

    .btn-outline {
      background: rgba(255, 255, 255, 0.2);
      color: white;
      border: 1px solid rgba(255, 255, 255, 0.3);
    }

    .btn-outline:hover {
      background: rgba(255, 255, 255, 0.3);
    }

    .btn-secondary {
      background: rgba(0, 0, 0, 0.2);
      color: white;
    }

    .btn-secondary:hover {
      background: rgba(0, 0, 0, 0.3);
    }

    .patient-alerts {
      margin-bottom: 1rem;
    }

    .alert-item {
      padding: 0.75rem 1rem;
      margin-bottom: 0.5rem;
      border-radius: 5px;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-weight: 500;
    }

    .alert-danger {
      background: #fee;
      color: #c53030;
      border-left: 4px solid #c53030;
    }

    .alert-warning {
      background: #fffbeb;
      color: #d69e2e;
      border-left: 4px solid #d69e2e;
    }

    .alert-info {
      background: #ebf8ff;
      color: #3182ce;
      border-left: 4px solid #3182ce;
    }

    @media (max-width: 768px) {
      .patient-header {
        flex-direction: column;
        gap: 1rem;
      }

      .patient-details {
        flex-direction: column;
        gap: 0.5rem;
      }
    }
  `],
  standalone: true,
  imports: [CommonModule]
})
export class PatientInfoHeaderComponent {
  @Input() patient: any;
  @Input() rendezVousActuel: any;
  @Output() onRetourDashboard = new EventEmitter<void>();
  @Output() onImprimerDossier = new EventEmitter<void>();

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR');
  }

  formatTime(date: string): string {
    return new Date(date).toLocaleTimeString('fr-FR', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  calculateAge(birthDate: string): number {
    const today = new Date();
    const birth = new Date(birthDate);
    let age = today.getFullYear() - birth.getFullYear();
    const monthDiff = today.getMonth() - birth.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
      age--;
    }
    
    return age;
  }
}