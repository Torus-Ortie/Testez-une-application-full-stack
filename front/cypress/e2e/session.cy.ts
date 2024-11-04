/// <reference types="Cypress" />

describe('Sessions page', () => {
    const sessions = [
        {
            id: 1,
            name: 'Hot yoga session',
            description:  'Hot yoga session description',
            date: new Date(),
            teacher_id: 1,
            users: [1, 2],
            createdAt: new Date(),
            updatedAt: new Date(),
        },
        {
            id: 2,
            name: 'Yoga session',
            description:  'Regular Yoga session description',
            date: new Date(),
            teacher_id: 1,
            users: [1, 2],
            createdAt: new Date(),
            updatedAt: new Date(),
        }
    ];
  
    const newSession = {
        id: 3,
        name: 'New yoga session',
        description:  'New yoga session description',
        date: new Date(),
        teacher_id: 1,
        users: [],
        createdAt: new Date(),
        updatedAt: new Date(),
    };
  
    const sessionsWithNewSession = [
        ...sessions,
        newSession,
    ];
  
    const updatedSession = {
        ...newSession,
        name: 'Updated yoga session',
        description:  'Updated yoga session description',
    }
  
    const sessionsWithUpdatedSession = [
            ...sessions,
        updatedSession,
    ];
  
    const teachers = [
        {
            id: 1,
            lastName: 'DELAHAYE',
            firstName: 'Margot',
            createdAt: new Date(),
            updatedAt: new Date(),
        },
    ]
  
    const admin = {
        token: 'token',
        type: 'Bearer',
        id: 1,
        username: 'yoga_admin',
        firstName: 'Yoga',
        lastName: 'Studio',
        admin: true,
    }
  
    const loginCredentials = {
        email: 'yoga@studio.com',
        password: 'pass!1234',
    }
  
    describe('test when the user is an admin', () => {
  
        before(() => {
            cy.visit('/login');
            cy.intercept('POST', '/api/auth/login', {
            statusCode: 201,
            body: admin,
            }).as('login');
    
            cy.intercept('GET', '/api/session', sessions).as('sessions');
    
            cy.get('input[formControlName=email]').type(loginCredentials.email);
            cy.get('input[formControlName=password]').type(`${loginCredentials.password}{enter}{enter}`);
            cy.url().should('eq', `${Cypress.config().baseUrl}sessions`);
        });
    
        it('should display the list of sessions', () => {
            cy.get('mat-card.item').should('have.length', sessions.length);
        });
    
        it('should display a button to create a session', () => {
            cy.get('[routerLink=create]').should('exist');
        });
    
        it('should display a button to view details of a session', () => {
            cy.get('mat-card-actions').find('button[mat-raised-button]').contains('Detail').should('exist');
        });
    
        describe('create session form', () => {
    
            before(() => {
                cy.intercept('GET', '/api/teacher', {
                    body: teachers
                }).as('teachers');
        
                cy.get('mat-card.item').should('have.length', sessions.length);
        
                cy.get('[routerLink=create]').click();
                cy.url().should('eq', `${Cypress.config().baseUrl}sessions/create`);
            })
    
            it('should display an error if a required field is missing', () => {
                cy.get('input[formControlName=name]').type(`{enter}`);
                cy.get('input[formControlName="name"]').should('have.class', 'ng-invalid');
                cy.get('button[type="submit"]').should('be.disabled');
            });
    
            it('should create a new session when required fields are entered', () => {
    
                cy.intercept('POST', '/api/session', {
                    statusCode: 201,
                    body: newSession,
                }).as('session');
        
                cy.intercept('GET', '/api/session', {
                    body: sessionsWithNewSession,
                }).as('sessions');
        
                cy.get('input[formControlName=name]').type(newSession.name);
                cy.get('input[formControlName=date]').type(newSession.date.toISOString().split('T')[0]);
                cy.get('textarea[formControlName=description]').type(newSession.description);
                cy.get('mat-select[formControlName=teacher_id]').click();
                cy.get('mat-option').contains(`${teachers[0].firstName} ${teachers[0].lastName}`).click();
        
                cy.get('button[type="submit"]').contains('Save').click();
                cy.get('snack-bar-container').contains('Session created !').should('exist');
                cy.url().should('eq', `${Cypress.config().baseUrl}sessions`);
        
                cy.get('mat-card.item').should('have.length', sessions.length + 1);
        
                cy.get('mat-card-title').contains(newSession.name, {matchCase: false}).should('exist');
    
            });
    
        });
    
        describe('update session form', () => {
            before(() => {
                cy.intercept('GET', '/api/teacher', {
                    body: teachers
                }).as('teachers');
        
                cy.get('mat-card.item').eq(newSession.id - 1).within(() => {
                    cy.get('mat-card-title').contains(newSession.name, {matchCase: false}).should('exist');
        
                    cy.intercept('GET', `/api/session/${newSession.id}`, {
                    body: newSession,
                    }).as('session to update');
        
                    cy.get('mat-card-actions').find('button[mat-raised-button]').contains('Edit').click();
        
                    cy.url().should('eq', `${Cypress.config().baseUrl}sessions/update/${newSession.id}`);
                });
            });
    
    
            it('should display an error if a required field is missing', () => {
                cy.get('input[formControlName=name]').clear().type(`{enter}`);
                cy.get('input[formControlName="name"]').should('have.class', 'ng-invalid');
                cy.get('button[type="submit"]').should('be.disabled');
            });
    
            it('should update a session when required fields are entered', () => {
                cy.intercept('PUT', `/api/session/${newSession.id}`, {
                    body: updatedSession,
                }).as('update session');
    
                cy.get('input[formControlName=name]').clear().type(updatedSession.name);
                cy.get('input[formControlName=date]').clear().type(updatedSession.date.toISOString().split('T')[0]);
                cy.get('textarea[formControlName=description]').clear().type(updatedSession.description);
                cy.get('mat-select[formControlName=teacher_id]').click();
                cy.get('mat-option').contains(`${teachers[0].firstName} ${teachers[0].lastName}`).click();
        
                cy.intercept('GET', '/api/session', {
                    body: sessionsWithUpdatedSession,
                }).as('sessions');
        
                cy.get('button[type="submit"]').contains('Save').click();
                cy.get('snack-bar-container').contains('Session updated !').should('exist');
                cy.wait(3000); // wait for the snackbar to close
                cy.url().should('eq', `${Cypress.config().baseUrl}sessions`);
        
                cy.get('mat-card-title').contains(updatedSession.name, {matchCase: false}).should('exist');
            });
        });
    
        describe('session details', () => {
            before(() => {
                cy.intercept('GET', `/api/session/${updatedSession.id}`, {
                body: updatedSession,
                }).as('session');
        
                cy.get('mat-card.item').eq(updatedSession.id - 1).within(() => {
                cy.get('mat-card-title').contains(updatedSession.name, {matchCase: false}).should('exist');
        
                cy.intercept('GET', `/api/teacher/${teachers[0].id}`, {
                    body: teachers[0]
                }).as('teacher 1');
        
                cy.get('mat-card-actions').find('button[mat-raised-button]').contains('Detail').click();
                });
            });
    
            it('should display the details of a session', () => {
                cy.url().should('eq', `${Cypress.config().baseUrl}sessions/detail/${updatedSession.id}`);
        
                cy.get('h1').contains(updatedSession.name, {matchCase: false});
                cy.get('div.description').contains(updatedSession.description);
                cy.get('mat-card-subtitle').contains(teachers[0].firstName, {matchCase: false});
            });
    
            it('should display a button to delete a session', () => {
                cy.get('mat-card-title').find('button[mat-raised-button]').contains('Delete').should('exist');
            });
    
            it('should delete a session', () => {
                cy.intercept('DELETE', `/api/session/${updatedSession.id}`, {
                    statusCode: 204,
                }).as('delete session');
        
                cy.intercept('GET', '/api/session', {
                    body: sessions,
                }).as('sessions');
        
                cy.get('mat-card-title').find('button[mat-raised-button]').contains('Delete').click();
        
                cy.get('snack-bar-container').contains('Session deleted !').should('exist');
        
                cy.wait(3000); // wait for the snackbar to close
        
                cy.url().should('eq', `${Cypress.config().baseUrl}sessions`);
                cy.get('mat-card.item').should('have.length', 2);
                cy.get('mat-card-title').contains(updatedSession.name, {matchCase: false}).should('not.exist');
            });
        });
    });
});
  