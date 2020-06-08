'use strict';

var module = angular.module('keycloak.loaders', [ 'keycloak.services', 'ngResource' ]);

module.factory('Loader', function($q) {
	var loader = {};
	loader.get = function(service, id) {
		return function() {
			var i = id && id();
			var delay = $q.defer();
			service.get(i, function(entry) {
				delay.resolve(entry);
			}, function() {
				delay.reject('Unable to fetch ' + i);
			});
			return delay.promise;
		};
	};
	loader.query = function(service, id) {
		return function() {
			var i = id && id();
			var delay = $q.defer();
			service.query(i, function(entry) {
				delay.resolve(entry);
			}, function() {
				delay.reject('Unable to fetch ' + i);
			});
			return delay.promise;
		};
	};
	return loader;
});


module.factory('CompaniesLoader', function(Loader, Companies, $route, $q) {
    console.log("CompaniesLoader executed");

    return Loader.get(Companies, function() {
        return {
        }
    });
});

module.factory('CurrenciesLoader', function(Loader, Currencies, $route, $q) {
    console.log("CurrenciesLoader executed");
    return Loader.get(Currencies, function() {
        return {
        }
    });
});
