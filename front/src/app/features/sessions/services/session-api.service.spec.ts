import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';
import { mockSession } from "../../../../test-constants";

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;
  let session: Session;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SessionApiService]
    });

    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
    session = mockSession;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should delete the session', () => {
    const id = '1';

    service.delete(id).subscribe();

    const req = httpMock.expectOne(`api/session/${id}`);

    expect(req.request.method).toBe('DELETE');

    req.flush(null);
  });

  it('should create a session', () => {
    service.create(session).subscribe();

    const req = httpMock.expectOne('api/session');

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(session);

    req.flush(session);
  });

  it('should update a session', () => {
    const id = '1';
  
    service.update(id, session).subscribe();
  
    const req = httpMock.expectOne(`api/session/${id}`);
  
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(session);
  
    req.flush(session);
  });

  it('should participate in a session', () => {
    const sessionId = '1';
    const userId = '1';
  
    service.participate(sessionId, userId).subscribe();

    const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
  
    expect(req.request.method).toBe('POST');
  
    req.flush(null);
  });

  it('should unparticipate from a session', () => {
    const sessionId = '1';
    const userId = '1';

    service.unParticipate(sessionId, userId).subscribe();

    const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
  
    expect(req.request.method).toBe('DELETE');
  
    req.flush(null);
  });
});