/// <reference types="Cypress" />

describe('Sessions page', () => {
    const session = {
        id: 1,
        name: 'Hot yoga session',
        description: 'Hot yoga session description',
        date: new Date(),
        teacher_id: 1,
        createdAt: new Date(),
        updatedAt: new Date()
    }

    const teachers = [
        {
            id: 1,
            lastName: 'DELAHAYE',
            firstName: 'Margot',
            createdAt: new Date(),
            updatedAt: new Date(),
        },
    ]

    const user = {
        token: 'token',
        type: 'Bearer',
        id: 1,
        username: 'yoga_user',
        firstName: 'User',
        lastName: 'Studio',
        admin: false,
    }
  
    const loginCredentials = {
        email: 'user@studio.com',
        password: 'pass!1234',
    }

    describe('test when the user is an user', () => {
  
        before(() => {
            cy.visit('/login');
            cy.intercept('POST', '/api/auth/login', {
            statusCode: 201,
            body: user,
            }).as('login');
    
            cy.intercept('GET', '/api/session', {
                body: [session]
            }).as('sessions');
    
            cy.get('input[formControlName=email]').type(loginCredentials.email);
            cy.get('input[formControlName=password]').type(`${loginCredentials.password}{enter}{enter}`);
            cy.url().should('eq', `${Cypress.config().baseUrl}sessions`);
        });
    
        describe('session to participate', () => {
            before(() => {
                cy.intercept('GET', `/api/session/${session.id}`, {
                body: { ...session, users: [] },
                }).as('session');
        
                cy.get('mat-card.item').eq(session.id - 1).within(() => {
                    cy.get('mat-card-title').contains(session.name, {matchCase: false}).should('exist');
            
                    cy.intercept('GET', `/api/teacher/${teachers[0].id}`, {
                        body: teachers[0]
                    }).as('teacher 1');
            
                    cy.get('mat-card-actions').find('button[mat-raised-button]').contains('Detail').click();
                });
            });
    
            it('should display the details of a session', () => {
                cy.url().should('eq', `${Cypress.config().baseUrl}sessions/detail/${session.id}`);
        
                cy.get('h1').contains(session.name, {matchCase: false});
                cy.get('div.description').contains(session.description);
                cy.get('mat-card-subtitle').contains(teachers[0].firstName, {matchCase: false});
            });
    
            it('should display a button to participate to the session', () => {
                cy.get('mat-card-title').find('button[mat-raised-button]').contains('Participate').should('exist');
            });

            it('should be able to stop participate to the session', () => {
                cy.intercept('GET', '/api/session/1', {
                    body: { ...session, users: [1] }
                });

                cy.get('mat-card-title').find('button[mat-raised-button]').contains('Participate').click();
                
                cy.intercept('POST', `/api/session/${session.id}/participate/${user.id}`, {})
                cy.intercept('GET', `/api/session/${session.id}`, {
                body: { ...session, users: [] }
                });

                cy.get('mat-card-title').find('button[mat-raised-button]').contains('Participate').should('exist');
            });

        });
    });
});
  