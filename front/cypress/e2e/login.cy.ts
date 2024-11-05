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

describe('Register spec', () => {
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
      cy.visit('/register');
  });

  it('should indicate an error if required fields are missing', () => {
      cy.get('input[formControlName=firstName]').type(user.firstName)
      cy.get('input[formControlName=lastName]').type(user.lastName)
      cy.get('input[formControlName="email"]').type(`{enter}`);
      cy.get('input[formControlName=password]').type(user.password)
      cy.get('input[formControlName="email"]').should('have.class', 'ng-invalid');
  
      cy.get('button[type=submit]').should('be.disabled')
  })

  it('should register, login, and check user account details then delete it', () => {
      cy.intercept('POST', '/api/auth/register', {
          statusCode: 201,
          body: user,
      }).as('register')
  
      cy.get('input[formControlName=firstName]').type(user.firstName)
      cy.get('input[formControlName=lastName]').type(user.lastName)
      cy.get('input[formControlName=email]').type(user.email)
      cy.get('input[formControlName=password]').type(user.password)
  
      cy.get('button[type=submit]').click()
  
      cy.url().should('include', '/login')
  
      cy.intercept('POST', '/api/auth/login', {
          statusCode: 201,
          body: user,
      }).as('login')
  
      cy.intercept('GET', '/api/session', {
          statusCode: 200,
          body: user,
      }).as('getSession')
  
      cy.get('input[formControlName=email]').type(user.email)
      cy.get('input[formControlName=password]').type(`${user.password}{enter}{enter}`)
  
      cy.intercept('GET', `/api/user/${user.id}`, {
          statusCode: 200,
          body: user,
      }).as('getUser')
  
      cy.get('span[routerLink=me]').click();
  
      cy.url().should('include', '/me');
  
      cy.get('.m3 mat-card-content p').contains(`Name: ${user.firstName} ${user.lastName.toUpperCase()}`).should('exist');
      cy.get('.m3 mat-card-content p').contains(`Email: ${user.email}`).should('exist');
      cy.get('.m3 mat-card-content div.my2').should('exist');

      cy.intercept('DELETE', `/api/user/${user.id}`, {
          statusCode: 200,
      }).as('deleteUser')
  
      cy.get('.my2 > .mat-focus-indicator').click();
      cy.wait('@deleteUser').its('response.statusCode').should('eq', 200);
      cy.url().should('eq', 'http://localhost:4200/');
  });

  it('should show an error if email is already used', () => {
      cy.intercept('POST', '/api/auth/register', { statusCode: 400 })
  
      cy.get('input[formControlName=firstName]').type("firstname")
      cy.get('input[formControlName=lastName]').type("lastname")
      cy.get('input[formControlName=email]').type("yoga@studio.com")
      cy.get('input[formControlName=password]').type(`${"test!123"}{enter}{enter}`)
      cy.get('.error').should('be.visible');
  })

})

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

describe('Sessions to participate page', () => {
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
