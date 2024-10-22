import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { subscribeSpyTo } from '@hirez_io/observer-spy';
import {Teacher} from "../interfaces/teacher.interface";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";

import { TeacherService } from './teacher.service';
import { mockTeacher } from "../../test-constants";

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TeacherService]
  });

    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return all teachers', async () => {
    const testTeachers: Teacher[] = [
      mockTeacher,
      {...mockTeacher, id: 2}
    ];

    const observerSpy = subscribeSpyTo(service.all());
    const req = httpMock.expectOne('api/teacher');
    req.flush(testTeachers);
    await observerSpy.onComplete();

    expect(observerSpy.getLastValue()).toEqual(testTeachers);
  });

  it('should return details of one teacher', async () => {
    const testTeacher = mockTeacher;

    const observerSpy = subscribeSpyTo(service.detail("1"));
    const req = httpMock.expectOne('api/teacher/1');
    req.flush(testTeacher);
    await observerSpy.onComplete();

    expect(observerSpy.getLastValue()).toEqual(testTeacher);
  });
});
