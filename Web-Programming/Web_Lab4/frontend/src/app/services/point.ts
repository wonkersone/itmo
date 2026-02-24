import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Point } from '../models/point.model';

@Injectable({
  providedIn: 'root'
})
export class PointService {
  private apiUrl = '/api/points';

  constructor(private http: HttpClient) { }

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getPoints(): Observable<Point[]> {
    return this.http.get<Point[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  addPoint(point: Point): Observable<Point> {
    return this.http.post<Point>(this.apiUrl, point, { headers: this.getHeaders() });
  }

  clearPoints(): Observable<any> {
    return this.http.delete(this.apiUrl, { headers: this.getHeaders() });
  }
}
