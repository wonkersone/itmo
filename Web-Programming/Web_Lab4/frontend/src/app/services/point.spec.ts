import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { PointService } from './point';

describe('PointService', () => {
  let service: PointService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PointService]
    });
    service = TestBed.inject(PointService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
