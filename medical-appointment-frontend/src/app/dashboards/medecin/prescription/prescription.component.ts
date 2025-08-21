import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';

interface Medicament {
  id: number;
  nom: string;
  dosage: string;
  forme: string;
  description: string;
}

interface LignePrescription {
  medicamentId: number;
  medicamentNom: string;
  dosage: string;
  quantite: number;
  duree: number;
  frequence: string;
  instructions: string;
}

interface Prescription {
  id: number;
  patientId: number;
  consultationId: number;
  datePrescription: Date;
  lignesPrescription: LignePrescription[];
  statut: string;
  instructions: string;
}

@Component({
  selector: 'app-prescription',
  template: `
    <div class="prescription-container">
      <div class="prescription-header">
        <h3><i class="fas fa-prescription"></i> Prescription</h3>
        <div class="prescription-actions">
          <button type="button" (click)="ajouterLignePrescription()" class="btn btn-primary">
            <i class="fas fa-plus"></i> Ajouter médicament
          </button>
          <button type="button" (click)="sauvegarderPrescription()" 
                  class="btn btn-success" 
                  [disabled]="!prescriptionForm.valid || lignesPrescription.length === 0">
            <i class="fas fa-save"></i> Sauvegarder
          </button>
          <button type="button" (click)="imprimerPrescription()" 
                  class="btn btn-info"
                  [disabled]="!prescriptionActuelle">
            <i class="fas fa-print"></i> Imprimer
          </button>
        </div>
      </div>

      <div class="prescription-content">
        <form [formGroup]="prescriptionForm" class="prescription-form">
          
          <!-- Instructions générales -->
          <div class="form-section">
            <h4><i class="fas fa-info-circle"></i> Instructions générales</h4>
            <div class="form-group">
              <label for="instructions">Instructions au patient</label>
              <textarea
                id="instructions"
                formControlName="instructions"
                rows="3"
                class="form-control"
                placeholder="Instructions générales pour le patient...">
              </textarea>
            </div>
          </div>

          <!-- Lignes de prescription -->
          <div class="form-section">
            <h4><i class="fas fa-pills"></i> Médicaments prescrits</h4>
            
            <div class="lignes-prescription" formArrayName="lignesPrescription">
              <div class="ligne-prescription" 
                   *ngFor="let ligne of lignesPrescription.controls; let i = index" 
                   [formGroupName]="i">
                
                <div class="ligne-header">
                  <h5>Médicament {{ i + 1 }}</h5>
                  <button type="button" (click)="supprimerLigne(i)" class="btn btn-sm btn-outline-danger">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>

                <div class="ligne-content">
                  <div class="form-row">
                    <div class="form-group col-md-6">
                      <label for="medicament-{{ i }}">Médicament *</label>
                      <select id="medicament-{{ i }}" formControlName="medicamentId" 
                              class="form-control" (change)="onMedicamentChange(i)">
                        <option value="">Sélectionner un médicament</option>
                        <option *ngFor="let medicament of medicaments" 
                                [value]="medicament.id">
                          {{ medicament.nom }} - {{ medicament.dosage }}
                        </option>
                      </select>
                    </div>
                    <div class="form-group col-md-3">
                      <label for="quantite-{{ i }}">Quantité *</label>
                      <input type="number" id="quantite-{{ i }}" formControlName="quantite" 
                             class="form-control" min="1" placeholder="1">
                    </div>
                    <div class="form-group col-md-3">
                      <label for="duree-{{ i }}">Durée (jours) *</label>
                      <input type="number" id="duree-{{ i }}" formControlName="duree" 
                             class="form-control" min="1" placeholder="7">
                    </div>
                  </div>

                  <div class="form-row">
                    <div class="form-group col-md-6">
                      <label for="frequence-{{ i }}">Fréquence *</label>
                      <select id="frequence-{{ i }}" formControlName="frequence" class="form-control">
                        <option value="">Sélectionner une fréquence</option>
                        <option value="1_fois_par_jour">1 fois par jour</option>
                        <option value="2_fois_par_jour">2 fois par jour</option>
                        <option value="3_fois_par_jour">3 fois par jour</option>
                        <option value="4_fois_par_jour">4 fois par jour</option>
                        <option value="matin">Le matin</option>
                        <option value="soir">Le soir</option>
                        <option value="matin_soir">Matin et soir</option>
                        <option value="au_besoin">Au besoin</option>
                      </select>
                    </div>
                    <div class="form-group col-md-6">
                      <label for="dosage-{{ i }}">Dosage</label>
                      <input type="text" id="dosage-{{ i }}" formControlName="dosage" 
                             class="form-control" placeholder="Ex: 1 comprimé">
                    </div>
                  </div>

                  <div class="form-group">
                    <label for="instructions-{{ i }}">Instructions spécifiques</label>
                    <textarea id="instructions-{{ i }}" formControlName="instructions" 
                             rows="2" class="form-control"
                             placeholder="Instructions spécifiques pour ce médicament..."></textarea>
                  </div>
                </div>
              </div>
            </div>

            <div class="no-medicaments" *ngIf="lignesPrescription.length === 0">
              <p><i class="fas fa-info-circle"></i> Aucun médicament prescrit</p>
            </div>
          </div>
        </form>

        <!-- Historique des prescriptions -->
        <div class="historique-section">
          <h4><i class="fas fa-history"></i> Historique des prescriptions</h4>
          <div class="prescriptions-list" *ngIf="historiquePrescriptions.length > 0">
            <div class="prescription-item" *ngFor="let prescription of historiquePrescriptions">
              <div class="prescription-header-item">
                <span class="prescription-date">{{ formatDate(prescription.datePrescription) }}</span>
                <span class="prescription-statut" [class]="'statut-' + prescription.statut">
                  {{ prescription.statut }}
                </span>
                <button type="button" (click)="voirPrescription(prescription)" 
                        class="btn btn-sm btn-outline-primary">
                  <i class="fas fa-eye"></i> Voir
                </button>
              </div>
              <div class="prescription-resume">
                <p><strong>{{ prescription.lignesPrescription.length }}</strong> médicament(s) prescrit(s)</p>
                <div class="medicaments-resume">
                  <span *ngFor="let ligne of prescription.lignesPrescription; let last = last">
                    {{ ligne.medicamentNom }}{{ !last ? ', ' : '' }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          <div class="no-prescriptions" *ngIf="historiquePrescriptions.length === 0">
            <p><i class="fas fa-info-circle"></i> Aucune prescription enregistrée</p>
          </div>
        </div>
      </div>

      <!-- Modal pour voir une prescription -->
      <div class="modal" *ngIf="showPrescriptionModal" (click)="closePrescriptionModal()">
        <div class="modal-content modal-lg" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h4>Détails de la prescription</h4>
            <button type="button" (click)="closePrescriptionModal()" class="close-btn">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div class="modal-body" *ngIf="prescriptionSelectionnee">
            <div class="prescription-details">
              <div class="prescription-info">
                <p><strong>Date:</strong> {{ formatDate(prescriptionSelectionnee.datePrescription) }}</p>
                <p><strong>Statut:</strong> {{ prescriptionSelectionnee.statut }}</p>
              </div>
              
              <div class="instructions-generales" *ngIf="prescriptionSelectionnee.instructions">
                <h5>Instructions générales</h5>
                <p>{{ prescriptionSelectionnee.instructions }}</p>
              </div>

              <div class="medicaments-details">
                <h5>Médicaments prescrits</h5>
                <div class="medicament-detail" *ngFor="let ligne of prescriptionSelectionnee.lignesPrescription">
                  <div class="medicament-header">
                    <strong>{{ ligne.medicamentNom }}</strong>
                    <span class="dosage">{{ ligne.dosage }}</span>
                  </div>
                  <div class="medicament-info">
                    <p><strong>Quantité:</strong> {{ ligne.quantite }}</p>
                    <p><strong>Durée:</strong> {{ ligne.duree }} jours</p>
                    <p><strong>Fréquence:</strong> {{ formatFrequence(ligne.frequence) }}</p>
                    <p *ngIf="ligne.instructions"><strong>Instructions:</strong> {{ ligne.instructions }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" (click)="closePrescriptionModal()" class="btn btn-secondary">
              Fermer
            </button>
            <button type="button" (click)="imprimerPrescriptionModal()" class="btn btn-primary">
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
export class PrescriptionComponent implements OnInit {
  @Input() patientId!: number;
   @Input() consultationId?: number;
   @Input() consultationData: any;
  

  prescriptionForm: FormGroup;
  medicaments: Medicament[] = [];
  historiquePrescriptions: Prescription[] = [];
  prescriptionActuelle: Prescription | null = null;
  
  showPrescriptionModal = false;
  prescriptionSelectionnee: Prescription | null = null;

  constructor(private fb: FormBuilder) {
    this.prescriptionForm = this.fb.group({
      instructions: [''],
      lignesPrescription: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.loadMedicaments();
    this.loadHistoriquePrescriptions();
  }

  get lignesPrescription(): FormArray {
    return this.prescriptionForm.get('lignesPrescription') as FormArray;
  }

  loadMedicaments(): void {
    // Simulation des médicaments - à remplacer par un appel API
    this.medicaments = [
      { id: 1, nom: 'Paracétamol', dosage: '500mg', forme: 'Comprimé', description: 'Antalgique' },
      { id: 2, nom: 'Ibuprofène', dosage: '200mg', forme: 'Comprimé', description: 'Anti-inflammatoire' },
      { id: 3, nom: 'Amoxicilline', dosage: '500mg', forme: 'Gélule', description: 'Antibiotique' },
      { id: 4, nom: 'Doliprane', dosage: '1000mg', forme: 'Comprimé', description: 'Antalgique' }
    ];
  }

  loadHistoriquePrescriptions(): void {
    // Simulation de l'historique - à remplacer par un appel API
    this.historiquePrescriptions = [];
  }

  ajouterLignePrescription(): void {
    const ligneGroup = this.fb.group({
      medicamentId: ['', Validators.required],
      medicamentNom: [''],
      dosage: [''],
      quantite: [1, [Validators.required, Validators.min(1)]],
      duree: [7, [Validators.required, Validators.min(1)]],
      frequence: ['', Validators.required],
      instructions: ['']
    });

    this.lignesPrescription.push(ligneGroup);
  }

  supprimerLigne(index: number): void {
    this.lignesPrescription.removeAt(index);
  }
  // Continuation du PrescriptionComponent (méthodes manquantes)

  onMedicamentChange(index: number): void {
    const ligne = this.lignesPrescription.at(index);
    const medicamentId = ligne.get('medicamentId')?.value;
    
    if (medicamentId) {
      const medicament = this.medicaments.find(m => m.id == medicamentId);
      if (medicament) {
        ligne.patchValue({
          medicamentNom: medicament.nom,
          dosage: medicament.dosage
        });
      }
    }
  }

  sauvegarderPrescription(): void {
    if (this.prescriptionForm.valid && this.lignesPrescription.length > 0) {
      const formValue = this.prescriptionForm.value;
      
      const nouvellePrescription: Prescription = {
        id: 0, // Sera assigné par le backend
        patientId: this.patientId,
        consultationId: this.consultationId || 0,
        datePrescription: new Date(),
        lignesPrescription: formValue.lignesPrescription.map((ligne: any) => ({
          medicamentId: ligne.medicamentId,
          medicamentNom: ligne.medicamentNom,
          dosage: ligne.dosage,
          quantite: ligne.quantite,
          duree: ligne.duree,
          frequence: ligne.frequence,
          instructions: ligne.instructions
        })),
        statut: 'active',
        instructions: formValue.instructions
      };

      // Ici, vous devriez appeler votre service pour sauvegarder
      // this.prescriptionService.creerPrescription(nouvellePrescription).subscribe(...)
      
      // Simulation pour l'exemple
      console.log('Prescription sauvegardée:', nouvellePrescription);
      
      // Réinitialiser le formulaire
      this.prescriptionForm.reset();
      this.lignesPrescription.clear();
      
      // Recharger l'historique
      this.loadHistoriquePrescriptions();
      
      // Notification de succès
      alert('Prescription sauvegardée avec succès!');
    }
  }

  imprimerPrescription(): void {
    if (this.prescriptionActuelle) {
      this.genererPrescriptionPDF(this.prescriptionActuelle);
    }
  }

  voirPrescription(prescription: Prescription): void {
    this.prescriptionSelectionnee = prescription;
    this.showPrescriptionModal = true;
  }

  closePrescriptionModal(): void {
    this.showPrescriptionModal = false;
    this.prescriptionSelectionnee = null;
  }

  imprimerPrescriptionModal(): void {
    if (this.prescriptionSelectionnee) {
      this.genererPrescriptionPDF(this.prescriptionSelectionnee);
    }
  }

  private genererPrescriptionPDF(prescription: Prescription): void {
    // Ici vous pourriez utiliser une bibliothèque comme jsPDF
    // Pour l'exemple, on ouvre une nouvelle fenêtre avec le contenu
    const contenuPrescription = this.genererContenuPrescription(prescription);
    const fenetre = window.open('', '_blank');
    if (fenetre) {
      fenetre.document.write(`
        <html>
          <head>
            <title>Prescription - ${this.formatDate(prescription.datePrescription)}</title>
            <style>
              body { font-family: Arial, sans-serif; margin: 20px; }
              .header { text-align: center; border-bottom: 2px solid #333; padding-bottom: 20px; }
              .prescription-info { margin: 20px 0; }
              .medicament { margin: 15px 0; padding: 10px; border: 1px solid #ddd; }
              .instructions { margin-top: 20px; padding: 10px; background-color: #f5f5f5; }
            </style>
          </head>
          <body>
            ${contenuPrescription}
          </body>
        </html>
      `);
      fenetre.document.close();
      fenetre.print();
    }
  }

  private genererContenuPrescription(prescription: Prescription): string {
    let contenu = `
      <div class="header">
        <h1>PRESCRIPTION MÉDICALE</h1>
        <p>Date: ${this.formatDate(prescription.datePrescription)}</p>
      </div>
      
      <div class="prescription-info">
        <p><strong>Patient ID:</strong> ${prescription.patientId}</p>
        <p><strong>Consultation ID:</strong> ${prescription.consultationId}</p>
      </div>
      
      <div class="medicaments">
        <h2>Médicaments prescrits:</h2>
    `;

    prescription.lignesPrescription.forEach((ligne, index) => {
      contenu += `
        <div class="medicament">
          <h3>${index + 1}. ${ligne.medicamentNom}</h3>
          <p><strong>Dosage:</strong> ${ligne.dosage}</p>
          <p><strong>Quantité:</strong> ${ligne.quantite}</p>
          <p><strong>Durée:</strong> ${ligne.duree} jours</p>
          <p><strong>Fréquence:</strong> ${this.formatFrequence(ligne.frequence)}</p>
          ${ligne.instructions ? `<p><strong>Instructions:</strong> ${ligne.instructions}</p>` : ''}
        </div>
      `;
    });

    contenu += `</div>`;

    if (prescription.instructions) {
      contenu += `
        <div class="instructions">
          <h2>Instructions générales:</h2>
          <p>${prescription.instructions}</p>
        </div>
      `;
    }

    return contenu;
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }

  formatFrequence(frequence: string): string {
    const frequences: { [key: string]: string } = {
      '1_fois_par_jour': '1 fois par jour',
      '2_fois_par_jour': '2 fois par jour',
      '3_fois_par_jour': '3 fois par jour',
      '4_fois_par_jour': '4 fois par jour',
      'matin': 'Le matin',
      'soir': 'Le soir',
      'matin_soir': 'Matin et soir',
      'au_besoin': 'Au besoin'
    };
    return frequences[frequence] || frequence;
  }
}