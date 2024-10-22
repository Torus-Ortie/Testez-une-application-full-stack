import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { RouterTestingModule, } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from '../../../../services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService} from "../../../../services/teacher.service";
import { DetailComponent } from './detail.component';
import {NgZone} from "@angular/core";
import {Session} from "../../interfaces/session.interface";
import {of} from "rxjs";


describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let router: Router;
  let route : ActivatedRoute;
  let ngZone: NgZone;
  let httpMock: HttpTestingController;
  let sessionApiService: SessionApiService;

  const mockSessionService = {
    sessionInformation: {
      admin: false,
      id: 1
    }
  }

  const mockSession: Session = {
    id: 1,
    name: 'Test Session',
    date: new Date(),
    users: [],
    teacher_id: 1,
    description: 'This is a test session',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        MatCardModule,
        MatIconModule
      ],
      declarations: [DetailComponent],
      providers: [
        SessionApiService,
        TeacherService,
        { provide: SessionService, useValue: mockSessionService }
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(DetailComponent);

    component = fixture.componentInstance;

    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    ngZone = TestBed.inject(NgZone);
    httpMock = TestBed.inject(HttpTestingController);
    sessionApiService = TestBed.inject(SessionApiService);

    fixture.detectChanges();

  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch and display session details on init', () => {
    const id = "1";
    component.sessionId = id;
    const detailSpy = jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(mockSession));

    component.ngOnInit();

    fixture.detectChanges();
    expect(component.userId).toEqual(mockSessionService.sessionInformation.id.toString());

    expect(detailSpy).toHaveBeenCalledWith(id);
    expect(component.session).toEqual(mockSession);
  });

  it('should call participate method of sessionApiService and fetchSession when participate is called', () => {
    expect(component.userId).toEqual(mockSessionService.sessionInformation.id.toString());

    component.session = mockSession;
    const participateSpy = jest.spyOn(sessionApiService, 'participate').mockReturnValue(of(undefined));
    const detailSpy = jest.spyOn(sessionApiService, 'detail').mockReturnValue(of({...mockSession, users: [1]}));

    component.participate();

    expect(participateSpy).toHaveBeenCalledWith(component.sessionId, component.userId);
    expect(detailSpy).toHaveBeenCalledWith(component.sessionId);
    expect(component.session.users).toContain(1);
    expect(component.isParticipate).toBe(true);
  });

  it('should call unparticipate method of sessionApiService and fetchSession when unParticipate is called', () => {
    expect(component.userId).toEqual(mockSessionService.sessionInformation.id.toString());

    component.session = mockSession;
    const unparticipateSpy = jest.spyOn(sessionApiService, 'unParticipate').mockReturnValue(of(undefined));
    const detailSpy = jest.spyOn(sessionApiService, 'detail').mockReturnValue(of({...mockSession, users: []}));

    component.unParticipate();

    expect(unparticipateSpy).toHaveBeenCalledWith(component.sessionId, component.userId);
    expect(detailSpy).toHaveBeenCalledWith(component.sessionId);
    expect(component.session.users).not.toContain(1);
    expect(component.isParticipate).toBe(false);
  });

  it('should allow an admin to delete a session', () => {
    mockSessionService.sessionInformation.admin = true;

    // Create the component after setting admin to true
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.isAdmin).toBe(true);

    const deleteSpy = jest.spyOn(sessionApiService, 'delete').mockReturnValue(of(null));

    component.delete();

    expect(deleteSpy).toHaveBeenCalledWith(component.sessionId);
  });

  it('should navigate back when back button is clicked', () => {
    const backSpy = jest.spyOn(window.history, 'back');
    component.back();
    expect(backSpy).toHaveBeenCalled();
    backSpy.mockRestore();
  });
});

