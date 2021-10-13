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

    $scope.myJson = {
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
          maxItems: 8,
          transform: {
              type: 'date'
          },
          zooming: true,
          values: [
            1442905200000, 1442908800000,
            1442912400000, 1442916000000,
            1442919600000, 1442923200000,
            1442926800000, 1442930400000,
            1442934000000, 1442937600000,
            1442941200000, 1442944800000,
            1442948400000
          ],
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
          text: "All Sites",
          values: [2596, 2626, 4480,
                   6394, 7488, 14510,
                   7012, 10389, 20281,
                   25597, 23309, 22385,
                   25097, 20813.65, 20510],
          backgroundColor1: "#77d9f8",
          backgroundColor2: "#272822",
          lineColor: "#40beeb"
      }, {
          text: "Site 1",
          values: [479, 199, 583,
                   1624, 2772, 7899,
                   3467, 3227, 12885,
                   17873, 14420, 12569,
                   17721, 11569.65, 7362],
          backgroundColor1: "#4AD8CC",
          backgroundColor2: "#272822",
          lineColor: "#4AD8CC"
      }, {
          text: "Site 2",
          values: [989, 1364, 2161,
                   2644, 1754, 2015,
                   818, 77, 1260,
                   3912, 1671, 1836,
                   2589, 1706, 1161],
          backgroundColor1: "#1D8CD9",
          backgroundColor2: "#1D8CD9",
          lineColor: "#1D8CD9"
      }, {
          text: "Site 3",
          values: [408, 343, 410,
                   840, 1614, 3274,
                   2092, 914, 5709,
                   6317, 6633, 6720,
                   6504, 6821, 4565],
          backgroundColor1: "#D8CD98",
          backgroundColor2: "#272822",
          lineColor: "#D8CD98"
      }]
    };
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

