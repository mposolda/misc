var module = angular.module('module1', ['ngResource', 'ngRoute']);

angular.element(document).ready(function ($http) {
    console.log("Going to bootstrap module1");
    angular.bootstrap(document, ["module1"]);
});

module.factory('SimpleUser', function($resource) {
    return $resource('/simple-rest-angular/rest/base?sleep=:sleep');
});

module.factory('SimpleUserLoader', function(SimpleUser, $q) {
    return function() {
        var def = $q.defer();
        var user = SimpleUser.get( { sleep: 5000 }, function() {
             console.log("User loaded successfully! Fulfilling promise");
             def.resolve(user);
        }, function(error) {
             console.log("User not loaded. Promise rejected");
             def.reject("User not loaded. Promise rejected");
        });
        return def.promise;
    }
});

module.config([ '$routeProvider', function($routeProvider) {

    $routeProvider
        .when('/base1', {
            templateUrl : 'user.html',
            resolve : {

            },
            controller : 'SimpleUserController1'
        })
        // The way used by our loaders
        .when('/base2', {
            templateUrl : 'user.html',
            resolve : {
                loadedUser : function(SimpleUserLoader) {
                    return SimpleUserLoader();
                },
            },
            controller : 'SimpleUserController2'
        })
        // The way used by AngularJS (builtin $promise object)
        .when('/base3', {
            templateUrl : 'user.html',
            resolve : {
                loadedUser : function(SimpleUser) {
                    var user = SimpleUser.get({ sleep:3000 });
                    console.log("Base3 - SimpleUser.get() invoked");
                    return user.$promise;
                },
            },
            controller : 'SimpleUserController2'
        })
        .otherwise({
            templateUrl : 'notfound.html'
        });
    }
]);

