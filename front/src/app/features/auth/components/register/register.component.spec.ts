import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RegisterComponent } from './register.component';
import { AuthService } from "../../services/auth.service";
import { RouterTestingModule } from "@angular/router/testing";
import { Router } from '@angular/router';
import { NgZone } from '@angular/core';
import { mockRegisterRequest, mockSessionInformation } from "../../../../../test-constants";
import { LoginComponent } from "../login/login.component";

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let httpMock: HttpTestingController;
  let router: Router;
  let ngZone: NgZone;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [
        AuthService,
      ],
      imports: [
        RouterTestingModule.withRoutes([
          { path: "login", component: LoginComponent}
        ]),
        BrowserAnimationsModule,
        HttpClientTestingModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    ngZone = TestBed.inject(NgZone);
    jest.spyOn(router, 'navigate');
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create the register component', () => {
    expect(component).toBeTruthy();
    expect(component.onError).toBe(false);
    expect(component.form.dirty).toBe(false);
    const button = fixture.debugElement.nativeElement.querySelector('button[type="submit"]');
    expect(button.disabled).toBe(true);
  });

  it('should call AuthService.register() with correct parameters and navigate to /login when registration attempt is successful', () => {
    component.form.setValue(mockRegisterRequest);
    fixture.detectChanges();

    expect(component.form.valid).toBe(true);
    const button = fixture.debugElement.nativeElement.querySelector('button[type="submit"]');
    expect(button.disabled).toBe(false);

    ngZone.run(() => {
        component.submit();

        const req = httpMock.expectOne('api/auth/register');
        expect(req.request.body).toEqual(mockRegisterRequest);
        req.flush(mockSessionInformation);
        expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
  });


  it('should set onError to true on failed registration attempt', () => {
    component.form.setValue(mockRegisterRequest);
    fixture.detectChanges();
    ngZone.run(() => {
      component.submit();

      const req = httpMock.expectOne('api/auth/register');
      req.flush('Error', { status: 500, statusText: 'Server Error' });

      fixture.detectChanges();

      expect(component.onError).toBe(true);
      const errorMessage = fixture.debugElement.nativeElement.querySelector('.error');
      expect(errorMessage.textContent).toContain('An error occurred');
    });
  });
});
