import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';  // IMPORTANT: Must be imported
import { Router } from '@angular/router';
import { ApiService } from '../api-service';
import { AuthService } from '../auth-service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],  // IMPORTANT: FormsModule must be here
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css'],
})
export class AdminDashboard implements OnInit {
  // Host requests
  requests: any[] = [];
  message = '';
  loading = true;
  processing = false;

  // User lists
  students: any[] = [];
  hosts: any[] = [];
  loadingUsers = false;

  // Password change
  selectedUser = '';
  newPassword = '';
  passwordInfo = '';

  // Active tab tracker
  activeTab: 'requests' | 'students' | 'hosts' | 'password' = 'requests';

  constructor(
    private api: ApiService,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadRequests();
    this.loadAllUsers();
  }

  /**
   * LOAD HOST REQUESTS
   */
  loadRequests() {
    this.loading = true;
    this.message = '';
    
    this.api.getHostRequests().subscribe({
      next: (r) => {
        this.requests = r;
        this.loading = false;
        console.log('Host requests loaded:', this.requests.length);
      },
      error: (err) => {
        console.error('Failed to load requests:', err);
        this.message = '❌ Failed to load requests';
        this.loading = false;
      }
    });
  }

  /**
   * LOAD ALL USERS (STUDENTS AND HOSTS)
   */
  loadAllUsers() {
    this.loadingUsers = true;
    
    // Load students
    this.api.getAllStudents().subscribe({
      next: (res) => {
        this.students = res;
        console.log('Students loaded:', this.students.length);
      },
      error: (err) => console.error('Failed to load students:', err)
    });

    // Load hosts
    this.api.getAllHosts().subscribe({
      next: (res) => {
        this.hosts = res;
        this.loadingUsers = false;
        console.log('Hosts loaded:', this.hosts.length);
      },
      error: (err) => {
        console.error('Failed to load hosts:', err);
        this.loadingUsers = false;
      }
    });
  }

  /**
   * APPROVE HOST REQUEST
   */
  approve(id: number) {
    this.processing = true;
    this.message = '';
    
    this.api.approveHost(id, 'admin').subscribe({
      next: (res) => {
        this.message = '✅ Host approved successfully!';
        this.processing = false;
        this.loadRequests();
        this.loadAllUsers(); // Refresh users list
      },
      error: (err) => {
        this.message = '❌ Failed to approve host';
        this.processing = false;
      }
    });
  }

  /**
   * REJECT HOST REQUEST
   */
  reject(id: number) {
    this.processing = true;
    this.message = '';
    
    this.api.rejectHost(id, 'admin', 'Not suitable').subscribe({
      next: (res) => {
        this.message = '✅ Host request rejected';
        this.processing = false;
        this.loadRequests();
      },
      error: (err) => {
        this.message = '❌ Failed to reject host';
        this.processing = false;
      }
    });
  }

  /**
   * CHANGE USER PASSWORD (ANY USER)
   */
  changePassword() {
    if (!this.selectedUser.trim()) {
      this.passwordInfo = '❌ Please enter username';
      return;
    }

    if (!this.newPassword.trim() || this.newPassword.length < 6) {
      this.passwordInfo = '❌ Password must be at least 6 characters';
      return;
    }

    this.api.changeUserPassword(this.selectedUser, this.newPassword)
      .subscribe({
        next: (res) => {
          this.passwordInfo = `✅ ${res}`;
          this.selectedUser = '';
          this.newPassword = '';
        },
        error: (err) => {
          let errorMsg = 'Failed to change password';
          if (typeof err.error === 'string') {
            errorMsg = err.error;
          }
          this.passwordInfo = `❌ ${errorMsg}`;
        }
      });
  }

  /**
   * LOGOUT
   */
  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}