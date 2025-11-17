import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth-service';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient, private auth: AuthService) {}

  private getHeaders(): HttpHeaders {
    const token = this.auth.getToken();
    return new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : '',
      'Content-Type': 'application/json'
    });
  }

  // ========== STUDENT ENDPOINTS ==========
  getStudentMessages(): Observable<any> {
    return this.http.get(`${this.apiUrl}/student/messages`, { 
      headers: this.getHeaders() 
    });
  }

  deleteStudentMessage(messageId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/student/messages/${messageId}`, { 
      headers: this.getHeaders(),
      responseType: 'text'
    });
  }

  // ========== HOST ENDPOINTS ==========
  sendToAllStudents(subject: string, message: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/host/message/all`, 
      { subject, message }, 
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }

  sendToOneStudent(username: string, subject: string, message: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/host/message/one`, 
      { username, subject, message }, 
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }

  // NEW: Get student list for host
  getStudentList(): Observable<any> {
    return this.http.get(`${this.apiUrl}/host/users/students`, {
      headers: this.getHeaders()
    });
  }

  // NEW: Host can change student password
  changeStudentPassword(username: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/host/users/change-password`,
      { username, newPassword },
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }

  // ========== ADMIN ENDPOINTS ==========
  getHostRequests(): Observable<any> {
    return this.http.get(`${this.apiUrl}/admin/host-requests`, { 
      headers: this.getHeaders() 
    });
  }

  approveHost(id: number, adminUsername: string): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/admin/host-requests/${id}/approve`, 
      {}, 
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }

  rejectHost(id: number, adminUsername: string, reason: string): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/admin/host-requests/${id}/reject`, 
      { reason }, 
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }

  // NEW: Get all students for admin
  getAllStudents(): Observable<any> {
    return this.http.get(`${this.apiUrl}/admin/users/students`, {
      headers: this.getHeaders()
    });
  }

  // NEW: Get all hosts for admin
  getAllHosts(): Observable<any> {
    return this.http.get(`${this.apiUrl}/admin/users/hosts`, {
      headers: this.getHeaders()
    });
  }

  // NEW: Admin can change any user password
  changeUserPassword(username: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/admin/users/change-password`,
      { username, newPassword },
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }
}