import { Component, OnInit, AfterViewInit, OnDestroy, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PointService } from '../../services/point';
import { Point } from '../../models/point.model';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './main.html',
  styleUrl: './main.css'
})
export class MainComponent implements OnInit, AfterViewInit, OnDestroy {
  points: Point[] = [];
  newPoint: any = { x: 0, y: '', r: 1, hit: false };

  yError: string | null = null;
  rError: string | null = null;

  private canvasTimeout: any;

  constructor(
    private pointService: PointService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private zone: NgZone
  ) {}

  ngOnInit(): void {
    if (!localStorage.getItem('authToken')) {
      this.router.navigate(['/']);
      return;
    }
    this.loadPoints();
  }

  ngAfterViewInit(): void {
    this.canvasTimeout = setTimeout(() => this.drawCanvas(), 50);
  }

  ngOnDestroy(): void {
    if (this.canvasTimeout) {
      clearTimeout(this.canvasTimeout);
    }
  }

  logout(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    this.router.navigate(['/']);
  }

  loadPoints(): void {
    this.pointService.getPoints().subscribe({
      next: (data: Point[]) => {
        this.zone.run(() => {
          this.points = [...data];
          this.drawCanvas();
          this.cdr.detectChanges();
        });
      },
      error: (err: any) => console.error("Ошибка при загрузке истории:", err)
    });
  }

  validateY(): void {
    const val = parseFloat(this.newPoint.y.toString().replace(',', '.'));
    if (isNaN(val)) {
      this.yError = "Введите число";
    } else if (val <= -3 || val >= 5) {
      this.yError = "Интервал: (-3 ... 5)";
    } else {
      this.yError = null;
    }
  }

  onRChange(): void {
    const r = parseFloat(this.newPoint.r);
    if (isNaN(r) || r <= 0) {
      this.rError = "Радиус R должен быть > 0";
    } else {
      this.rError = null;
    }
    this.drawCanvas();
  }

  addPoint(): void {
    this.validateY();
    this.onRChange();

    if (this.yError || this.rError) return;

    const y = parseFloat(this.newPoint.y.toString().replace(',', '.'));
    const x = parseFloat(this.newPoint.x);
    const r = parseFloat(this.newPoint.r);

    this.pointService.addPoint({ x, y, r }).subscribe({
      next: (result: Point) => {
        this.zone.run(() => {
          // Создаем новый массив с новой точкой в начале
          this.points = [result, ...this.points];
          this.drawCanvas();
          this.cdr.detectChanges();
        });
      },
      error: () => alert("Ошибка при отправке данных на сервер")
    });
  }

  clearPoints(): void {
    if (!confirm("Вы уверены, что хотите очистить историю?")) return;

    this.pointService.clearPoints().subscribe({
      next: () => {
        this.zone.run(() => {
          this.points = [];
          this.drawCanvas();
          this.cdr.detectChanges();
        });
      },
      error: (err: any) => {
        console.error("Ошибка при очистке:", err);
        alert("Не удалось очистить историю на сервере");
      }
    });
  }

  onCanvasClick(event: MouseEvent): void {
    const canvas = document.getElementById('graphCanvas') as HTMLCanvasElement;
    if (!canvas) return;

    const rect = canvas.getBoundingClientRect();
    const w = canvas.width;
    const center = w / 2;
    const step = w / 12;

    const xClick = (event.clientX - rect.left - center) / step;
    const yClick = (center - (event.clientY - rect.top)) / step;

    this.addPointFromCoords(parseFloat(xClick.toFixed(2)), parseFloat(yClick.toFixed(2)));
  }

  private addPointFromCoords(x: number, y: number): void {
    this.onRChange();
    if (this.rError) {
      alert("Сначала выберите положительный радиус R");
      return;
    }

    const r = parseFloat(this.newPoint.r);

    this.pointService.addPoint({ x, y, r }).subscribe({
      next: (result: Point) => {
        this.zone.run(() => {
          this.points = [result, ...this.points];
          this.drawCanvas();
          this.cdr.detectChanges();
        });
      },
      error: (err: any) => console.error("Ошибка при сохранении клика:", err)
    });
  }

  drawCanvas(): void {
    const canvas = document.getElementById('graphCanvas') as HTMLCanvasElement;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const r = parseFloat(this.newPoint.r);
    const w = canvas.width;
    const h = canvas.height;
    const center = w / 2;
    const step = w / 12;

    ctx.clearRect(0, 0, w, h);

    if (r > 0) {
      ctx.fillStyle = 'rgba(0, 180, 216, 0.4)';
      ctx.fillRect(center, center - r * step, r * step, r * step);

      ctx.beginPath();
      ctx.moveTo(center, center);
      ctx.lineTo(center - r * step, center);
      ctx.lineTo(center, center - (r / 2) * step);
      ctx.closePath();
      ctx.fill();

      ctx.beginPath();
      ctx.moveTo(center, center);
      ctx.arc(center, center, (r / 2) * step, 0, 0.5 * Math.PI, false);
      ctx.closePath();
      ctx.fill();
    }

    ctx.strokeStyle = '#caf0f8';
    ctx.lineWidth = 1.5;
    ctx.beginPath();
    ctx.moveTo(20, center); ctx.lineTo(w - 20, center);
    ctx.lineTo(w - 30, center - 5); ctx.moveTo(w - 20, center); ctx.lineTo(w - 30, center + 5);
    ctx.moveTo(center, h - 20); ctx.lineTo(center, 20);
    ctx.lineTo(center - 5, 30); ctx.moveTo(center, 20); ctx.lineTo(center + 5, 30);
    ctx.stroke();

    if (!isNaN(r) && r !== 0) {
      ctx.fillStyle = '#caf0f8';
      ctx.font = '12px Arial';
      const labels = [r, r / 2, -r / 2, -r];
      labels.forEach(val => {
        ctx.beginPath();
        ctx.moveTo(center + val * step, center - 5);
        ctx.lineTo(center + val * step, center + 5);
        ctx.stroke();
        ctx.fillText(val.toString(), center + val * step - 5, center + 18);

        ctx.beginPath();
        ctx.moveTo(center - 5, center - val * step);
        ctx.lineTo(center + 5, center - val * step);
        ctx.stroke();
        ctx.fillText(val.toString(), center + 10, center - val * step + 5);
      });
    }

    this.points.forEach(p => {
      const isHitNow = this.checkLocalHit(p.x, p.y, r);
      ctx.fillStyle = isHitNow ? '#00e676' : '#ff5252';
      ctx.beginPath();
      ctx.arc(center + p.x * step, center - p.y * step, 4, 0, 2 * Math.PI);
      ctx.fill();
      ctx.strokeStyle = '#fff';
      ctx.lineWidth = 0.5;
      ctx.stroke();
    });
  }

  checkLocalHit(x: number, y: number, r: number): boolean {
    if (isNaN(r) || r <= 0) return false;
    if (x >= 0 && x <= r && y >= 0 && y <= r) return true;
    if (x <= 0 && y >= 0 && y <= (0.5 * x + r / 2)) return true;
    if (x >= 0 && y <= 0 && (x * x + y * y) <= (r / 2) * (r / 2)) return true;
    return false;
  }
}
