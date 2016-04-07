'use strict';

describe('Controller: Oauth2Ctrl', function () {

  // load the controller's module
  beforeEach(module('mayfieldIsApp'));

  var Oauth2Ctrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    Oauth2Ctrl = $controller('Oauth2Ctrl', {
      $scope: scope
      // place here mocked dependencies
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(Oauth2Ctrl.awesomeThings.length).toBe(3);
  });
});
