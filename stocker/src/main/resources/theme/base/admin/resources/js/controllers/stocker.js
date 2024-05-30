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

module.controller('CompaniesCtrl', function($scope, Companies, companies, systemInfo, MyMath, ColorMarker) {
    console.log('CompaniesCtrl executed');
    $scope.systemInfo = systemInfo;
    $scope.companies = companies;
    $scope.MyMath = MyMath;
    $scope.ColorMarker = ColorMarker;
});

module.controller('CompanyCtrl', function($scope, Company, company, companyCandles, MyMath, ColorMarker) {
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

    var chartStockDescription = company.name + " - price (" + company.currency + ")"
    var chartTimeValues = [];
    var chartStockValues = [];
    for (var i=0 ; i<companyCandles.candles.length ; i++) {
        var currentCandle = companyCandles.candles[i];
        chartTimeValues.push(currentCandle.t * 1000);
        chartStockValues.push(currentCandle.o);
    }

    $scope.stockChartJson = {
      gui: {
        contextMenu: {
          button: {
            visible: 0
          }
        }
      },
      backgroundColor: "#434343",
      globals: {
          shadow: false,
          fontFamily: "Helvetica"
      },
      type: "area",

      legend: {
          layout: "x4",
          backgroundColor: "transparent",
          borderColor: "transparent",
          marker: {
              borderRadius: "50px",
              borderColor: "transparent"
          },
          item: {
              fontColor: "white"
          }

      },
      scaleX: {
          maxItems: 5,
          transform: {
              type: 'date'
          },
          zooming: true,
          values: chartTimeValues,
          lineColor: "white",
          lineWidth: "1px",
          tick: {
              lineColor: "white",
              lineWidth: "1px"
          },
          item: {
              fontColor: "white"
          },
          guide: {
              visible: false
          }
      },
      scaleY: {
          lineColor: "white",
          lineWidth: "1px",
          tick: {
              lineColor: "white",
              lineWidth: "1px"
          },
          guide: {
              lineStyle: "solid",
              lineColor: "#626262"
          },
          item: {
              fontColor: "white"
          },
      },
      tooltip: {
          visible: false
      },
      crosshairX: {
          scaleLabel: {
              backgroundColor: "#fff",
              fontColor: "black"
          },
          plotLabel: {
              backgroundColor: "#434343",
              fontColor: "#FFF",
              _text: "Number of hits : %v"
          }
      },
      plot: {
          lineWidth: "2px",
          aspect: "spline",
          marker: {
              visible: false
          }
      },
      series: [{
          text: chartStockDescription,
          values: chartStockValues,
          backgroundColor1: "#77d9f8",
          backgroundColor2: "#272822",
          lineColor: "#40beeb"
      }]
    };
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


module.controller('SummaryCtrl', function($scope, Companies, companies, Currencies, currencies, rateOfReturns, czkCurrency, MyMath, ColorMarker) {
    console.log('SummaryCtrl executed');
    $scope.companies = companies;
    $scope.currencies = currencies;
    $scope.rateOfReturns = rateOfReturns;
    $scope.czkCurrency = czkCurrency;
    $scope.MyMath = MyMath;
    $scope.ColorMarker = ColorMarker;

    $scope.totalFees = companies.totalFeesCZK + currencies.totalFeesCZK;
});

