'use strict';

/**
 * @ngdoc function
 * @name mayfieldIsApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the mayfieldIsApp
 */
angular.module('mayfieldIsApp')
  .controller('MainCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
    $scope.slides = [
            {
                    image: 'images/AmsterdamAPI.png', 
                    description: 'IT Community', 
                    text1:'Sharing ideas and best practices.',
                    text2:'Involved with HL7 and Code4Health.'
               },
            {
                image: 'images/BPMNeReferral.png', 
                description: 'Workflow/Business Process', 
                text1:'Increasing productivity, efficiency and.',
                text2:'reducing costs.',
                text3:''},
            {
                image: 'images/openSource.png', 
                description: 'Open Source', 
                text1:'Using industry proven technology.',
                text2:'Supporting Code4Health and NHS England',
                text3:'Open Source initiatives.'
            },
            {
                image: 'images/TIE.png', 
                description: 'Trust Integration Engine', 
                text1:'Ensuring information is shared across the ',
                text2:'health and social care enterprise.',
                text3:'Extensive experience of HL7'}
        ];
    $scope.currentIndex = 0;

    $scope.setCurrentSlideIndex = function (index) {
        $scope.currentIndex = index;
    };

    $scope.isCurrentSlideIndex = function (index) {
        return $scope.currentIndex === index;
    };
    $scope.prevSlide = function () {
            $scope.currentIndex = ($scope.currentIndex < $scope.slides.length - 1) ? ++$scope.currentIndex : 0;
        };

    $scope.nextSlide = function () {
        $scope.currentIndex = ($scope.currentIndex > 0) ? --$scope.currentIndex : $scope.slides.length - 1;
    };
    
    $scope.disabled=false;
    
    setInterval(function() {
        if ($scope.disabled)
        {
            return;
        }
        //console.log("Timer");
        $scope.nextSlide();
        $scope.$apply();
    },8000);
    
  })
  .animation('.slide-animation', function () {
        return {
            addClass: function (element, className, done) {
                if (className == 'ng-hide') {
                    TweenMax.to(element, 0.5, {left: -element.parent().width(), onComplete: done });
                }
                else {
                    done();
                }
            },
            removeClass: function (element, className, done) {
                if (className == 'ng-hide') {
                    element.removeClass('ng-hide');

                    TweenMax.set(element, { left: element.parent().width() });
                    TweenMax.to(element, 0.5, {left: 0, onComplete: done });
                }
                else {
                    done();
                }
            }
        };
    });;
