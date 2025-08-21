import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Examen, ExamenService, CreateExamenRequest, ExamenResultatsRequest } from '../../../core/services/examen.service';

@Component({
  selector: 'app-examens',
  template: `
    <div class="examens-container">
      <div class="examens-header">
        <h3><i class="fas fa-stethoscope"></i> Examens médicaux</h3>
        <div class="examens-actions">
          <button type="button" (click)="showPrescrireModal = true" class="btn btn-primary">
            <i class="fas fa-plus"></i> Prescrire un examen
          </button>
          <button type="button" (click)="loadExamensEnAttente()" class="btn btn-info">
            <i class="fas fa-clock"></i> Examens en attente
          </button>
          <button type="button" (click)="loadExamensUrgents()" class="btn btn-warning">
            <i class="fas fa-exclamation-triangle"></i> Examens urgents
          </button>
        </div>
      </div>

      <div class="examens-content">
        <!-- Filtres -->
        <div class="filters-section">
          <div class="form-row">
            <div class="form-group col-md-4">
              <label>Filtrer par statut</label>
              <select [(ngModel)]="filtreStatut" (change)="appliquerFiltres()" class="form-control">
                <option value="">Tous les statuts</option>
                <option value="prescrit">Prescrit</option>
                <option value="programme">Programmé</option>
                <option value="realise">Réalisé</option>
                <option value="interprete">Interprété</option>
              </select>
            </div>
            <div class="form-group col-md-4">
              <label>Filtrer par type</label>
              <select [(ngModel)]="filtreType" (change)="appliquerFiltres()" class="form-control">
                <option value="">Tous les types</option>
                <option value="biologique">Biologique</option>
                <option value="radiologique">Radiologique</option>
                <option value="cardiologique">Cardiologique</option>
                <option value="neurologique">Neurologique</option>
                <option value="autre">Autre</option>
              </select>
            </div>
            <div class="form-group col-md-4">
              <label>Filtrer par urgence</label>
              <select [(ngModel)]="filtreUrgence" (change)="appliquerFiltres()" class="form-control">
                <option value="">Toutes les urgences</option>
                <option value="true">Urgent</option>
                <option value="false">Non urgent</option>
              </select>
            </div>
          </div>
        </div>

        <!-- Liste des examens -->
        <div class="examens-list">
          <div class="examen-item" *ngFor="let examen of examensAffiches" 
               [class.urgent]="examen.urgence">
            <div class="examen-header">
              <div class="examen-info">
                <h4>{{ examen.nomExamen }}</h4>
                <span class="examen-type">{{ examen.typeExamen }}</span>
                <span class="examen-statut" [class]="'statut-' + examen.statut">
                  {{ formatStatut(examen.statut) }}
                </span>
                <span class="examen-urgence" *ngIf="examen.urgence">
                  <i class="fas fa-exclamation-triangle"></i> URGENT
                </span>
              </div>
              <div class="examen-actions">
                <button type="button" (click)="voirExamen(examen)" 
                        class="btn btn-sm btn-outline-primary">
                  <i class="fas fa-eye"></i> Voir
                </button>
                <button type="button" (click)="programmerExamen(examen)" 
                        class="btn btn-sm btn-outline-info"
                        *ngIf="examen.statut === 'prescrit'">
                  <i class="fas fa-calendar"></i> Programmer
                </button>
                <button type="button" (click)="saisirResultats(examen)" 
                        class="btn btn-sm btn-outline-success"
                        *ngIf="examen.statut === 'programme' || examen.statut === 'realise'">
                  <i class="fas fa-edit"></i> Résultats
                </button>
              </div>
            </div>
            
            <div class="examen-details">
              <p class="examen-description">{{ examen.description }}</p>
              <div class="examen-meta">
                <span *ngIf="examen.dateRealisation">
                  <i class="fas fa-calendar"></i> 
                  Programmé le {{ formatDate(examen.dateRealisation) }}
                </span>
              </div>
              
              <div class="examen-resultats" *ngIf="examen.resultats">
                <h5>Résultats:</h5>
                <p>{{ examen.resultats }}</p>
                <div class="examen-interpretation" *ngIf="examen.interpretation">
                  <h6>Interprétation:</h6>
                  <p>{{ examen.interpretation }}</p>
                </div>
              </div>
            </div>
          </div>
          
          <div class="no-examens" *ngIf="examensAffiches.length === 0">
            <p><i class="fas fa-info-circle"></i> Aucun examen trouvé</p>
          </div>
        </div>
      </div>

      <!-- Modal pour prescrire un examen -->
      <div class="modal" *ngIf="showPrescrireModal" (click)="closePrescrireModal()">
        <div class="modal-content modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h4>Prescrire un examen</h4>
            <button type="button" (click)="closePrescrireModal()" class="close-btn">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div class="modal-body">
            <form [formGroup]="prescrireForm" (ngSubmit)="prescrireExamen()">
              <div class="form-group">
                <label for="typeExamen">Type d'examen *</label>
                <select id="typeExamen" formControlName="typeExamen" class="form-control">
                  <option value="">Sélectionner un type</option>
                  <option value="biologique">Biologique</option>
                  <option value="radiologique">Radiologique</option>
                  <option value="cardiologique">Cardiologique</option>
                  <option value="neurologique">Neurologique</option>
                  <option value="autre">Autre</option>
                </select>
              </div>

              <div class="form-group">
                <label for="nomExamen">Nom de l'examen *</label>
                <input type="text" id="nomExamen" formControlName="nomExamen" 
                       class="form-control" placeholder="Ex: Prise de sang, IRM, ECG...">
              </div>

              <div class="form-group">
                <label for="description">Description</label>
                <textarea id="description" formControlName="description" 
                         rows="3" class="form-control"
                         placeholder="Description détaillée de l'examen..."></textarea>
              </div>

              <div class="form-group">
                <div class="form-check">
                  <input type="checkbox" id="urgence" formControlName="urgence" class="form-check-input">
                  <label for="urgence" class="form-check-label">
                    <i class="fas fa-exclamation-triangle"></i> Examen urgent
                  </label>
                </div>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" (click)="closePrescrireModal()" class="btn btn-secondary">
              Annuler
            </button>
            <button type="button" (click)="prescrireExamen()" 
                    class="btn btn-primary" 
                    [disabled]="!prescrireForm.valid">
              <i class="fas fa-plus"></i> Prescrire
            </button>
          </div>
        </div>
      </div>

      <!-- Modal pour programmer un examen -->
      <div class="modal" *ngIf="showProgrammerModal" (click)="closeProgrammerModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h4>Programmer l'examen</h4>
            <button type="button" (click)="closeProgrammerModal()" class="close-btn">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div class="modal-body">
            <form [formGroup]="programmerForm" (ngSubmit)="confirmerProgrammation()">
              <div class="form-group">
                <label for="dateRealisation">Date de réalisation *</label>
                <input type="datetime-local" id="dateRealisation" 
                       formControlName="dateRealisation" class="form-control">
              </div>
              
              <div class="examen-info" *ngIf="examenSelectionne">
                <h5>{{ examenSelectionne.nomExamen }}</h5>
                <p>{{ examenSelectionne.description }}</p>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" (click)="closeProgrammerModal()" class="btn btn-secondary">
              Annuler
            </button>
            <button type="button" (click)="confirmerProgrammation()" 
                    class="btn btn-primary" 
                    [disabled]="!programmerForm.valid">
              <i class="fas fa-calendar"></i> Programmer
            </button>
          </div>
        </div>
      </div>

      <!-- Modal pour saisir les résultats -->
      <div class="modal" *ngIf="showResultatsModal" (click)="closeResultatsModal()">
        <div class="modal-content modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h4>Saisir les résultats</h4>
            <button type="button" (click)="closeResultatsModal()" class="close-btn">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div class="modal-body">
            <form [formGroup]="resultatsForm" (ngSubmit)="confirmerResultats()">
              <div class="examen-info" *ngIf="examenSelectionne">
                <h5>{{ examenSelectionne.nomExamen }}</h5>
                <p>{{ examenSelectionne.description }}</p>
              </div>

              <div class="form-group">
                <label for="resultats">Résultats *</label>
                <textarea id="resultats" formControlName="resultats" 
                         rows="5" class="form-control"
                         placeholder="Saisir les résultats de l'examen..."></textarea>
              </div>

              <div class="form-group">
                <label for="interpretation">Interprétation</label>
                <textarea id="interpretation" formControlName="interpretation" 
                         rows="3" class="form-control"
                         placeholder="Interprétation des résultats..."></textarea>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" (click)="closeResultatsModal()" class="btn btn-secondary">
              Annuler
            </button>
            <button type="button" (click)="confirmerResultats()" 
                    class="btn btn-primary" 
                    [disabled]="!resultatsForm.valid">
              <i class="fas fa-save"></i> Sauvegarder
            </button>
          </div>
        </div>
      </div>

      <!-- Modal pour voir les détails d'un examen -->
      <div class="modal" *ngIf="showDetailsModal" (click)="closeDetailsModal()">
        <div class="modal-content modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h4>Détails de l'examen</h4>
            <button type="button" (click)="closeDetailsModal()" class="close-btn">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div class="modal-body" *ngIf="examenSelectionne">
            <div class="examen-details-complet">
              <div class="detail-section">
                <h5>Informations générales</h5>
                <p><strong>Nom:</strong> {{ examenSelectionne.nomExamen }}</p>
                <p><strong>Type:</strong> {{ examenSelectionne.typeExamen }}</p>
                <p><strong>Statut:</strong> {{ formatStatut(examenSelectionne.statut) }}</p>
                <p><strong>Urgence:</strong> {{ examenSelectionne.urgence ? 'Oui' : 'Non' }}</p>
                <p *ngIf="examenSelectionne.dateRealisation">
                  <strong>Date de réalisation:</strong> {{ formatDate(examenSelectionne.dateRealisation) }}
                </p>
              </div>

              <div class="detail-section" *ngIf="examenSelectionne.description">
                <h5>Description</h5>
                <p>{{ examenSelectionne.description }}</p>
              </div>

              <div class="detail-section" *ngIf="examenSelectionne.resultats">
                <h5>Résultats</h5>
                <p>{{ examenSelectionne.resultats }}</p>
              </div>

              <div class="detail-section" *ngIf="examenSelectionne.interpretation">
                <h5>Interprétation</h5>
                <p>{{ examenSelectionne.interpretation }}</p>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" (click)="closeDetailsModal()" class="btn btn-secondary">
              Fermer
            </button>
            <button type="button" (click)="imprimerExamen()" class="btn btn-primary">
              <i class="fas fa-print"></i> Imprimer
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class ExamensComponent implements OnInit {
  @Input() patientId!: number;
  @Input() consultationId?: number;
  @Input() dossierMedical: any;

  examens: Examen[] = [];
  examensAffiches: Examen[] = [];
  
  // Filtres
  filtreStatut = '';
  filtreType = '';
  filtreUrgence = '';

  // Modals
  showPrescrireModal = false;
  showProgrammerModal = false;
  showResultatsModal = false;
  showDetailsModal = false;

  // Formulaires
  prescrireForm: FormGroup;
  programmerForm: FormGroup;
  resultatsForm: FormGroup;

  // Examen sélectionné
  examenSelectionne: Examen | null = null;

  constructor(
    private examenService: ExamenService,
    private fb: FormBuilder
  ) {
    this.prescrireForm = this.fb.group({
      typeExamen: ['', Validators.required],
      nomExamen: ['', Validators.required],
      description: [''],
      urgence: [false]
    });

    this.programmerForm = this.fb.group({
      dateRealisation: ['', Validators.required]
    });

    this.resultatsForm = this.fb.group({
      resultats: ['', Validators.required],
      interpretation: ['']
    });
  }

  ngOnInit(): void {
    this.loadExamensPatient();
  }

  loadExamensPatient(): void {
    this.examenService.getExamensPatient(this.patientId).subscribe({
      next: (examens) => {
        this.examens = examens;
        this.appliquerFiltres();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des examens:', error);
      }
    });
  }

  loadExamensEnAttente(): void {
    this.examenService.getExamensEnAttente().subscribe({
      next: (examens) => {
        this.examens = examens;
        this.appliquerFiltres();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des examens en attente:', error);
      }
    });
  }

  loadExamensUrgents(): void {
    this.examenService.getExamensUrgents().subscribe({
      next: (examens) => {
        this.examens = examens;
        this.appliquerFiltres();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des examens urgents:', error);
      }
    });
  }

  appliquerFiltres(): void {
    this.examensAffiches = this.examens.filter(examen => {
      const matchStatut = !this.filtreStatut || examen.statut === this.filtreStatut;
      const matchType = !this.filtreType || examen.typeExamen === this.filtreType;
      const matchUrgence = !this.filtreUrgence || examen.urgence.toString() === this.filtreUrgence;
      
      return matchStatut && matchType && matchUrgence;
    });
  }

  // Gestion des modals
  closePrescrireModal(): void {
    this.showPrescrireModal = false;
    this.prescrireForm.reset();
  }

  closeProgrammerModal(): void {
    this.showProgrammerModal = false;
    this.programmerForm.reset();
    this.examenSelectionne = null;
  }

  closeResultatsModal(): void {
    this.showResultatsModal = false;
    this.resultatsForm.reset();
    this.examenSelectionne = null;
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.examenSelectionne = null;
  }

  // Actions sur les examens
  prescrireExamen(): void {
    if (this.prescrireForm.valid) {
      const request: CreateExamenRequest = {
        consultationId: this.consultationId || 0,
        typeExamen: this.prescrireForm.value.typeExamen,
        nomExamen: this.prescrireForm.value.nomExamen,
        description: this.prescrireForm.value.description,
        urgence: this.prescrireForm.value.urgence
      };

      this.examenService.prescrireExamen(request).subscribe({
        next: (examen) => {
          this.examens.push(examen);
          this.appliquerFiltres();
          this.closePrescrireModal();
          alert('Examen prescrit avec succès!');
        },
        error: (error) => {
          console.error('Erreur lors de la prescription:', error);
          alert('Erreur lors de la prescription de l\'examen');
        }
      });
    }
  }

  programmerExamen(examen: Examen): void {
    this.examenSelectionne = examen;
    this.showProgrammerModal = true;
  }

  confirmerProgrammation(): void {
    if (this.programmerForm.valid && this.examenSelectionne) {
      const dateRealisation = this.programmerForm.value.dateRealisation;
      
      this.examenService.programmerExamen(this.examenSelectionne.id, dateRealisation).subscribe({
        next: (examen) => {
          const index = this.examens.findIndex(e => e.id === examen.id);
          if (index !== -1) {
            this.examens[index] = examen;
            this.appliquerFiltres();
          }
          this.closeProgrammerModal();
          alert('Examen programmé avec succès!');
        },
        error: (error) => {
          console.error('Erreur lors de la programmation:', error);
          alert('Erreur lors de la programmation de l\'examen');
        }
      });
    }
  }

  saisirResultats(examen: Examen): void {
    this.examenSelectionne = examen;
    this.resultatsForm.patchValue({
      resultats: examen.resultats || '',
      interpretation: examen.interpretation || ''
    });
    this.showResultatsModal = true;
  }

  confirmerResultats(): void {
    if (this.resultatsForm.valid && this.examenSelectionne) {
      const request: ExamenResultatsRequest = {
        resultats: this.resultatsForm.value.resultats,
        interpretation: this.resultatsForm.value.interpretation
      };

      this.examenService.saisirResultats(this.examenSelectionne.id, request).subscribe({
        next: (examen) => {
          const index = this.examens.findIndex(e => e.id === examen.id);
          if (index !== -1) {
            this.examens[index] = examen;
            this.appliquerFiltres();
          }
          this.closeResultatsModal();
          alert('Résultats sauvegardés avec succès!');
        },
        error: (error) => {
          console.error('Erreur lors de la sauvegarde:', error);
          alert('Erreur lors de la sauvegarde des résultats');
        }
      });
    }
  }

  voirExamen(examen: Examen): void {
    this.examenSelectionne = examen;
    this.showDetailsModal = true;
  }

  imprimerExamen(): void {
    if (this.examenSelectionne) {
      const contenu = this.genererContenuExamen(this.examenSelectionne);
      const fenetre = window.open('', '_blank');
      if (fenetre) {
        fenetre.document.write(`
          <html>
            <head>
              <title>Examen - ${this.examenSelectionne.nomExamen}</title>
              <style>
                body { font-family: Arial, sans-serif; margin: 20px; }
                .header { text-align: center; border-bottom: 2px solid #333; padding-bottom: 20px; }
                .section { margin: 20px 0; }
                .label { font-weight: bold; }
                .urgent { color: red; }
              </style>
            </head>
            <body>
              ${contenu}
            </body>
          </html>
        `);
        fenetre.document.close();
        fenetre.print();
      }
    }
  }

  private genererContenuExamen(examen: Examen): string {
    return `
      <div class="header">
        <h1>Examen Médical</h1>
        <h2>${examen.nomExamen}</h2>
      </div>

      <div class="section">
        <div class="label">Type d'examen:</div>
        <div>${examen.typeExamen}</div>
      </div>

      <div class="section">
        <div class="label">Statut:</div>
        <div>${this.formatStatut(examen.statut)}</div>
      </div>

      <div class="section">
        <div class="label">Urgence:</div>
        <div class="${examen.urgence ? 'urgent' : ''}">${examen.urgence ? 'URGENT' : 'Non urgent'}</div>
      </div>

      ${examen.dateRealisation ? `
        <div class="section">
          <div class="label">Date de réalisation:</div>
          <div>${this.formatDate(examen.dateRealisation)}</div>
        </div>
      ` : ''}

      ${examen.description ? `
        <div class="section">
          <div class="label">Description:</div>
          <div>${examen.description}</div>
        </div>
      ` : ''}

      ${examen.resultats ? `
        <div class="section">
          <div class="label">Résultats:</div>
          <div>${examen.resultats}</div>
        </div>
      ` : ''}

      ${examen.interpretation ? `
        <div class="section">
          <div class="label">Interprétation:</div>
          <div>${examen.interpretation}</div>
        </div>
      ` : ''}

      <div class="section">
        <div class="label">Date d'impression:</div>
        <div>${new Date().toLocaleDateString('fr-FR')}</div>
      </div>
    `;
  }

  formatStatut(statut: string): string {
    const statuts: { [key: string]: string } = {
      'prescrit': 'Prescrit',
      'programme': 'Programmé',
      'realise': 'Réalisé',
      'interprete': 'Interprété'
    };
    return statuts[statut] || statut;
  }

  formatDate(date: Date | string): string {
    if (!date) return '';
    
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    return dateObj.toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}