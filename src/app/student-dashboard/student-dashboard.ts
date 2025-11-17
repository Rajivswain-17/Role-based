import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ApiService } from '../api-service';
import { AuthService } from '../auth-service';

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './student-dashboard.html',
  styleUrl: './student-dashboard.css',
})
export class StudentDashboard implements OnInit {
  messages: any[] = [];
  loading = true;
  deleting = false;
  message = '';
  studentUsername = '';
  expandedMessages: { [key: number]: boolean } = {};

  constructor(
    private api: ApiService,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.studentUsername = this.getStudentUsername();
    this.loadMessages();
  }

  getStudentUsername(): string {
    // Get username from localStorage
    return localStorage.getItem('username') || 'Student';
  }

  loadMessages() {
    this.loading = true;
    this.message = '';
    this.api.getStudentMessages().subscribe({
      next: (r) => {
        this.messages = r;
        this.loading = false;
        console.log('Messages loaded:', this.messages);
      },
      error: (err) => {
        console.error('Failed to load messages:', err);
        this.message = '❌ Failed to load messages';
        this.loading = false;
      }
    });
  }

  toggleMessage(index: number) {
    this.expandedMessages[index] = !this.expandedMessages[index];
  }

  deleteMessage(messageId: number, event: Event) {
    // Stop event propagation to prevent toggle
    event.stopPropagation();

    if (!confirm('Are you sure you want to delete this message?')) {
      return;
    }

    this.deleting = true;
    this.message = '';
    
    this.api.deleteStudentMessage(messageId).subscribe({
      next: (res) => {
        this.message = '✅ Message deleted successfully!';
        this.deleting = false;
        this.loadMessages(); // Reload messages after deletion
      },
      error: (err) => {
        this.message = '❌ Failed to delete message: ' + (err.error || 'Unknown error');
        this.deleting = false;
      }
    });
  }

  formatDate(dateString: any): string {
    if (!dateString) return 'Recently';
    
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins} min${diffMins > 1 ? 's' : ''} ago`;
    if (diffHours < 24) return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
    if (diffDays < 7) return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
    
    return date.toLocaleDateString('en-US', { 
      month: 'short', 
      day: 'numeric', 
      year: date.getFullYear() !== now.getFullYear() ? 'numeric' : undefined 
    });
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}