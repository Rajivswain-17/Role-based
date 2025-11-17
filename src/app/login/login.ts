import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth-service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {
  username = '';
  password = '';
  message = '';
  loading = false;

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  onLogin() {
    if (!this.username || !this.password) {
      this.message = '❌ Please fill in all fields';
      return;
    }

    this.loading = true;
    this.message = '';

    this.auth.login({ username: this.username, password: this.password }).subscribe({
      next: res => {
        this.loading = false;
        this.message = '✅ Login successful! Redirecting...';
        
        // Store username along with token and roles
        this.auth.storeUserData(res.token, res.roles, res.username);
        
        const role = res.roles[0].replace('ROLE_', '').toLowerCase();
        
        setTimeout(() => {
          this.router.navigate([`/${role}`]);
        }, 1000);
      },
      error: err => {
        this.loading = false;
        this.message = '❌ ' + (err.error?.message || 'Invalid credentials');
      }
    });
  }
}