import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-consultation-en-cours',
  templateUrl: './consultation-en-cours.component.html',
  styleUrls: ['./consultation-en-cours.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class ConsultationEnCoursComponent implements OnInit {
  
  @Input() patientId!: number;
  @Input() rendezVousId!: number;
  @Input() consultationData: any;
  @Output() onDataChange = new EventEmitter<any>();

  @Output() consultationTerminee = new EventEmitter<any>();
  @Output() consultationAnnulee = new EventEmitter<void>();

  consultationForm: FormGroup;
  startTime: Date = new Date();

  constructor(private fb: FormBuilder) {
    this.consultationForm = this.fb.group({
      motifConsultation: ['', Validators.required],
      symptomes: [''],
      tension: [''],
      pouls: [''],
      temperature: [''],
      poids: [''],
      taille: [''],
      saturation: [''],
      examenClinique: [''],
      diagnostic: ['', Validators.required],
      diagnosticsSecondaires: [''],
      planTraitement: [''],
      observations: [''],
      prochainRendezVous: [''],
      recommandationsSuivi: ['']
    });
  }

  ngOnInit(): void {
    this.startTime = new Date();
    this.chargerDonneesPatient();
  }

  chargerDonneesPatient(): void {
    // Implémentation du chargement des données
  }

  formatTime(date: Date): string {
    return date.toLocaleTimeString('fr-FR', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  getCurrentTime(): string {
    return new Date().toLocaleTimeString('fr-FR', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  ajouterPrescription(): void {
    // Logique d'ajout de prescription
  }

  demanderExamen(): void {
    // Logique de demande d'examen
  }

  genererRapport(): void {
    // Logique de génération de rapport
  }

  sauvegarderBrouillon(): void {
    const consultationData = {
      ...this.consultationForm.value,
      patientId: this.patientId,
      heureDebut: this.startTime,
      statut: 'brouillon',
      dateCreation: new Date()
    };
    // Logique de sauvegarde
  }

  terminerConsultation(): void {
    if (this.consultationForm.valid) {
      const consultationData = {
        ...this.consultationForm.value,
        patientId: this.patientId,
        heureDebut: this.startTime,
        heureFin: new Date(),
        statut: 'terminee',
        dateCreation: new Date()
      };
      this.consultationTerminee.emit(consultationData);
    }
  }

  annulerConsultation(): void {
    if (confirm('Êtes-vous sûr de vouloir annuler cette consultation ?')) {
      this.consultationAnnulee.emit();
    }
  }
}