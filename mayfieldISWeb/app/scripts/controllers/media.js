'use strict';

/**
 * @ngdoc function
 * @name mayfieldIsApp.controller:Oauth2Ctrl
 * @description
 * # Oauth2Ctrl
 * Controller of the mayfieldIsApp
 */
angular.module('mayfieldIsApp')
  .controller('MediaCtrl', ['$scope', '$sce',function ($scope,$sce) {
    this.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
    
    $scope.trustSrc = function(src) {
    return $sce.trustAsResourceUrl(src);
  }

  $scope.movie = {src:"https://player.vimeo.com/video/146233858", title:"FHIR OAuth2"};
    
  }]);
