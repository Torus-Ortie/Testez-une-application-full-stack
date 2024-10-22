import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SessionService } from 'src/app/services/session.service';
import { expect } from '@jest/globals';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LoginComponent } from './login.component';
import { mockSessionInformation, mockLoginRequest} from "../../../../../test-constants";
import { NgZone } from "@angular/core";

describe('LoginComponent integration', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let httpMock: HttpTestingController;
  let router: Router;
  let ngZone: NgZone;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        AuthService,
        SessionService
      ],
      imports: [
        RouterTestingModule.withRoutes(
          [
            { path: 'sessions', redirectTo: '/sessions'}
          ]
        ),
        BrowserAnimationsModule,
        HttpClientTestingModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule]
    })
      .compileComponents();
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    ngZone = TestBed.inject(NgZone);
    httpMock = TestBed.inject(HttpTestingController);
    jest.spyOn(router, 'navigate');

    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create the login component', () => {
    expect(component).toBeTruthy();
    expect(component.hide).toBe(true);
    expect(component.onError).toBe(false);
    expect(component.form.dirty).toBe(false);
    const button = fixture.debugElement.nativeElement.querySelector('button[type="submit"]');
    expect(button.disabled).toBe(true);
  });


  it('should call AuthService.login() with correct parameters and navigate to /sessions when login attempt is successful', () => {
    component.form.setValue(mockLoginRequest);
    fixture.detectChanges();

    expect(component.form.valid).toBe(true);
    const button = fixture.debugElement.nativeElement.querySelector('button[type="submit"]');
    expect(button.disabled).toBe(false);

    ngZone.run(() => {
      component.submit();

      const req = httpMock.expectOne('api/auth/login');
      expect(req.request.body).toEqual(mockLoginRequest);
      req.flush(mockSessionInformation);

      expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    });
  });

  it('should set onError to true on failed login attempt', () => {
    component.form.setValue(mockLoginRequest);
    fixture.detectChanges();

    ngZone.run(() => {
      component.submit();

      const req = httpMock.expectOne('api/auth/login');
      req.flush('Error', { status: 500, statusText: 'Server Error' });

      fixture.detectChanges();

      expect(component.onError).toBe(true);
      const errorMessage = fixture.debugElement.nativeElement.querySelector('.error');
      expect(errorMessage.textContent).toContain('An error occurred');
    });
  });
});