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

    $scope.dividends = [];
    for (var i=0 ; i<company.dividendsSumPerYear.length ; i++) {
        var currentDivYearSum = company.dividendsSumPerYear[i];
        currentDivYearSum.yearSum = true;
        currentDivYearSum.collapsed = true;
        currentDivYearSum.visible = true;

        for (var j=0 ; j<currentDivYearSum.dividendsOfYear.length ; j++) {
            var currentDiv = currentDivYearSum.dividendsOfYear[j];
            currentDiv.visible = false;
            currentDiv.year = currentDivYearSum.year;
            currentDiv.yearSum = false;
            $scope.dividends.push(currentDiv);
        }

        $scope.dividends.push(currentDivYearSum);
    }

    var changeVisibility = function(year, visible) {
        for (var i=0 ; i<$scope.dividends.length ; i++) {
            var curr = $scope.dividends[i];
            if (!curr.yearSum && curr.year === year) {
                curr.visible = visible;
            }
        }
    }

    $scope.changeCollapseStatusOfYear = function(year) {
       console.log("changeCollapseStatusOfYear: " + year);

       for (var i=0 ; i<$scope.dividends.length ; i++) {
           var curr = $scope.dividends[i];
           if (curr.yearSum && curr.year === year) {
               if (curr.collapsed) {
                   console.log("Uncollapsing year: " + year);
                   curr.collapsed = false;
                   changeVisibility(year, true);
               } else {
                   console.log("Collapsing year: " + year);
                   curr.collapsed = true;
                   changeVisibility(year, false);
               }
           }
       }
    }
});

module.controller('CurrenciesCtrl', function($scope, Currencies, currencies, MyMath, ColorMarker) {
    console.log('CurrenciesCtrl executed');
    $scope.currencies = currencies;
    $scope.MyMath = MyMath;
    $scope.ColorMarker = ColorMarker;
});

module.controller('TransactionsCtrl', function($scope, transactions, MyMath, ColorMarker) {
    console.log('TransactionsCtrl executed');
    $scope.transactions = transactions;
    $scope.MyMath = MyMath;
    $scope.ColorMarker = ColorMarker;
});

module.controller('DividendsAllSumCtrl', function($scope, dividendsAllSum, MyMath, ColorMarker) {
    console.log('DividendsAllSumCtrl executed');
    $scope.dividendsAllSum = dividendsAllSum;
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

