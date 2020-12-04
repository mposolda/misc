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
});

module.controller('CompanyCtrl', function($scope, Company, company, MyMath, ColorMarker) {
    console.log('CompanyCtrl executed');
    $scope.company = company;
    $scope.MyMath = MyMath;
    $scope.ColorMarker = ColorMarker;
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

    $scope.totalFees = companies.totalFeesCZK + currencies.totalFeesCZK;
});

