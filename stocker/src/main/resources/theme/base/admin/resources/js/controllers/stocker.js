module.controller('GlobalCtrl', function($scope, $http, Auth, Current, $location, Notifications) {
    console.log('GlobalCtrl executed');
    $scope.authUrl = authUrl;
    $scope.resourceUrl = resourceUrl;
    $scope.auth = Auth;

    $scope.$watch(function() {
        return $location.path();
    }, function() {
        $scope.fragment = $location.path();
        $scope.path = $location.path().substring(1).split("/");
    });

    $location.url('/companies')
});

module.controller('HomeCtrl', function(Auth, Current, $location) {
    console.log('HomeCtrl executed');
});

module.controller('CompaniesCtrl', function($scope, Companies, companies, MyMath, ColorMarker) {
    console.log('CompaniesCtrl executed');
    $scope.companies = companies;
    $scope.MyMath = MyMath;
    $scope.ColorMarker = ColorMarker;

//    ServerInfo.reload();
//
//    $scope.serverInfo = ServerInfo.get();
//
//    $scope.$watch($scope.serverInfo, function() {
//        $scope.providers = [];
//        for(var spi in $scope.serverInfo.providers) {
//            var p = angular.copy($scope.serverInfo.providers[spi]);
//            p.name = spi;
//            $scope.providers.push(p)
//        }
//    });
//
//    $scope.serverInfoReload = function() {
//        ServerInfo.reload();
//    }
});

module.controller('CurrenciesCtrl', function($scope, Currencies, currencies, MyMath, ColorMarker) {
    console.log('CurrenciesCtrl executed');
    $scope.currencies = currencies;
    $scope.MyMath = MyMath;
    $scope.ColorMarker = ColorMarker;
});

module.controller('SummaryCtrl', function($scope, Companies, companies, Currencies, currencies, MyMath, ColorMarker) {
    console.log('SummaryCtrl executed');
    $scope.companies = companies;
    $scope.currencies = currencies;
    $scope.MyMath = MyMath;
    $scope.ColorMarker = ColorMarker;
});

