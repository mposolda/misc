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


module.factory('CompaniesLoader', function(Loader, Companies, $route) {
    console.log("CompaniesLoader executed");

    return Loader.get(Companies, function() {
        return {
        }
    });
});

module.factory('CompanyLoader', function(Loader, Company, $route) {
    console.log("CompanyLoader executed");

    return Loader.get(Company, function() {
        return {
            ticker : $route.current.params.ticker
        }
    });
});

module.factory('CompanyCandlesLoader', function(Loader, CompanyCandles, $route) {
    console.log("CompanyCandlesLoader executed");
    return Loader.get(CompanyCandles, function() {
        return {
            ticker : $route.current.params.ticker
        }
    });
});

module.factory('CurrenciesLoader', function(Loader, Currencies, $route) {
    console.log("CurrenciesLoader executed");
    return Loader.get(Currencies, function() {
        return {
        }
    });
});

module.factory('TransactionsLoader', function(Loader, Transactions, $route) {
    console.log("TransactionsLoader executed");
    return Loader.get(Transactions, function() {
        return {
        }
    });
});

module.factory('DividendsAllSumLoader', function(Loader, DividendsAllSum, $route) {
    console.log("DividendsAllSumLoader executed");
    return Loader.get(DividendsAllSum, function() {
        return {
        }
    });
});
