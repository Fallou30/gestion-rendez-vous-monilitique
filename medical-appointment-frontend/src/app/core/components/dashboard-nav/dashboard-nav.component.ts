// dashboard-nav.component.ts
import { Component, Input, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TypeUtilisateur } from '../../models/utilisateur/utilisateur.module';
import { AuthService } from '../../services/auth.service';

interface NavLink {
  label: string;
  path: string;
  icon: string;
  roles: TypeUtilisateur[];
  badgeCount?: number;
}

interface NavGroup {
  label: string;
  icon: string;
  roles: TypeUtilisateur[];
  links: NavLink[];
  isOpen?: boolean;
}

interface Notification {
  id: string;
  title: string;
  message: string;
  type: 'info' | 'warning' | 'success' | 'error';
  timestamp: Date;
  read: boolean;
}

@Component({
  selector: 'app-dashboard-nav',
  templateUrl: './dashboard-nav.component.html',
  styleUrls: ['./dashboard-nav.component.scss'],
  imports: [CommonModule, RouterModule],
})
export class DashboardNavComponent implements OnInit {
  @Input() currentRole: TypeUtilisateur = TypeUtilisateur.ADMIN;
  
  userMenuOpen = false;
  mobileMenuOpen = false;
  notificationMenuOpen = false;
  private boundCloseAllMenus!: (event: Event) => void;
  userInitials = 'AD';
  userName = 'Admin Système';
  email = '';
  role = '';
  
  patientName = signal<string>('');
  showNotifications = signal<boolean>(false);
  
  // État des groupes de navigation
  openGroups: Set<string> = new Set();
  
  notifications: Notification[] = [
    {
      id: '1',
      title: 'Nouvelle demande médecin',
      message: 'Dr. Martin a soumis sa demande d\'inscription',
      type: 'info',
      timestamp: new Date(),
      read: false
    },
    {
      id: '2',
      title: 'Validation requise',
      message: '3 réceptionnistes en attente de validation',
      type: 'warning',
      timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000),
      read: false
    },
    {
      id: '3',
      title: 'Système mis à jour',
      message: 'Mise à jour de sécurité appliquée avec succès',
      type: 'success',
      timestamp: new Date(Date.now() - 24 * 60 * 60 * 1000),
      read: true
    }
  ];

  // Navigation groupée pour éviter le débordement
  navGroups: NavGroup[] = [
    // Liens directs prioritaires (toujours visibles)
    {
      label: 'Tableau de bord',
      icon: 'fas fa-chart-pie',
      roles: [TypeUtilisateur.ADMIN],
      links: [{
        label: 'Tableau de bord',
        path: '/admin/dashboard',
        icon: 'fas fa-chart-pie',
        roles: [TypeUtilisateur.ADMIN]
      }]
    },
    
    // Groupe Gestion des utilisateurs
    {
      label: 'Utilisateurs',
      icon: 'fas fa-users',
      roles: [TypeUtilisateur.ADMIN],
      links: [
        {
          label: 'Patients',
          path: '/admin/patients',
          icon: 'fas fa-users',
          roles: [TypeUtilisateur.ADMIN]
        },
        {
          label: 'Médecins',
          path: '/admin/medecins',
          icon: 'fas fa-user-md',
          roles: [TypeUtilisateur.ADMIN]
        },
        {
          label: 'Réceptionnistes',
          path: '/admin/receptionnistes',
          icon: 'fas fa-user-tie',
          roles: [TypeUtilisateur.ADMIN]
        }
      ]
    },
    
    // Groupe Gestion médicale
    {
      label: 'Médical',
      icon: 'fas fa-stethoscope',
      roles: [TypeUtilisateur.ADMIN],
      links: [
        {
          label: 'Rendez-vous',
          path: '/admin/rendez-vous',
          icon: 'fas fa-calendar-alt',
          roles: [TypeUtilisateur.ADMIN]
        },
        {
          label: 'Validations',
          path: '/admin/validations',
          icon: 'fas fa-clipboard-check',
          roles: [TypeUtilisateur.ADMIN],
          badgeCount: 3
        }
      ]
    },
    
    // Groupe Administration
    {
      label: 'Administration',
      icon: 'fas fa-cogs',
      roles: [TypeUtilisateur.ADMIN],
      links: [
        {
          label: 'Rapports',
          path: '/admin/rapports',
          icon: 'fas fa-chart-line',
          roles: [TypeUtilisateur.ADMIN]
        },
        {
          label: 'Paramètres',
          path: '/admin/parametres',
          icon: 'fas fa-cogs',
          roles: [TypeUtilisateur.ADMIN]
        }
      ]
    },
    
    // Navigation médecin (liens directs car moins nombreux)
    {
      label: 'Mes patients',
      icon: 'fas fa-user-injured',
      roles: [TypeUtilisateur.MEDECIN],
      links: [{
        label: 'Mes patients',
        path: '/medecin/patients',
        icon: 'fas fa-user-injured',
        roles: [TypeUtilisateur.MEDECIN],
        badgeCount: 12
      }]
    },
    {
      label: 'Calendrier',
      icon: 'fas fa-calendar-alt',
      roles: [TypeUtilisateur.MEDECIN],
      links: [{
        label: 'Calendrier',
        path: '/medecin/calendrier',
        icon: 'fas fa-calendar-alt',
        roles: [TypeUtilisateur.MEDECIN]
      }]
    },
    {
      label: 'Consultations',
      icon: 'fas fa-stethoscope',
      roles: [TypeUtilisateur.MEDECIN],
      links: [{
        label: 'Consultations',
        path: '/medecin/consultations',
        icon: 'fas fa-stethoscope',
        roles: [TypeUtilisateur.MEDECIN]
      }]
    },
    {
      label: 'Ordonnances & Examens',
      icon: 'fas fa-prescription-bottle-alt',
      roles: [TypeUtilisateur.MEDECIN],
      links: [
        {
          label: 'Ordonnances',
          path: '/medecin/ordonnances',
          icon: 'fas fa-prescription-bottle-alt',
          roles: [TypeUtilisateur.MEDECIN]
        },
        {
          label: 'Examens',
          path: '/medecin/examens',
          icon: 'fas fa-microscope',
          roles: [TypeUtilisateur.MEDECIN],
          badgeCount: 3
        }
      ]
    }
  ];

  constructor(
    private router: Router,
    private authService: AuthService 
  ) {}

  ngOnInit() {
    this.initUserData();
    this.boundCloseAllMenus = (event: Event) => this.closeAllMenus(event);
    document.addEventListener('click', this.boundCloseAllMenus);
  }

  private initUserData(): void {
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.currentRole = currentUser.type as TypeUtilisateur;
      
      if (this.currentRole === TypeUtilisateur.PATIENT) {
        this.patientName.set(`${currentUser.prenom} ${currentUser.nom}`);
        this.userName = this.patientName();
      } else {
        this.userName = this.currentRole === TypeUtilisateur.MEDECIN 
          ? `Dr. ${currentUser.nom}` 
          : `${currentUser.prenom} ${currentUser.nom}`;
      }
      
      this.email = currentUser.email || '';
      this.role = this.currentRole;
      this.userInitials = currentUser.prenom.charAt(0) + currentUser.nom.charAt(0);
    }
  }

  private closeAllMenus(event?: Event): void {
    if (event && !(event.target as Element).closest('.dashboard-nav-user, .notification-container, .nav-dropdown-parent')) {
      this.userMenuOpen = false;
      this.notificationMenuOpen = false;
      this.openGroups.clear();
    } else if (!event) {
      this.userMenuOpen = false;
      this.notificationMenuOpen = false;
      this.openGroups.clear();
    }
  }

  get filteredNavGroups(): NavGroup[] {
    return this.navGroups.filter(group => 
      group.roles.includes(this.currentRole)
    );
  }
  
  get unreadNotificationCount(): number {
    return this.notifications.filter(n => !n.read).length;
  }

  get urgentNotificationCount(): number {
    return this.notifications.filter(n =>
      !n.read && (n.type === 'error' || n.type === 'warning')
    ).length;
  }

  getDashboardRoute(): string {
    return this.authService.getDashboardRoute();
  }

  // Gestion des groupes de navigation
  toggleGroup(groupLabel: string, event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    
    if (this.openGroups.has(groupLabel)) {
      this.openGroups.delete(groupLabel);
    } else {
      this.openGroups.add(groupLabel);
    }
  }

  isGroupOpen(groupLabel: string): boolean {
    return this.openGroups.has(groupLabel);
  }

  // Navigation directe pour les groupes avec un seul lien
  navigateOrToggle(group: NavGroup, event: Event): void {
    if (group.links.length === 1) {
      // Navigation directe
      this.router.navigate([group.links[0].path]);
      this.closeAllMenus();
    } else {
      // Toggle du groupe
      this.toggleGroup(group.label, event);
    }
  }

  // Vérifier si un groupe est actif
  isGroupActive(group: NavGroup): boolean {
    return group.links.some(link => 
      this.router.url.startsWith(link.path)
    );
  }

  // Compter les badges d'un groupe
  getGroupBadgeCount(group: NavGroup): number {
    return group.links.reduce((total, link) => total + (link.badgeCount || 0), 0);
  }

  // Méthodes pour les notifications
  markNotificationAsRead(notificationId: string): void {
    const notification = this.notifications.find(n => n.id === notificationId);
    if (notification) notification.read = true;
  }

  markAllNotificationsAsRead(): void {
    this.notifications.forEach(n => n.read = true);
  }

  deleteNotification(notificationId: string): void {
    this.notifications = this.notifications.filter(n => n.id !== notificationId);
  }

  // Navigation
  goToProfil(): void {
    this.router.navigate(['/profile']);
    this.closeAllMenus();
  }

  logout(): void {
    if (confirm('Êtes-vous sûr de vouloir vous déconnecter ?')) {
      this.authService.logout();
      this.router.navigate(['/home']);
    }
  }

  getNotificationIcon(type: string): string {
    switch (type) {
      case 'info': return 'fas fa-info-circle text-blue-500';
      case 'warning': return 'fas fa-exclamation-triangle text-yellow-500';
      case 'success': return 'fas fa-check-circle text-green-500';
      case 'error': return 'fas fa-times-circle text-red-500';
      default: return 'fas fa-bell text-gray-500';
    }
  }

  formatTimestamp(timestamp: Date): string {
    const now = new Date();
    const diffInMs = now.getTime() - timestamp.getTime();
    const diffInMinutes = Math.floor(diffInMs / (1000 * 60));
    const diffInHours = Math.floor(diffInMinutes / 60);
    const diffInDays = Math.floor(diffInHours / 24);

    if (diffInMinutes < 60) {
      return `Il y a ${diffInMinutes} min`;
    } else if (diffInHours < 24) {
      return `Il y a ${diffInHours}h`;
    } else {
      return `Il y a ${diffInDays}j`;
    }
  }

  viewMedicalRecord(patientId: string): void {
    this.router.navigate(['/medecin/dossier-medical', patientId]);
  }

  ngOnDestroy() {
    document.removeEventListener('click', this.boundCloseAllMenus);
  }
}