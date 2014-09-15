var module = angular.module('module1', ['ngResource', 'ngRoute']);

angular.element(document).ready(function ($http) {
    console.log("Going to bootstrap module1");
    angular.bootstrap(document, ["module1"]);
});

module.factory('SimpleUser', function($resource) {
    return $resource('/simple-rest-angular/rest/base?sleep=:sleep');
});

module.config([ '$routeProvider', function($routeProvider) {

    $routeProvider
        .when('/base1', {
            templateUrl : 'user.html',
            resolve : {

            },
            controller : 'SimpleUserController1'
        })
        .when('/base2', {
            templateUrl : 'user.html',
            resolve : {

            },
            controller : 'SimpleUserController2'
        })
        .otherwise({
            templateUrl : 'notfound.html'
        });
    }
]);

