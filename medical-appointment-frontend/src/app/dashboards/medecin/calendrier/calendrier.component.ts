import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { RendezVousService } from '../../../core/services/rendez-vous.service';
import {
  FullCalendarComponent,
  FullCalendarModule,
} from '@fullcalendar/angular';
import {
  CalendarOptions,
  EventApi,
  DateSelectArg,
  EventClickArg,
  EventInput,
} from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin, { DateClickArg } from '@fullcalendar/interaction';
import frLocale from '@fullcalendar/core/locales/fr';
import { AuthService } from '../../../core/services/auth.service';

import { NotificationService } from '../../../core/services/notification.service';
import { Medecin } from '../../../core/models/utilisateur/utilisateur.module';
import {
  RendezVousRequest,
  Hopital,
  Service,
  StatutRendezVous,
} from '../../../core/models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';
import { HopitalService } from '../../../core/services/hopital.service';
import { ServiceService } from '../../../core/services/service.service';
import { MedecinService } from '../../../core/services/medecin.service';
import { PatientService } from '../../../core/services/patient.service';

@Component({
  selector: 'app-calendrier-medecin',
  standalone: true,
  imports: [CommonModule, FullCalendarModule, FormsModule, ReactiveFormsModule],
  templateUrl: './calendrier.component.html',
  styleUrls: ['./calendrier.component.scss'],
})
export class CalendrierMedecinComponent implements OnInit {
  @ViewChild('rdvModal') rdvModal!: ElementRef<HTMLDialogElement>;
  @ViewChild('calendar') calendarComponent!: FullCalendarComponent;
  disponibilitesJour: any[] = [];

  medecinId!: number;
  calendarOptions: CalendarOptions;
  rdvForm: FormGroup;
  patients: any[] = [];
  isSubmitting = false;
  selectedDate: Date | null = null;
  selectedService!: Service;
  selectedHopital!: Hopital;
  medecin!: Medecin;
  constructor(
    private rendezVousService: RendezVousService,
    private authService: AuthService,
    private medecinService: MedecinService,
    private notificationService: NotificationService,
    private serviceService: ServiceService,
    private hopitalService: HopitalService,
    private patientService: PatientService,
    private fb: FormBuilder
  ) {
    this.calendarOptions = {
      initialView: 'timeGridWeek',
      locale: frLocale,
      headerToolbar: {
        left: 'prev,next today',
        center: 'title',
        right: 'dayGridMonth,timeGridWeek,timeGridDay',
      },
      plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
      weekends: false,
      editable: true,
      selectable: true,
      selectMirror: true,
      dayMaxEvents: true,
      events: [],
      eventClick: this.handleEventClick.bind(this),
      dateClick: this.handleDateClick.bind(this),
      eventDrop: this.handleEventDrop.bind(this),
      eventResize: this.handleEventResize.bind(this),
      slotMinTime: '08:00:00',
      slotMaxTime: '20:00:00',
      slotDuration: '00:15:00',
    };

    this.rdvForm = this.fb.group({
      patientId: ['', Validators.required],
      date: ['', Validators.required],
      heureDebut: ['', Validators.required],
      duree: ['30', Validators.required],
      typeConsultation: ['GENERALE', Validators.required],
      notes: [''],
    });
  }

  ngOnInit(): void {
    this.rdvForm.get('date')?.valueChanges.subscribe((date) => {
      if (date) {
        this.fetchCreneauxDisponibles(date);
      }
    });

    const user = this.authService.currentUserValue;
    if (user?.id) {
      this.medecinId = user.id;
      this.medecinService.getMedecinById(this.medecinId).subscribe({
        next: (medecin) => {
          if (!medecin.idService) {
            console.error('Le médecin ne possède pas de serviceId');
            return;
          }
          this.serviceService.getServiceById(medecin.idService!).subscribe({
            next: (service) => {
              this.selectedService = service;
              if (!service.idHopital) {
                console.error('Le service ne possède pas de hopitalId');
                return;
              }
              this.hopitalService
                .getHopitalById(service.idHopital)
                .subscribe({
                  next: (hopital) => {
                    this.selectedHopital = hopital;
                  },
                  error: (err) => {
                    console.error('Erreur chargement hôpital', err);
                  },
                });
            },
            error: (err) => {
              console.error('Erreur chargement service', err);
            },
          });
        },
        error: (err) => {
          console.error('Erreur chargement détails médecin', err);
        },
      });

      this.loadPatients();
      this.loadRendezVous();
    }
  }

  loadRendezVous(): void {
    this.rendezVousService.getRendezVousByMedecin(this.medecinId).subscribe({
      next: (rendezVous) => {
        this.calendarOptions.events =
          this.transformToCalendarEvents(rendezVous);
      },
      error: (err) => {
        console.error('Erreur chargement rendez-vous', err);
        this.notificationService.showError(
          'Erreur lors du chargement des rendez-vous'
        );
      },
    });
  }
  fetchCreneauxDisponibles(date: string): void {
    this.rendezVousService
      .getDisponibilitesParMedecinEtDate(this.medecinId, date)
      .subscribe({
        next: (dispos) => {
          this.disponibilitesJour = dispos;
          // Optionnel : préremplir le premier créneau
          if (dispos.length > 0) {
            const first = dispos[0];
            this.rdvForm.patchValue({
              heureDebut: first.heureDebut,
            });
          }
        },
        error: () => {
          this.notificationService.showError(
            'Erreur lors du chargement des créneaux disponibles'
          );
          this.disponibilitesJour = [];
        },
      });
  }

  loadPatients(): void {
    this.patientService.getPatients().subscribe({
      next: (patients) => {
        this.patients = patients;
      },
      error: (err) => {
        console.error('Erreur chargement patients', err);
        this.notificationService.showError(
          'Erreur lors du chargement des patients'
        );
      },
    });
  }

  private transformToCalendarEvents(rendezVous: any[]): EventInput[] {
    return rendezVous.map((rdv) => ({
      id: rdv.id.toString(),
      title: `${rdv.patient.nom} ${rdv.patient.prenom} - ${rdv.typeConsultation}`,
      start: new Date(rdv.dateHeureDebut),
      end: new Date(rdv.dateHeureFin),
      color: this.getEventColor(rdv.statut),
      extendedProps: {
        patient: rdv.patient,
        statut: rdv.statut,
        type: rdv.typeConsultation,
        notes: rdv.notes,
      },
    }));
  }

  private getEventColor(statut: string): string {
    const colors: Record<string, string> = {
      CONFIRME: '#3b82f6',
      PROGRAMME: '#f59e0b',
      EN_COURS: '#10b981',
      TERMINE: '#64748b',
      URGENT: '#ef4444',
    };
    return colors[statut] || '#8b5cf6';
  }

  handleEventClick(clickInfo: EventClickArg): void {
    const event = clickInfo.event;
    const rdvDetails = {
      patient: event.extendedProps['patient'],
      dateHeureDebut: event.start,
      dateHeureFin: event.end,
      type: event.extendedProps['type'],
      notes: event.extendedProps['notes'],
    };
    console.log('Détails du RDV:', rdvDetails);
    // Vous pouvez ouvrir un modal de détails ici
  }

  handleDateClick(arg: DateClickArg): void {
    this.selectedDate = arg.date;
    this.rdvForm.patchValue({
      date: this.formatDateForInput(arg.date),
      heureDebut: this.formatTimeForInput(arg.date),
    });

    this.openCreateRdvModal();
  }

  handleEventDrop(dropInfo: any): void {
    this.rendezVousService
      .updateRendezVous(
        parseInt(dropInfo.event.id),
        dropInfo.event.start,
        dropInfo.event.end
      )
      .subscribe({
        next: () => {
          this.notificationService.showSuccess(
            'Rendez-vous déplacé avec succès'
          );
        },
        error: (err) => {
          console.error('Erreur déplacement RDV', err);
          this.notificationService.showError(
            'Erreur lors du déplacement du rendez-vous'
          );
          dropInfo.revert();
        },
      });
  }

  handleEventResize(resizeInfo: any): void {
    this.rendezVousService
      .updateRendezVous(
        parseInt(resizeInfo.event.id),
        resizeInfo.event.start,
        resizeInfo.event.end
      )
      .subscribe({
        next: () => {
          this.notificationService.showSuccess(
            'Durée du rendez-vous mise à jour'
          );
        },
        error: (err) => {
          console.error('Erreur modification durée', err);
          this.notificationService.showError(
            'Erreur lors de la modification de la durée'
          );
          resizeInfo.revert();
        },
      });
  }

  openCreateRdvModal(): void {
    this.rdvModal.nativeElement.showModal();
  }

  closeModal(): void {
    this.rdvModal.nativeElement.close();
    this.rdvForm.reset();
    this.isSubmitting = false;
  }

  onSubmit(): void {
    if (this.rdvForm.invalid) {
      this.notificationService.showError(
        'Veuillez remplir tous les champs obligatoires'
      );
      return;
    }

    this.isSubmitting = true;
    const formValue = this.rdvForm.value;
    const startDateTime = new Date(`${formValue.date}T${formValue.heureDebut}`);
    const endDateTime = new Date(
      startDateTime.getTime() + formValue.duree * 60000
    );

    const rdvData: RendezVousRequest = {
      idPatient: formValue.patientId,
      idMedecin: this.medecinId,
      idService: this.selectedService.idService!,
      idHopital: this.selectedHopital.idHopital!, 
      dateHeure: startDateTime,
      typeConsultation: formValue.typeConsultation,
      motif: formValue.notes || '',
      niveauUrgence: formValue.niveauUrgence || 'NORMAL',
      dureePrevue: formValue.duree,
    };

    this.rendezVousService.createRendezVous(rdvData).subscribe({
      next: () => {
        this.notificationService.showSuccess('Rendez-vous créé avec succès');
        this.closeModal();
        this.loadRendezVous();
      },
      error: (err) => {
        console.error('Erreur création RDV', err);
        this.notificationService.showError(
          'Erreur lors de la création du rendez-vous'
        );
        this.isSubmitting = false;
      },
    });
  }

  private formatDateForInput(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  private formatTimeForInput(date: Date): string {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }
}
