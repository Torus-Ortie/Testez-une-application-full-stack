import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { ObserverSpy } from '@hirez_io/observer-spy';

import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';
import { mockSessionInformation } from "../../test-constants";

describe('SessionService', () => {
  let service: SessionService;
  let user: SessionInformation;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
    user = mockSessionInformation;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('$isLogged', () => {
    it('should emit the logged in status as an Observable boolean', () => {
      const observerSpy = new ObserverSpy();
      service.isLogged = true;
      service.$isLogged().subscribe(observerSpy);

      expect(observerSpy.receivedNext()).toBe(true);
    });
  });

  describe('logIn', () => {
    it('should update isLogged to true and sessionInformation when logIn is called', () => {
      const observerSpy = new ObserverSpy<boolean>();
      service.$isLogged().subscribe(observerSpy);
      service.logIn(user);

      expect(service.isLogged).toBe(true);
      expect(service.sessionInformation).toBe(user);
      expect(observerSpy.getLastValue()).toBe(true);
    });
  });

  describe('logOut', () => {
    it('should update isLogged to false and sessionInformation to undefined when logOut is called', () => {
      service.logIn(user);
      const observerSpy = new ObserverSpy<boolean>();
      service.$isLogged().subscribe(observerSpy);

      service.logOut();

      expect(service.isLogged).toBe(false);
      expect(service.sessionInformation).toBeUndefined();
      expect(observerSpy.getLastValue()).toBe(false);
    });
  });
});
