'use strict';

var auth = {};
var resourceBundle;

var module = angular.module('keycloak', [ 'keycloak.services', 'keycloak.loaders', 'ui.bootstrap', 'ui.select2', 'angularFileUpload', 'angularTreeview', 'pascalprecht.translate', 'ngCookies', 'ngSanitize', 'ui.ace']);
var resourceRequests = 0;
var loadingTimer = -1;

angular.element(document).ready(function () {

    function loadResourceBundle(success, error) {
        var req = new XMLHttpRequest();
        req.open('GET', consoleBaseUrl + 'messages.json?lang=' + locale, true);
        req.setRequestHeader('Accept', 'application/json');

        req.onreadystatechange = function () {
            if (req.readyState == 4) {
                if (req.status == 200) {
                    var data = JSON.parse(req.responseText);
                    success && success(data);
                } else {
                    error && error();
                }
            }
        }

        req.send();
    }



    loadResourceBundle(function(data) {
        resourceBundle = data;

        module.factory('Auth', function () {
            return auth;
        });
        module.factory('currentLocale', function () {
            return locale;
        });
        var injector = angular.bootstrap(document, ["keycloak"]);

        injector.get('$translate')('consoleTitle').then(function (consoleTitle) {
            document.title = consoleTitle;
        });
    });
});

module.config(['$translateProvider', function($translateProvider) {
    $translateProvider.useSanitizeValueStrategy('sanitizeParameters');
    $translateProvider.preferredLanguage(locale);
    $translateProvider.translations(locale, resourceBundle);
}]);

// Change for upgrade to AngularJS 1.6
// See https://github.com/angular/angular.js/commit/aa077e81129c740041438688dff2e8d20c3d7b52
module.config(['$locationProvider', function($locationProvider) {
  $locationProvider.hashPrefix('');
}]);

module.config([ '$routeProvider', function($routeProvider) {
    $routeProvider
        .when('/companies', {
            templateUrl : resourceUrl + '/partials/companies.html',
            resolve : {
                companies : function(CompaniesLoader) {
                    return CompaniesLoader();
                }
            },
            controller : 'CompaniesCtrl'
        })
        .when('/companies/:ticker', {
            templateUrl : resourceUrl + '/partials/company.html',
            resolve : {
                company : function(CompanyLoader) {
                    return CompanyLoader();
                }
            },
            controller : 'CompanyCtrl'
        })
        .when('/currencies', {
            templateUrl : resourceUrl + '/partials/currencies.html',
            resolve : {
                currencies : function(CurrenciesLoader) {
                    return CurrenciesLoader();
                }
            },
            controller : 'CurrenciesCtrl'
        })
        .when('/summary', {
            templateUrl : resourceUrl + '/partials/summary.html',
            resolve : {
                companies : function(CompaniesLoader) {
                    return CompaniesLoader();
                },
                currencies : function(CurrenciesLoader) {
                    return CurrenciesLoader();
                }
            },
            controller : 'SummaryCtrl'
        })
        .when('/transactions', {
            templateUrl : resourceUrl + '/partials/transactions.html',
            resolve : {
                transactions : function(TransactionsLoader) {
                    return TransactionsLoader();
                }
            },
            controller : 'TransactionsCtrl'
        })
        .when('/logout', {
            templateUrl : resourceUrl + '/partials/home.html',
            controller : 'LogoutCtrl'
        })
        .when('/notfound', {
            templateUrl : resourceUrl + '/partials/notfound.html'
        })
        .when('/forbidden', {
            templateUrl : resourceUrl + '/partials/forbidden.html'
        })
        .otherwise({
            templateUrl : resourceUrl + '/partials/pagenotfound.html'
        });
} ]);

module.config(function($httpProvider) {
    $httpProvider.interceptors.push('errorInterceptor');

    var spinnerFunction = function(data, headersGetter) {
        if (resourceRequests == 0) {
            loadingTimer = window.setTimeout(function() {
                $('#loading').show();
                loadingTimer = -1;
            }, 500);
        }
        resourceRequests++;
        return data;
    };
    $httpProvider.defaults.transformRequest.push(spinnerFunction);

    $httpProvider.interceptors.push('spinnerInterceptor');

});

module.factory('spinnerInterceptor', function($q, $window, $rootScope, $location) {
    return {
        response: function(response) {
            resourceRequests--;
            if (resourceRequests == 0) {
                if(loadingTimer != -1) {
                    window.clearTimeout(loadingTimer);
                    loadingTimer = -1;
                }
                $('#loading').hide();
            }
            return response;
        },
        responseError: function(response) {
            resourceRequests--;
            if (resourceRequests == 0) {
                if(loadingTimer != -1) {
                    window.clearTimeout(loadingTimer);
                    loadingTimer = -1;
                }
                $('#loading').hide();
            }

            return $q.reject(response);
        }
    };
});

module.factory('errorInterceptor', function($q, $window, $rootScope, $location, Notifications, Auth) {
    return {
        response: function(response) {
            return response;
        },
        responseError: function(response) {
            if (response.status == 401) {
                Auth.authz.logout();
            } else if (response.status == 403) {
                $location.path('/forbidden');
            } else if (response.status == 404) {
                $location.path('/notfound');
            } else if (response.status) {
                if (response.data && response.data.errorMessage) {
                    Notifications.error(response.data.errorMessage);
                } else if (response.data && response.data.error_description) {
                    Notifications.error(response.data.error_description);
                } else {
                    Notifications.error("An unexpected server error has occurred");
                }
            } else {
                Notifications.error("No response from server.");
            }
            return $q.reject(response);
        }
    };
});

// collapsable form fieldsets
module.directive('collapsable', function() {
    return function(scope, element, attrs) {
        element.click(function() {
            $(this).toggleClass('collapsed');
            $(this).find('.toggle-icons').toggleClass('kc-icon-collapse').toggleClass('kc-icon-expand');
            $(this).find('.toggle-icons').text($(this).text() == "Icon: expand" ? "Icon: collapse" : "Icon: expand");
            $(this).parent().find('.form-group').toggleClass('hidden');
        });
    }
});

// collapsable form fieldsets
module.directive('uncollapsed', function() {
    return function(scope, element, attrs) {
        element.prepend('<i class="toggle-class fa fa-angle-down"></i> ');
        element.click(function() {
            $(this).find('.toggle-class').toggleClass('fa-angle-down').toggleClass('fa-angle-right');
            $(this).parent().find('.form-group').toggleClass('hidden');
        });
    }
});

// collapsable form fieldsets
module.directive('collapsed', function() {
    return function(scope, element, attrs) {
        element.prepend('<i class="toggle-class fa fa-angle-right"></i> ');
        element.parent().find('.form-group').toggleClass('hidden');
        element.click(function() {
            $(this).find('.toggle-class').toggleClass('fa-angle-down').toggleClass('fa-angle-right');
            $(this).parent().find('.form-group').toggleClass('hidden');
        });
    }
});

/**
 * Directive for presenting an ON-OFF switch for checkbox.
 * Usage: <input ng-model="mmm" name="nnn" id="iii" onoffswitch [on-text="ooo" off-text="fff"] />
 */
module.directive('onoffswitch', function() {
    return {
        restrict: "EA",
        replace: true,
        scope: {
            name: '@',
            id: '@',
            ngModel: '=',
            ngDisabled: '=',
            kcOnText: '@onText',
            kcOffText: '@offText'
        },
        // TODO - The same code acts differently when put into the templateURL. Find why and move the code there.
        //templateUrl: "templates/kc-switch.html",
        template: "<span><div class='onoffswitch' tabindex='0'><input type='checkbox' ng-model='ngModel' ng-disabled='ngDisabled' class='onoffswitch-checkbox' name='{{name}}' id='{{id}}'><label for='{{id}}' class='onoffswitch-label'><span class='onoffswitch-inner'><span class='onoffswitch-active'>{{kcOnText}}</span><span class='onoffswitch-inactive'>{{kcOffText}}</span></span><span class='onoffswitch-switch'></span></label></div></span>",
        compile: function(element, attrs) {
            /*
            We don't want to propagate basic attributes to the root element of directive. Id should be passed to the
            input element only to achieve proper label binding (and validity).
            */
            element.removeAttr('name');
            element.removeAttr('id');

            if (!attrs.onText) { attrs.onText = "ON"; }
            if (!attrs.offText) { attrs.offText = "OFF"; }

            element.bind('keydown', function(e){
                var code = e.keyCode || e.which;
                if (code === 32 || code === 13) {
                    e.stopImmediatePropagation();
                    e.preventDefault();
                    $(e.target).find('input').click();
                }
            });
        }
    }
});

/**
 * Directive for presenting an ON-OFF switch for checkbox. The directive expects the value to be string 'true' or 'false', not boolean true/false
 * This directive provides some additional capabilities to the default onoffswitch such as:
 *
 * - Dynamic values for id and name attributes. Useful if you need to use this directive inside a ng-repeat
 * - Specific scope to specify the value. Instead of just true or false.
 *
 * Usage: <input ng-model="mmm" name="nnn" id="iii" kc-onoffswitch-model [on-text="ooo" off-text="fff"] />
 */
module.directive('onoffswitchstring', function() {
    return {
        restrict: "EA",
        replace: true,
        scope: {
            name: '=',
            id: '=',
            value: '=',
            ngModel: '=',
            ngDisabled: '=',
            kcOnText: '@onText',
            kcOffText: '@offText'
        },
        // TODO - The same code acts differently when put into the templateURL. Find why and move the code there.
        //templateUrl: "templates/kc-switch.html",
        template: '<span><div class="onoffswitch" tabindex="0"><input type="checkbox" ng-true-value="\'true\'" ng-false-value="\'false\'" ng-model="ngModel" ng-disabled="ngDisabled" class="onoffswitch-checkbox" name="kc{{name}}" id="kc{{id}}"><label for="kc{{id}}" class="onoffswitch-label"><span class="onoffswitch-inner"><span class="onoffswitch-active">{{kcOnText}}</span><span class="onoffswitch-inactive">{{kcOffText}}</span></span><span class="onoffswitch-switch"></span></label></div></span>',
        compile: function(element, attrs) {

            if (!attrs.onText) { attrs.onText = "ON"; }
            if (!attrs.offText) { attrs.offText = "OFF"; }

            element.bind('keydown click', function(e){
                var code = e.keyCode || e.which;
                if (code === 32 || code === 13) {
                    e.stopImmediatePropagation();
                    e.preventDefault();
                    $(e.target).find('input').click();
                }
            });
        }
    }
});

/**
 * Directive for presenting an ON-OFF switch for checkbox. The directive expects the true-value or false-value to be string like 'true' or 'false', not boolean true/false.
 * This directive provides some additional capabilities to the default onoffswitch such as:
 *
 * - Specific scope to specify the value. Instead of just 'true' or 'false' you can use any other values. For example: true-value="'foo'" false-value="'bar'" .
 * But 'true'/'false' are defaults if true-value and false-value are not specified
 *
 * Usage: <input ng-model="mmm" name="nnn" id="iii" onoffswitchvalue [ true-value="'true'" false-value="'false'" on-text="ooo" off-text="fff"] />
 */
module.directive('onoffswitchvalue', function() {
    return {
        restrict: "EA",
        replace: true,
        scope: {
            name: '@',
            id: '@',
            trueValue: '@',
            falseValue: '@',
            ngModel: '=',
            ngDisabled: '=',
            kcOnText: '@onText',
            kcOffText: '@offText'
        },
        // TODO - The same code acts differently when put into the templateURL. Find why and move the code there.
        //templateUrl: "templates/kc-switch.html",
        template: "<span><div class='onoffswitch' tabindex='0'><input type='checkbox' ng-true-value='{{trueValue}}' ng-false-value='{{falseValue}}' ng-model='ngModel' ng-disabled='ngDisabled' class='onoffswitch-checkbox' name='{{name}}' id='{{id}}'><label for='{{id}}' class='onoffswitch-label'><span class='onoffswitch-inner'><span class='onoffswitch-active'>{{kcOnText}}</span><span class='onoffswitch-inactive'>{{kcOffText}}</span></span><span class='onoffswitch-switch'></span></label></div></span>",
        compile: function(element, attrs) {
            /*
             We don't want to propagate basic attributes to the root element of directive. Id should be passed to the
             input element only to achieve proper label binding (and validity).
             */
            element.removeAttr('name');
            element.removeAttr('id');

            if (!attrs.trueValue) { attrs.trueValue = "'true'"; }
            if (!attrs.falseValue) { attrs.falseValue = "'false'"; }

            if (!attrs.onText) { attrs.onText = "ON"; }
            if (!attrs.offText) { attrs.offText = "OFF"; }

            element.bind('keydown', function(e){
                var code = e.keyCode || e.which;
                if (code === 32 || code === 13) {
                    e.stopImmediatePropagation();
                    e.preventDefault();
                    $(e.target).find('input').click();
                }
            });
        }
    }
});

module.directive('kcInput', function() {
    var d = {
        scope : true,
        replace : false,
        link : function(scope, element, attrs) {
            var form = element.children('form');
            var label = element.children('label');
            var input = element.children('input');

            var id = form.attr('name') + '.' + input.attr('name');

            element.attr('class', 'control-group');

            label.attr('class', 'control-label');
            label.attr('for', id);

            input.wrap('<div class="controls"/>');
            input.attr('id', id);

            if (!input.attr('placeHolder')) {
                input.attr('placeHolder', label.text());
            }

            if (input.attr('required')) {
                label.append(' <span class="required">*</span>');
            }
        }
    };
    return d;
});

module.directive('kcEnter', function() {
    return function(scope, element, attrs) {
        element.bind("keydown keypress", function(event) {
            if (event.which === 13) {
                scope.$apply(function() {
                    scope.$eval(attrs.kcEnter);
                });

                event.preventDefault();
            }
        });
    };
});

// Don't allow URI reserved characters
module.directive('kcNoReservedChars', function (Notifications, $translate) {
    return function($scope, element) {
        element.bind("keypress", function(event) {
            var keyPressed = String.fromCharCode(event.which || event.keyCode || 0);
            
            // ] and ' can not be used inside a character set on POSIX and GNU
            if (keyPressed.match('[:/?#[@!$&()*+,;=]') || keyPressed === ']' || keyPressed === '\'') {
                event.preventDefault();
                $scope.$apply(function() {
                    Notifications.warn($translate.instant('key-not-allowed-here', {character: keyPressed}));
                });
            }
        });
    };
});

module.directive('kcSave', function ($compile, $timeout, Notifications) {
    var clickDelay = 500; // 500 ms

    return {
        restrict: 'A',
        link: function ($scope, elem, attr, ctrl) {
            elem.addClass("btn btn-primary");
            elem.attr("type","submit");

            var disabled = false;
            elem.on('click', function(evt) {
                if ($scope.hasOwnProperty("changed") && !$scope.changed) return;

                // KEYCLOAK-4121: Prevent double form submission
                if (disabled) {
                    evt.preventDefault();
                    evt.stopImmediatePropagation();
                    return;
                } else {
                    disabled = true;
                    $timeout(function () { disabled = false; }, clickDelay, false);
                }

                $scope.$apply(function() {
                    var form = elem.closest('form');
                    if (form && form.attr('name')) {
                        var ngValid = form.find('.ng-valid');
                        if ($scope[form.attr('name')].$valid) {
                            //ngValid.removeClass('error');
                            ngValid.parent().removeClass('has-error');
                            $scope['save']();
                        } else {
                            Notifications.error("Missing or invalid field(s). Please verify the fields in red.")
                            //ngValid.removeClass('error');
                            ngValid.parent().removeClass('has-error');

                            var ngInvalid = form.find('.ng-invalid');
                            //ngInvalid.addClass('error');
                            ngInvalid.parent().addClass('has-error');
                        }
                    }
                });
            })
        }
    }
});

module.directive('kcReset', function ($compile, Notifications) {
    return {
        restrict: 'A',
        link: function ($scope, elem, attr, ctrl) {
            elem.addClass("btn btn-default");
            elem.attr("type","submit");
            elem.bind('click', function() {
                $scope.$apply(function() {
                    var form = elem.closest('form');
                    if (form && form.attr('name')) {
                        form.find('.ng-valid').removeClass('error');
                        form.find('.ng-invalid').removeClass('error');
                        $scope['reset']();
                    }
                })
            })
        }
    }
});

module.directive('kcCancel', function ($compile, Notifications) {
    return {
        restrict: 'A',
        link: function ($scope, elem, attr, ctrl) {
            elem.addClass("btn btn-default");
            elem.attr("type","submit");
        }
    }
});

module.directive('kcDelete', function ($compile, Notifications) {
    return {
        restrict: 'A',
        link: function ($scope, elem, attr, ctrl) {
            elem.addClass("btn btn-danger");
            elem.attr("type","submit");
        }
    }
});


module.directive('kcDropdown', function ($compile, Notifications) {
    return {
        scope: {
            kcOptions: '=',
            kcModel: '=',
            id: "=",
            kcPlaceholder: '@'
        },
        restrict: 'EA',
        replace: true,
        templateUrl: resourceUrl + '/templates/kc-select.html',
        link: function(scope, element, attr) {
            scope.updateModel = function(item) {
                scope.kcModel = item;
            };
        }
    }
});

module.directive('kcReadOnly', function() {
    var disabled = {};

    var d = {
        replace : false,
        link : function(scope, element, attrs) {
            var disable = function(i, e) {
                if (!e.disabled) {
                    disabled[e.tagName + i] = true;
                    e.disabled = true;
                }
            }

            var enable = function(i, e) {
                if (disabled[e.tagName + i]) {
                    e.disabled = false;
                    delete disabled[i];
                }
            }

            var filterIgnored = function(i, e){
                return !e.attributes['kc-read-only-ignore'];
            }

            scope.$watch(attrs.kcReadOnly, function(readOnly) {
                if (readOnly) {
                    element.find('input').filter(filterIgnored).each(disable);
                    element.find('button').filter(filterIgnored).each(disable);
                    element.find('select').filter(filterIgnored).each(disable);
                    element.find('textarea').filter(filterIgnored).each(disable);
                } else {
                    element.find('input').filter(filterIgnored).each(enable);
                    element.find('input').filter(filterIgnored).each(enable);
                    element.find('button').filter(filterIgnored).each(enable);
                    element.find('select').filter(filterIgnored).each(enable);
                    element.find('textarea').filter(filterIgnored).each(enable);
                }
            });
        }
    };
    return d;
});

module.directive('kcMenu', function () {
    return {
        scope: true,
        restrict: 'E',
        replace: true,
        templateUrl: resourceUrl + '/templates/kc-menu.html'
    }
});

module.directive('kcTabsCompanies', function () {
    return {
        scope: true,
        restrict: 'E',
        replace: true,
        templateUrl: resourceUrl + '/templates/kc-tabs-companies.html'
    }
});

module.directive('kcNavigationUser', function () {
    return {
        scope: true,
        restrict: 'E',
        replace: true,
        templateUrl: resourceUrl + '/templates/kc-navigation-user.html'
    }
});

/*
*  Used to select the element (invoke $(elem).select()) on specified action list.
*  Usages kc-select-action="click mouseover"
*  When used in the textarea element, this will select/highlight the textarea content on specified action (i.e. click).
*/
module.directive('kcSelectAction', function ($compile, Notifications) {
    return {
        restrict: 'A',
        compile: function (elem, attrs) {

            var events = attrs.kcSelectAction.split(" ");

            for(var i=0; i < events.length; i++){

                elem.bind(events[i], function(){
                    elem.select();
                });
            }
        }
    }
});

module.filter('remove', function() {
    return function(input, remove, attribute) {
        if (!input || !remove) {
            return input;
        }

        var out = [];
        for ( var i = 0; i < input.length; i++) {
            var e = input[i];

            if (Array.isArray(remove)) {
                for (var j = 0; j < remove.length; j++) {
                    if (attribute) {
                        if (remove[j][attribute] == e[attribute]) {
                            e = null;
                            break;
                        }
                    } else {
                        if (remove[j] == e) {
                            e = null;
                            break;
                        }
                    }
                }
            } else {
                if (attribute) {
                    if (remove[attribute] == e[attribute]) {
                        e = null;
                    }
                } else {
                    if (remove == e) {
                        e = null;
                    }
                }
            }

            if (e != null) {
                out.push(e);
            }
        }

        return out;
    };
});

module.filter('capitalize', function() {
    return function(input) {
        if (!input) {
            return;
        }
        var splittedWords = input.split(/\s+/);
        for (var i=0; i<splittedWords.length ; i++) {
            splittedWords[i] = splittedWords[i].charAt(0).toUpperCase() + splittedWords[i].slice(1);
        };
        return splittedWords.join(" ");
    };
});

/*
 * Guarantees a deterministic property iteration order.
 * See: http://www.2ality.com/2015/10/property-traversal-order-es6.html
 */
module.filter('toOrderedMapSortedByKey', function(){
   return function(input){

       if(!input){
           return input;
       }

       var keys = Object.keys(input);

       if(keys.length <= 1){
           return input;
       }

       keys.sort();

       var result = {};
       for (var i = 0; i < keys.length; i++) {
           result[keys[i]] = input[keys[i]];
       }

       return result;
   };
});

module.directive('kcSidebarResize', function ($window) {
    return function (scope, element) {
        function resize() {
            var navBar = angular.element(document.getElementsByClassName('navbar-pf')).height();
            var container = angular.element(document.getElementById("view").getElementsByTagName("div")[0]).height();
            var height = Math.max(container, window.innerHeight - navBar - 3);

            element[0].style['min-height'] = height + 'px';
        }

        resize();

        var w = angular.element($window);
        scope.$watch(function () {
            return {
                'h': window.innerHeight,
                'w': window.innerWidth
            };
        }, function () {
            resize();
        }, true);
        w.bind('resize', function () {
            scope.$apply();
        });
    }
});



module.directive('kcTooltip', function($compile) {
        return {
            restrict: 'E',
            replace: false,
            terminal: true,
            priority: 1000,
            link: function link(scope,element, attrs) {
                var angularElement = angular.element(element[0]);
                var tooltip = angularElement.text();
                angularElement.text('');
                element.addClass('hidden');

                var label = angular.element(element.parent().children()[0]);
                label.append(' <i class="fa fa-question-circle text-muted" tooltip="' + tooltip + '" tooltip-placement="right" tooltip-trigger="mouseover mouseout"></i>');

                $compile(label)(scope);
            }
        };
});

module.directive( 'kcOpen', function ( $location ) {
    return function ( scope, element, attrs ) {
        var path;

        attrs.$observe( 'kcOpen', function (val) {
            path = val;
        });

        element.bind( 'click', function () {
            scope.$apply( function () {
                $location.path(path);
            });
        });
    };
});

module.directive('kcOnReadFile', function ($parse) {
    console.debug('kcOnReadFile');
    return {
        restrict: 'A',
        scope: false,
        link: function(scope, element, attrs) {
            var fn = $parse(attrs.kcOnReadFile);

            element.on('change', function(onChangeEvent) {
                var reader = new FileReader();

                reader.onload = function(onLoadEvent) {
                    scope.$apply(function() {
                        fn(scope, {$fileContent:onLoadEvent.target.result});
                    });
                };

                reader.readAsText((onChangeEvent.srcElement || onChangeEvent.target).files[0]);
            });
        }
    };
});

module.controller('PagingCtrl', function ($scope) {
    $scope.currentPageInput = 1;

    $scope.firstPage = function() {
        if (!$scope.hasPrevious()) return;
        $scope.currentPage = 1;
        $scope.currentPageInput = 1;
    };

    $scope.lastPage = function() {
        if (!$scope.hasNext()) return;
        $scope.currentPage = $scope.numberOfPages;
        $scope.currentPageInput = $scope.numberOfPages;
    };

    $scope.previousPage = function() {
        if (!$scope.hasPrevious()) return;
        $scope.currentPage--;
        $scope.currentPageInput = $scope.currentPage;
    };

    $scope.nextPage = function() {
        if (!$scope.hasNext()) return;
        $scope.currentPage++;
        $scope.currentPageInput = $scope.currentPage;
    };

    $scope.hasNext = function() {
        return $scope.currentPage < $scope.numberOfPages;
    };

    $scope.hasPrevious = function() {
        return $scope.currentPage > 1;
    };
});

// Provides a component for injection with utility methods for manipulating strings
module.factory('KcStrings', function () {
    var instance = {};
    
    // some IE versions do not support string.endsWith method, this method should be used as an alternative for cross-browser compatibility
    instance.endsWith = function(source, suffix) {
        return source.indexOf(suffix, source.length - suffix.length) !== -1;
    };
    
    return instance;
});

module.directive('kcPaging', function () {
    return {
        scope: {
            currentPage: '=',
            currentPageInput: '=',
            numberOfPages: '='
        },
        restrict: 'E',
        replace: true,
        controller: 'PagingCtrl',
        templateUrl: resourceUrl + '/templates/kc-paging.html'
    }
});

// Tests the page number input from currentPageInput to see
// if it represents a valid page.  If so, the current page is changed.
module.directive('kcValidPage', function() {
   return {
       require: 'ngModel',
       link: function(scope, element, attrs, ctrl) {
           ctrl.$validators.inRange = function(modelValue, viewValue) {
               if (viewValue >= 1 && viewValue <= scope.numberOfPages) {
                   scope.currentPage = viewValue;
               }

               return true;
           }
       }
   }
});

// Directive to parse/format strings into numbers
module.directive('stringToNumber', function() {
    return {
        require: 'ngModel',
        link: function(scope, element, attrs, ngModel) {
            ngModel.$parsers.push(function(value) {
                return (typeof value === 'undefined' || value === null)? '' : '' + value;
            });
            ngModel.$formatters.push(function(value) {
                return parseFloat(value);
            });
        }
    };
});

// filter used for paged tables
module.filter('startFrom', function () {
    return function (input, start) {
        if (input) {
            start = +start;
            return input.slice(start);
        }
        return [];
    };
});


module.filter('resolveClientRootUrl', function() {
    return function(input) {
        if (!input) {
            return;
        }
        return input.replace("${authBaseUrl}", authServerUrl).replace("${authAdminUrl}", authUrl);
    };
});
