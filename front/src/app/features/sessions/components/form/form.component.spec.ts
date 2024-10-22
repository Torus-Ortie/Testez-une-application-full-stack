import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { NgZone } from '@angular/core';
import { FormComponent } from './form.component';
import { Session } from 'src/app/features/sessions/interfaces/session.interface';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { mockSessionInformation, mockTeacher } from "../../../../../test-constants";
import { TeacherService } from '../../../../services/teacher.service';
import { ListComponent } from "../list/list.component";

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: Router;
  let route : ActivatedRoute;
  let ngZone: NgZone;
  let httpMock: HttpTestingController;
  let sessionService: SessionService;

  //Partial of session interface to match available form inputs
  const mockSession: Partial<Session> = {
    name: 'testSession',
    description: 'test session description',
    date: new Date(),
    teacher_id: 1
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({

      imports: [
        RouterTestingModule.withRoutes([
          { path: 'sessions', component: ListComponent}
        ]),
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        NoopAnimationsModule
      ],
      providers: [
        SessionService,
        SessionApiService,
        TeacherService
      ],
      declarations: [FormComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    ngZone = TestBed.inject(NgZone);
    httpMock = TestBed.inject(HttpTestingController);

    sessionService = TestBed.inject(SessionService);
    sessionService.sessionInformation = {
      ...mockSessionInformation,
      admin: true
    };

    fixture.detectChanges();

    const req = httpMock.expectOne('api/teacher');
    expect(req.request.method).toEqual('GET');
    req.flush([mockTeacher]);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('initForm', () => {
    it('should create the form with empty fields when the route is create', () => {
      component.ngOnInit();
      expect(component.sessionForm).toBeTruthy();
      expect(component.sessionForm?.dirty).toBe(false);
      expect(component.sessionForm?.contains('name')).toBe(true);
      expect(component.sessionForm?.contains('date')).toBe(true);
      expect(component.sessionForm?.contains('teacher_id')).toBe(true);
      expect(component.sessionForm?.contains('description')).toBe(true);
    });

    it('should initialize the form with the session details when the route includes "update"', () => {
      const id = "1";
      jest.spyOn(router, 'url', 'get').mockReturnValue('update');
      jest.spyOn(route.snapshot.paramMap, 'get').mockReturnValue(id);

      component.ngOnInit();

      expect(component.sessionForm).toBeDefined();
      expect(component.onUpdate).toBe(true);
      const req = httpMock.expectOne(`api/session/${id}`);
      expect(req.request.method).toEqual('GET');

      const formattedDate = mockSession.date?.toISOString().split('T')[0];
      const mockSessionWithFormattedDate = {
        ...mockSession,
        date: formattedDate,
      }
      req.flush(mockSessionWithFormattedDate);
      expect(component.sessionForm?.value).toEqual(mockSessionWithFormattedDate);
    });

    it('should navigate to /sessions if user is not an admin', () => {
      sessionService.sessionInformation = {...mockSessionInformation, admin: false };
      const navigateSpy = jest.spyOn(router,'navigate');

      ngZone.run(() => {
        component.ngOnInit();
        expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
      });
    });
  });

  describe('submit', () => {
    let navigateSpy: jest.SpyInstance;
    let snackSpy: jest.SpyInstance;

    beforeEach(() => {
      navigateSpy = jest.spyOn(router, 'navigate');
      const matSnackBar = TestBed.inject(MatSnackBar);
      snackSpy = jest.spyOn(matSnackBar, 'open');
    });

    it('should call submit as a POST request when onUpdate is false', () => {
      component.onUpdate = false;
      ngZone.run(() => {
        component.submit();

        const req = httpMock.expectOne('api/session');
        expect(req.request.method).toEqual('POST');
        req.flush(mockSession);

        expect(snackSpy).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
        expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
      });
    });

    it('should call submit as a PUT request when onUpdate is true', () => {
      component.onUpdate = true;
      const id = '1'
      Object.defineProperty(component, 'id', { value: id})

      ngZone.run(() => {
        component.submit();

        const req = httpMock.expectOne(`api/session/${id}`);
        expect(req.request.method).toEqual('PUT');
        req.flush(true);

        expect(snackSpy).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
        expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
      });
    });
  });
});
