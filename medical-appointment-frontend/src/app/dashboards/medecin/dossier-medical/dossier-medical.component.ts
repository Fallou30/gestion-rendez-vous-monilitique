import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DossierMedicalService, DossierMedical, DocumentMedical, UpdateDossierRequest } from '../../../core/services/dossier-medical.service';
import { ConsultationService } from '../../../core/services/consultation.service';
import { FileUploadService } from '../../../core/services/file-upload.service';

@Component({
  selector: 'app-dossier-medical',
  templateUrl: './dossier-medical.component.html',
  styleUrls: ['./dossier-medical.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class DossierMedicalComponent implements OnInit {
  @Input() patientId!: number;
  @Input() dossierMedical: any;
  @Input() patient: any;
  @Output() onDossierUpdate = new EventEmitter<void>();


  dossierForm: FormGroup;
  documentForm: FormGroup;
  isEditing = false;
  isLoading = false;
  
  historiqueConsultations: any[] = [];
  documentsMedicaux: DocumentMedical[] = [];
  
  showDocumentModal = false;
  selectedFile: File | null = null;

  constructor(
    private fb: FormBuilder,
    private dossierMedicalService: DossierMedicalService,
    private consultationService: ConsultationService,
    private fileUploadService: FileUploadService // Assurez-vous d'avoir un service pour gérer les fichiers
  ) {
    this.dossierForm = this.fb.group({
      antecedentsMedicaux: [''],
      antecedentsFamiliaux: [''],
      vaccinations: [''],
      notesGenerales: ['']
    });

    this.documentForm = this.fb.group({
      typeDocument: ['', Validators.required],
      description: ['']
    });
  }

  ngOnInit(): void {
    if (this.dossierMedical) {
      this.initializeForms();
      this.loadHistoriqueConsultations();
      this.loadDocuments(); 
    }
  }

  initializeForms(): void {
    if (this.dossierMedical) {
      this.dossierForm.patchValue({
        antecedentsMedicaux: this.dossierMedical.antecedentsMedicaux || '',
        antecedentsFamiliaux: this.dossierMedical.antecedentsFamiliaux || '',
        vaccinations: this.dossierMedical.vaccinations || '',
        notesGenerales: this.dossierMedical.notesGenerales || ''
      });
    }
  }

  loadHistoriqueConsultations(): void {
    this.consultationService.getHistoriqueConsultations(this.patientId).subscribe({
      next: (consultations) => {
        this.historiqueConsultations = consultations;
      },
      error: (error) => {
        console.error('Erreur lors du chargement de l\'historique:', error);
      }
    });
  }

  toggleEditMode(): void {
    this.isEditing = !this.isEditing;
    if (!this.isEditing) {
      this.initializeForms();
    }
  }

  sauvegarderDossier(): void {
    if (this.dossierForm.valid && this.dossierMedical) {
      this.isLoading = true;
      const updateRequest: UpdateDossierRequest = this.dossierForm.value;

      this.dossierMedicalService.mettreAJourDossier(this.dossierMedical.id, updateRequest).subscribe({
        next: (dossierMisAJour) => {
          this.dossierMedical = dossierMisAJour;
          this.isEditing = false;
          this.isLoading = false;
          this.showNotification('Dossier mis à jour avec succès', 'success');
        },
        error: (error) => {
          this.isLoading = false;
          this.showNotification('Erreur lors de la mise à jour', 'error');
          console.error('Erreur mise à jour dossier:', error);
        }
      });
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.showDocumentModal = true;
    }
  }

  ajouterDocument(): void {
    if (this.documentForm.valid && this.selectedFile && this.dossierMedical) {
      const typeDocument = this.documentForm.get('typeDocument')?.value;
      const description = this.documentForm.get('description')?.value || '';

      this.dossierMedicalService.ajouterDocument(
        this.dossierMedical.id,
        this.selectedFile,
        typeDocument,
        description
      ).subscribe({
        next: (document) => {
          this.documentsMedicaux.push(document);
          this.closeDocumentModal();
          this.showNotification('Document ajouté avec succès', 'success');
        },
        error: (error) => {
          this.showNotification('Erreur lors de l\'ajout du document', 'error');
          console.error('Erreur ajout document:', error);
        }
      });
    }
  }

  closeDocumentModal(): void {
    this.showDocumentModal = false;
    this.selectedFile = null;
    this.documentForm.reset();
  }

telechargerDocument(doc: DocumentMedical): void {
  this.fileUploadService.downloadFile(doc.cheminFichier).subscribe({
    next: (blob: Blob) => {
      // Créer un lien de téléchargement
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a'); // Ici, on utilise l'objet global `document`
      a.href = url;

      // Utiliser le nom original du fichier ou générer un nom
      a.download = doc.nomFichier || `document_${doc.id}.${this.getFileExtension(doc.typeDocument)}`;

      // Déclencher le téléchargement
      a.click();

      // Nettoyer
      window.URL.revokeObjectURL(url);
      a.remove();
    },
    error: (error) => {
      console.error('Erreur lors du téléchargement:', error);
      this.showNotification('Échec du téléchargement', 'error');
    }
  });
}

// Ajouter cette méthode pour la prévisualisation
previsualiserDocument(document: DocumentMedical): void {
  const previewUrl = this.fileUploadService.getFilePreviewUrl(document.cheminFichier);
  window.open(previewUrl, '_blank');
}
loadDocuments(): void {
  if (this.dossierMedical) {
    this.dossierMedicalService.getDocumentsDossier(this.dossierMedical.id).subscribe({
      next: (documents) => {
        this.documentsMedicaux = documents;
      },
      error: (error) => {
        console.error('Erreur chargement documents:', error);
      }
    });
  }
}
// Mettre à jour getDocumentIcon pour les icônes
getDocumentIcon(typeDocument: string): string {
  const icons: {[key: string]: string} = {
    'ORDONNANCE': 'fas fa-file-prescription',
    'EXAMEN': 'fas fa-file-medical',
    'RAPPORT': 'fas fa-file-alt',
    'CERTIFICAT': 'fas fa-file-certificate',
    'IMAGE': 'fas fa-file-image',
    'AUTRE': 'fas fa-file'
  };
  return icons[typeDocument] || 'fas fa-file';
}
// Ajouter cette méthode utilitaire
private getFileExtension(typeDocument: string): string {
  const extensions: {[key: string]: string} = {
    'ORDONNANCE': 'pdf',
    'EXAMEN': 'pdf',
    'RAPPORT': 'pdf',
    'CERTIFICAT': 'pdf',
    'IMAGE': 'jpg',
    'AUTRE': 'pdf'
  };
  return extensions[typeDocument] || 'pdf';
}


  formatDate(date: Date | string): string {
    return new Date(date).toLocaleDateString('fr-FR');
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  private showNotification(message: string, type: 'success' | 'error' | 'info'): void {
    console.log(`${type.toUpperCase()}: ${message}`);
  }
  
}