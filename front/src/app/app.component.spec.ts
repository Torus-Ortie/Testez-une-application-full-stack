import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import { AuthService } from './features/auth/services/auth.service';
import { SessionService } from './services/session.service';
import { expect } from '@jest/globals';
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { MatToolbarModule } from '@angular/material/toolbar';
import { NgZone } from "@angular/core";
import { By } from '@angular/platform-browser';
import { of } from "rxjs";

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let sessionService: SessionService;
  let router: Router;
  let ngZone: NgZone;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule, MatToolbarModule],
      declarations: [AppComponent],
      providers: [AuthService, SessionService]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    ngZone = TestBed.inject(NgZone);
    fixture.detectChanges();
  });

  it('should create the app with isLogged as false', () => {
    expect(component).toBeTruthy();
    const loginLink = fixture.debugElement.query(By.css('[routerLink="login"]'));
    const registerLink = fixture.debugElement.query(By.css('[routerLink="register"]'));
    const meLink = fixture.debugElement.query(By.css('[routerLink="me"]'));

    component.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(false);
      expect(loginLink).toBeTruthy();
      expect(registerLink).toBeTruthy();
      expect(meLink).toBeFalsy();
    });
  });

  it('should create the app with isLogged as true', () => {
    jest.spyOn(sessionService, '$isLogged').mockReturnValue(of(true));

    fixture.detectChanges();

    const loginLink = fixture.debugElement.query(By.css('[routerLink="login"]'));
    const registerLink = fixture.debugElement.query(By.css('[routerLink="register"]'));
    const meLink = fixture.debugElement.query(By.css('[routerLink="me"]'));

    component.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(true);
      expect(loginLink).toBeFalsy();
      expect(registerLink).toBeFalsy();
      expect(meLink).toBeTruthy();
    });
  });

  it('should call logOut method of SessionService and navigate to the root route', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const isLoggedSpy = jest.spyOn(sessionService, '$isLogged').mockReturnValue(of(true));

    fixture.detectChanges(); // update view with new isLogged value

    let meLink = fixture.debugElement.query(By.css('[routerLink="me"]'));
    let logoutLink = fixture.debugElement.queryAll(By.css('.link'))
      .find(de => de.nativeElement.textContent === 'Logout');

    component.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(true);
    });

    expect(meLink).toBeTruthy();
    expect(logoutLink).toBeTruthy();

    jest.spyOn(sessionService, 'logOut');

    ngZone.run(() => {
      component.logout();
      isLoggedSpy.mockRestore();
      fixture.detectChanges();
    });

    let loginLink = fixture.debugElement.query(By.css('[routerLink="login"]'));
    let meLinkAfterLogout = fixture.debugElement.query(By.css('[routerLink="me"]'));
    expect(sessionService.logOut).toHaveBeenCalled();
    component.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(false);
    });
    expect(navigateSpy).toHaveBeenCalledWith(['']);
    expect(loginLink).toBeTruthy();
    expect(meLinkAfterLogout).toBeFalsy();
  });
});
