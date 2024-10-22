import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { UserService } from './user.service';
import { subscribeSpyTo } from '@hirez_io/observer-spy';
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { mockUser } from "../../test-constants";

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return user', async () => {
    const testUser = mockUser;
    const testUserId = testUser.id.toString();

    const observerSpy = subscribeSpyTo(service.getById(testUserId));
    const req = httpMock.expectOne(`api/user/${testUserId}`);
    req.flush(testUser);
    await observerSpy.onComplete();

    expect(observerSpy.getLastValue()).toEqual(testUser);
  });

  it('should delete user', async () => {
    const userId = mockUser.id.toString();
    const deleteRoute = `api/user/${userId}`;

    const observerSpy = subscribeSpyTo(service.delete(userId));
    const req = httpMock.expectOne(deleteRoute);
    req.flush({});
    await observerSpy.onComplete();
    expect(observerSpy.getLastValue()).toEqual({});

    expect(req.request.method).toBe('DELETE');
    expect(req.request.url).toBe(deleteRoute);
  });
});
