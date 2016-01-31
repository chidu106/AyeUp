'use strict';

describe('Service: WatchListService', function () {

  // load the service's module
  beforeEach(module('stockDogApp'));

  // instantiate service
  var WatchListService;
  beforeEach(inject(function (_WatchListService_) {
    WatchListService = _WatchListService_;
  }));

  it('should do something', function () {
    expect(!!WatchListService).toBe(true);
  });

});
