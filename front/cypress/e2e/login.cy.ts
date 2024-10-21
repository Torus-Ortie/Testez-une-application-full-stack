/// <reference types="Cypress" />
describe('Login spec', () => {
  const user = {
    id: 1,
    email: 'yoga@studio.com',
    firstName: 'Yoga',
    lastName: 'Studio',
    password: 'test!1234',
    admin: false,
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  beforeEach(() => {
    cy.visit('/login');
  });

  it('should login successfully', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 201,
      body: user,
    });

    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [],
    }).as('session');

    cy.get('input[formControlName=email]').type(user.email)
    cy.get('input[formControlName=password]').type(`${user.password}{enter}{enter}`)

    cy.url().should('eq', `${Cypress.config().baseUrl}sessions`);

    cy.get('.error').should('not.exist')
  })

  it('should not login with incorrect email', () => {
    cy.get('input[formControlName=email]').type("wrong@studio.com")
    cy.get('input[formControlName=password]').type(`${user.password}{enter}{enter}`)

    cy.get('.error').should('contain', 'An error occurred')
  })

  it('should not login with incorrect password', () => {
    cy.get('input[formControlName=email]').type(user.email)
    cy.get('input[formControlName=password]').type(`wrong-password{enter}{enter}`)

    cy.get('.error').should('contain', 'An error occurred')
  })

  it('should not login with missing password', () => {
    cy.get('input[formControlName=email]').type(user.email)
    cy.get('input[formControlName=password]').type(`{enter}{enter}`)

    cy.get('.error').should('contain', 'An error occurred')
  })
});
