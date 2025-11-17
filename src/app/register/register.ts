import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth-service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class Register {
  username = '';
  email = '';
  password = '';
  role = 'STUDENT';
  message = '';
  loading = false;

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  onRegister() {
    console.log('📝 Register clicked');
    
    // Validation
    if (!this.username || !this.email || !this.password) {
      this.message = '❌ Please fill in all fields';
      return;
    }

    if (this.password.length < 6) {
      this.message = '❌ Password must be at least 6 characters';
      return;
    }

    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email)) {
      this.message = '❌ Please enter a valid email';
      return;
    }

    this.loading = true;
    this.message = '';

    this.auth.register({
      username: this.username,
      password: this.password,
      email: this.email,
      role: this.role
    })
    .subscribe({
      next: (res) => {
        console.log('✅ Registration response:', res);
        this.loading = false;
        
        // res is TEXT string from backend
        const responseText = res.toLowerCase();
        
        // Check if registration was successful
        if (responseText.includes('success') || responseText.includes('registered')) {
          if (this.role === 'STUDENT' || this.role === 'ADMIN') {
            this.message = '✅ Registration successful! Redirecting to login...';
            setTimeout(() => {
              this.router.navigate(['/login']);
            }, 2000);
          } else if (this.role === 'HOST') {
            this.message = '✅ Host registration submitted! Please wait for admin approval.';
            setTimeout(() => {
              this.router.navigate(['/login']);
            }, 3000);
          }
        } else if (responseText.includes('approval') || responseText.includes('pending')) {
          // Host registration pending
          this.message = '✅ ' + res;
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 3000);
        } else {
          // Unknown success response
          this.message = '✅ ' + res;
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        }
      },
      error: (err) => {
        console.error('❌ Registration error:', err);
        this.loading = false;
        
        // Handle different error types
        if (err.status === 400) {
          // Bad request - err.error is TEXT because responseType: 'text'
          const errorMsg = err.error || 'Registration failed';
          
          if (errorMsg.includes('exists') || errorMsg.includes('already')) {
            this.message = '❌ Username or email already exists!';
          } else if (errorMsg.includes('Admin registration')) {
            this.message = '❌ Admin registration is closed!';
          } else {
            this.message = '❌ ' + errorMsg;
          }
        } else if (err.status === 500) {
          this.message = '❌ Server error. Please check backend logs.';
        } else if (err.status === 0) {
          this.message = '❌ Cannot connect to backend. Is it running on port 8080?';
        } else {
          this.message = '❌ ' + (err.error || 'Registration failed');
        }
      }
    });
  }
}