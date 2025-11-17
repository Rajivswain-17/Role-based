import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(data: any): Observable<any> {
    console.log('🔐 Attempting login for:', data.username);
    return this.http.post(`${this.apiUrl}/login`, data, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  register(data: any): Observable<any> {
    console.log('📝 Attempting registration:', data);
    return this.http.post(`${this.apiUrl}/register`, data, { 
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
      responseType: 'text' 
    });
  }

  logout(): void {
    console.log('👋 Logging out');
    localStorage.clear();
  }

  storeUserData(token: string, roles: string[], username: string): void {
    console.log('💾 Storing user data');
    console.log('Token:', token);
    console.log('Roles:', roles);
    console.log('Username:', username);
    localStorage.setItem('token', token);
    localStorage.setItem('roles', JSON.stringify(roles));
    localStorage.setItem('username', username);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUserRoles(): string[] {
    const roles = localStorage.getItem('roles');
    return roles ? JSON.parse(roles) : [];
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}