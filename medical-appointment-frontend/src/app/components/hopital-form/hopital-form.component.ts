// components/hopital-form/hopital-form.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HopitalRequest, StatutHopital } from '../../core/models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';
import { HopitalService } from '../../core/services/hopital.service';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';
import { AfterViewInit } from '@angular/core';


@Component({
  selector: 'app-hopital-form',
  templateUrl: './hopital-form.component.html',
  styleUrls: ['./hopital-form.component.css'],
  imports: [CommonModule,FormsModule,ReactiveFormsModule]
})
export class HopitalFormComponent implements OnInit {
  hopitalForm: FormGroup;
  isEditMode: boolean = false;
  hopitalId: number | null = null;
  statutOptions = Object.values(StatutHopital);
  loading: boolean = false;
  private map: L.Map | undefined;
  // Liste des régions et villes (à adapter selon vos besoins)
regions = [
  { 
    code: 'DK', 
    nom: 'Dakar', 
    villes: ['Dakar', 'Pikine', 'Guediawaye', 'Rufisque', 'Bargny', 'Sébikotane'] 
  },
  { 
    code: 'TH', 
    nom: 'Thiès', 
    villes: ['Thiès', 'Mbour', 'Tivaouane', 'Khombole', 'Pout', 'Kayar'] 
  },
  { 
    code: 'SL', 
    nom: 'Saint-Louis', 
    villes: ['Saint-Louis', 'Richard-Toll', 'Dagana', 'Podor', 'Rossa-Béthio'] 
  },
  { 
    code: 'ZG', 
    nom: 'Ziguinchor', 
    villes: ['Ziguinchor', 'Bignona', 'Oussouye', 'Adéane', 'Thionck-Essyl'] 
  },
  { 
    code: 'KD', 
    nom: 'Kaolack', 
    villes: ['Kaolack', 'Kaffrine', 'Guinguinéo', 'Ndoffane', 'Nioro du Rip'] 
  },
  { 
    code: 'TG', 
    nom: 'Tambacounda', 
    villes: ['Tambacounda', 'Bakel', 'Koumpentoum', 'Goudiry', 'Kidira'] 
  },
  { 
    code: 'KL', 
    nom: 'Kolda', 
    villes: ['Kolda', 'Vélingara', 'Médina Yoro Foulah', 'Saré Coly Sallé'] 
  },
  { 
    code: 'MT', 
    nom: 'Matam', 
    villes: ['Matam', 'Ouro Sogui', 'Kanel', 'Waoundé', 'Nabadji Civol'] 
  },
  { 
    code: 'FD', 
    nom: 'Fatick', 
    villes: ['Fatick', 'Foundiougne', 'Gossas', 'Passy', 'Sokone'] 
  },
  { 
    code: 'KDG', 
    nom: 'Kédougou', 
    villes: ['Kédougou', 'Salémata', 'Saraya', 'Bandafassi', 'Dindéfélo'] 
  },
  { 
    code: 'LG', 
    nom: 'Louga', 
    villes: ['Louga', 'Linguère', 'Kébémer', 'Dahra', 'Sagatta'] 
  },
  { 
    code: 'SE', 
    nom: 'Sédhiou', 
    villes: ['Sédhiou', 'Bounkiling', 'Goudomp', 'Diattacounda', 'Tanaff'] 
  },
  { 
    code: 'DL', 
    nom: 'Diourbel', 
    villes: ['Diourbel', 'Bambey', 'Mbacké', 'Touba', 'Ndindy'] 
  }
];
villesFiltrees: string[] = [];
showMap = false;
  mapOptions = {
    layers: [
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
      })
    ],
    zoom: 13,
    center: L.latLng(14.6928, -17.4467) // Coordonnées par défaut (Dakar)
  };
ngAfterViewInit(): void {
    if (!this.showMap) return;

    this.initMap();
  }

  initMap(): void {
    if (this.map) {
      this.map.remove();
    }

    this.map = L.map('map').setView(this.mapOptions.center, this.mapOptions.zoom);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    // Si coordonnées GPS valides dans le formulaire, placer un marqueur
    const coords = this.hopitalForm.get('coordonneesGps')?.value;
    if (coords) {
      const [lat, lng] = coords.split(',').map(Number);
      if (!isNaN(lat) && !isNaN(lng)) {
        L.marker([lat, lng]).addTo(this.map)
          .bindPopup(this.hopitalForm.get('nom')?.value || 'Hôpital')
          .openPopup();

        this.map.setView([lat, lng], 13);
      }
    }
  }
logoPreview: string | null = null;


  constructor(
    private fb: FormBuilder,
    private hopitalService: HopitalService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.hopitalForm = this.fb.group({
      nom: ['', [Validators.required, Validators.maxLength(200)]],
      adresse: [''],
      ville: ['', Validators.maxLength(100)],
      region: ['', Validators.maxLength(100)],
      telephone: ['', [Validators.maxLength(20), Validators.pattern(/^[0-9+\-\s]+$/)]],
      email: ['', [Validators.email]],
      siteWeb: ['', [Validators.pattern(/^https?:\/\/.+/)]],
      coordonneesGps: [''],
      heuresOuverture: [''],
      typeEtablissement: ['', Validators.maxLength(100)],
      capaciteLits: ['', [Validators.min(1), Validators.max(10000)]],
      statut: [StatutHopital.ACTIF, Validators.required]
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.hopitalId = +params['id'];
        this.loadHopital();
      }
    });
  }

  loadHopital(): void {
    if (this.hopitalId) {
      this.loading = true;
      this.hopitalService.getHopitalById(this.hopitalId).subscribe({
        next: (hopital) => {
          this.hopitalForm.patchValue(hopital);
          this.loading = false;
        },
        error: (error) => {
          console.error('Erreur lors du chargement de l\'hôpital:', error);
          this.loading = false;
        }
      });
    }
  }

  onSubmit(): void {
    if (this.hopitalForm.valid) {
      this.loading = true;
      const hopitalData: HopitalRequest = this.hopitalForm.value;
      
      if (this.isEditMode && this.hopitalId) {
        this.hopitalService.updateHopital(this.hopitalId, hopitalData).subscribe({
          next: (response) => {
            this.router.navigate(['/hopitaux']);
          },
          error: (error) => {
            console.error('Erreur lors de la mise à jour:', error);
            this.loading = false;
          }
        });
      } else {
        this.hopitalService.createHopital(hopitalData).subscribe({
          next: (response) => {
            this.router.navigate(['/hopitaux']);
          },
          error: (error) => {
            console.error('Erreur lors de la création:', error);
            this.loading = false;
          }
        });
      }
    } else {
      this.markFormGroupTouched(this.hopitalForm);
    }
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      if (control) {
        control.markAsTouched();
        if (control instanceof FormGroup) {
          this.markFormGroupTouched(control);
        }
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/hopitaux']);
  }

  // Méthodes utilitaires pour la validation
  isFieldInvalid(fieldName: string): boolean {
    const field = this.hopitalForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldError(fieldName: string): string {
    const field = this.hopitalForm.get(fieldName);
    if (field && field.errors) {
      if (field.errors['required']) {
        return 'Ce champ est requis';
      }
      if (field.errors['email']) {
        return 'Format d\'email invalide';
      }
      if (field.errors['pattern']) {
        if (fieldName === 'telephone') {
          return 'Format de téléphone invalide';
        }
        if (fieldName === 'siteWeb') {
          return 'L\'URL doit commencer par http:// ou https://';
        }
      }
      if (field.errors['maxlength']) {
        return `Maximum ${field.errors['maxlength'].requiredLength} caractères`;
      }
      if (field.errors['min']) {
        return `Valeur minimale: ${field.errors['min'].min}`;
      }
      if (field.errors['max']) {
        return `Valeur maximale: ${field.errors['max'].max}`;
      }
    }
    return '';
  }
  // Méthode appelée quand la région change
  onRegionChange(): void {
    const regionCode = this.hopitalForm.get('region')?.value;
    const selectedRegion = this.regions.find(r => r.code === regionCode);
    
    this.villesFiltrees = selectedRegion ? selectedRegion.villes : [];
    this.hopitalForm.get('ville')?.reset('');

    if (selectedRegion && this.showMap) {
      const regionCoordinates: {[key: string]: [number, number]} = {
        'DK': [14.6928, -17.4467],  // Dakar
        'TH': [14.7910, -16.9256],  // Thiès
        'SL': [16.0160, -16.4896],  // Saint-Louis
        'ZG': [12.5560, -16.2719],  // Ziguinchor
        'KD': [14.1500, -16.1000],  // Kaolack
        'TG': [13.8000, -13.6667],  // Tambacounda
        'KL': [14.1333, -16.0667],  // Kolda
        'MT': [15.5000, -13.2500],  // Matam
        'FD': [16.5000, -15.0000],  // Fatick
        'KDG': [12.9083, -14.9500],  // Kédougou
        'LG': [15.6167, -16.2167]   // Louga
      };

      const coordinates = regionCoordinates[selectedRegion.code];
      if (coordinates) {
        this.mapOptions.center = L.latLng(coordinates[0], coordinates[1]);
        this.mapOptions.zoom = 10;

        if (this.map) {
          this.map.setView(this.mapOptions.center, this.mapOptions.zoom);
        }
      }
    }
  }


// Méthode pour localiser sur la carte
locateOnMap(): void {
    this.showMap = !this.showMap;

    if (this.showMap) {
      setTimeout(() => {
        this.initMap();
      }, 0);
    } else if (this.map) {
      this.map.remove();
      this.map = undefined;
    }
  }


// Gestion du logo
onLogoChange(event: Event): void {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];
    
    // Vérification du type et taille
    if (!file.type.match('image.*')) {
      alert('Seules les images sont autorisées');
      return;
    }
    
    if (file.size > 2097152) { // 2MB
      alert('La taille maximale est de 2MB');
      return;
    }
    
    // Prévisualisation
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.logoPreview = e.target.result;
    };
    reader.readAsDataURL(file);
  }
}

removeLogo(): void {
  this.logoPreview = null;
  // Ici vous devriez aussi gérer la suppression côté serveur si le logo était déjà enregistré
}

// Aperçu de l'hôpital
previewHopital(): void {
  if (this.hopitalForm.valid) {
    // Ici vous pourriez ouvrir un modal ou une nouvelle route avec l'aperçu
    console.log('Aperçu:', this.hopitalForm.value);
    alert('Fonctionnalité d\'aperçu à implémenter');
  }
}
}