/// <reference types="Cypress" />

describe('Me', () => {
    const user = {
        id: 2,
        email: 'user@studio.com',
        firstName: 'User',
        lastName: 'Studio',
        password: 'test!1234',
        admin: false,
        createdAt: new Date(),
        updatedAt: new Date()
    }


    it('should login successfully', () => {
        cy.visit('/login');
        cy.intercept('POST', '/api/auth/login', {
        statusCode: 201,
        body: user,
        }).as('login');

        cy.intercept('GET', '/api/session', []).as('sessions');

        cy.get('input[formControlName=email]').type(user.email);
        cy.get('input[formControlName=password]').type(`${user.password}{enter}{enter}`);
        cy.url().should('eq', `${Cypress.config().baseUrl}sessions`);
    })

    it('should show account information successfully', () => {

        cy.intercept('GET', `/api/user/${user.id}`, {
        body: user
        }).as('user informations');


        cy.get('[routerlink="me"]').click();
        cy.url().should('include', '/me');
        cy.get('mat-card-content').contains(user.firstName);
    })


    it('should delete the account successfully', () => {

        cy.intercept('GET', `/api/user/${user.id}`, {
        body: user
        }).as('user informations');

        cy.intercept('GET', '/api/session', {});

        cy.intercept('DELETE', `/api/user/${user.id}`, { statusCode: 200 });

        cy.url().should('include', '/me');
        cy.get('mat-card-content').find('button[mat-raised-button]').contains('delete').click();
        cy.get('[routerlink="login"]').should('exist');
        cy.get('[routerlink="register"]').should('exist');
    })

})
