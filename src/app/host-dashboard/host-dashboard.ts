import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../api-service';
import { AuthService } from '../auth-service';

@Component({
  selector: 'app-host-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './host-dashboard.html',
  styleUrls: ['./host-dashboard.css']
})
export class HostDashboard implements OnInit {
  // Message sending fields
  subject = '';
  message = '';
  username = '';
  info = '';
  
  // Password change fields
  selectedStudent = '';
  newPassword = '';
  passwordInfo = '';
  
  // Students list
  students: any[] = [];
  loadingStudents = false;

  // Active tab tracker
  activeTab: 'message' | 'password' | 'students' = 'message';

  constructor(
    private api: ApiService,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadStudents();
  }

  /**
   * LOAD ALL STUDENTS
   */
  loadStudents() {
    this.loadingStudents = true;
    this.api.getStudentList().subscribe({
      next: (res) => {
        this.students = res;
        this.loadingStudents = false;
        console.log('Students loaded:', this.students.length);
      },
      error: (err) => {
        console.error('Failed to load students:', err);
        this.loadingStudents = false;
      }
    });
  }

  /**
   * SEND MESSAGE TO STUDENTS
   */
  send() {
    if (!this.subject.trim() || !this.message.trim()) {
      this.info = '❌ Please fill in both subject and message';
      return;
    }

    if (this.username.trim()) {
      // Send to specific student
      const targetUsername = this.username.trim();
      this.api.sendToOneStudent(targetUsername, this.subject, this.message)
        .subscribe({
          next: res => {
            console.log('Backend response:', res);
            this.info = `✅ ${res}`;
            this.clear();
          },
          error: err => {
            console.error('Error:', err);
            let errorMsg = 'Failed to send message';
            
            if (typeof err.error === 'string') {
              errorMsg = err.error;
            } else if (err.error?.message) {
              errorMsg = err.error.message;
            } else if (err.message) {
              errorMsg = err.message;
            }
            
            this.info = `❌ ${errorMsg}`;
          }
        });
    } else {
      // Send to all students
      this.api.sendToAllStudents(this.subject, this.message)
        .subscribe({
          next: res => {
            console.log('Backend response:', res);
            this.info = `✅ ${res}`;
            this.clear();
          },
          error: err => {
            console.error('Error:', err);
            let errorMsg = 'Failed to broadcast message';
            
            if (typeof err.error === 'string') {
              errorMsg = err.error;
            } else if (err.error?.message) {
              errorMsg = err.error.message;
            } else if (err.message) {
              errorMsg = err.message;
            }
            
            this.info = `❌ ${errorMsg}`;
          }
        });
    }
  }

  /**
   * CHANGE STUDENT PASSWORD
   */
  changePassword() {
    if (!this.selectedStudent.trim()) {
      this.passwordInfo = '❌ Please enter student username';
      return;
    }

    if (!this.newPassword.trim() || this.newPassword.length < 6) {
      this.passwordInfo = '❌ Password must be at least 6 characters';
      return;
    }

    this.api.changeStudentPassword(this.selectedStudent, this.newPassword)
      .subscribe({
        next: (res) => {
          this.passwordInfo = `✅ ${res}`;
          this.selectedStudent = '';
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
   * CLEAR MESSAGE FORM
   */
  clear() {
    this.subject = '';
    this.message = '';
    this.username = '';
  }

  /**
   * LOGOUT
   */
  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}