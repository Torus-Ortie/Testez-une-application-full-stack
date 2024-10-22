/// <reference types="Cypress" />
describe('Logout spec', () => {

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

    before(() => {
        cy.visit('/login')
        cy.intercept('POST', '/api/auth/login', {
        statusCode: 201,
        body: user,
        }).as('login')

        cy.intercept('GET', '/api/session', []).as('sessions');

        cy.get('input[formControlName=email]').type(user.email)
        cy.get('input[formControlName=password]').type(`${user.password}{enter}{enter}`)

    })

    it('should logout successfully', () => {
        cy.get('.link').contains('Logout').click()
        cy.url().should('eq', Cypress.config().baseUrl)
        cy.get('.link').contains('Logout').should('not.exist')
    })
})
  