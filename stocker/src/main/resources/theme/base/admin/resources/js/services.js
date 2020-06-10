'use strict';

var module = angular.module('keycloak.services', [ 'ngResource', 'ngRoute' ]);

module.service('Dialog', function($modal) {
    var dialog = {};

    var openDialog = function(title, message, btns, template) {
        var controller = function($scope, $modalInstance, title, message, btns) {
            $scope.title = title;
            $scope.message = message;
            $scope.btns = btns;

            $scope.ok = function () {
                $modalInstance.close();
            };
            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };
        };

        return $modal.open({
            templateUrl: resourceUrl + template,
            controller: controller,
            resolve: {
                title: function() {
                    return title;
                },
                message: function() {
                    return message;
                },
                btns: function() {
                    return btns;
                }
            }
        }).result;
    }

    var escapeHtml = function(str) {
        var div = document.createElement('div');
        div.appendChild(document.createTextNode(str));
        return div.innerHTML;
    };

    dialog.confirmDelete = function(name, type, success) {
        var title = 'Delete ' + escapeHtml(type.charAt(0).toUpperCase() + type.slice(1));
        var msg = 'Are you sure you want to permanently delete the ' + type + ' ' + name + '?';
        var btns = {
            ok: {
                label: 'Delete',
                cssClass: 'btn btn-danger'
            },
            cancel: {
                label: 'Cancel',
                cssClass: 'btn btn-default'
            }
        }

        openDialog(title, msg, btns, '/templates/kc-modal.html').then(success);
    }

    dialog.confirmGenerateKeys = function(name, type, success) {
        var title = 'Generate new keys for realm';
        var msg = 'Are you sure you want to permanently generate new keys for ' + name + '?';
        var btns = {
            ok: {
                label: 'Generate Keys',
                cssClass: 'btn btn-danger'
            },
            cancel: {
                label: 'Cancel',
                cssClass: 'btn btn-default'
            }
        }

        openDialog(title, msg, btns, '/templates/kc-modal.html').then(success);
    }

    dialog.confirm = function(title, message, success, cancel) {
        var btns = {
            ok: {
                label: title,
                cssClass: 'btn btn-danger'
            },
            cancel: {
                label: 'Cancel',
                cssClass: 'btn btn-default'
            }
        }

        openDialog(title, message, btns, '/templates/kc-modal.html').then(success, cancel);
    }

    dialog.message = function(title, message, success, cancel) {
        var btns = {
            ok: {
                label: "Ok",
                cssClass: 'btn btn-default'
            }
        }

        openDialog(title, message, btns, '/templates/kc-modal-message.html').then(success, cancel);
    }

    dialog.open = function(title, message, btns, success, cancel) {
        openDialog(title, message, btns, '/templates/kc-modal.html').then(success, cancel);
    }

    return dialog
});

module.service('CopyDialog', function($modal) {
    var dialog = {};
    dialog.open = function (title, suggested, success) {
        var controller = function($scope, $modalInstance, title) {
            $scope.title = title;
            $scope.name = { value: 'Copy of ' + suggested };
            $scope.ok = function () {
                console.log('ok with name: ' + $scope.name);
                $modalInstance.close();
                success($scope.name.value);
            };
            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };
        }
        $modal.open({
            templateUrl: resourceUrl + '/templates/kc-copy.html',
            controller: controller,
            resolve: {
                title: function() {
                    return title;
                }
            }
        });
    };
    return dialog;
});

module.service('UpdateDialog', function($modal) {
    var dialog = {};
    dialog.open = function (title, name, desc, success) {
        var controller = function($scope, $modalInstance, title) {
            $scope.title = title;
            $scope.name = { value: name };
            $scope.description = { value: desc };
            $scope.ok = function () {
                console.log('ok with name: ' + $scope.name + 'and description: ' + $scope.description);
                $modalInstance.close();
                success($scope.name.value, $scope.description.value);
            };
            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };
        }
        $modal.open({
            templateUrl: resourceUrl + '/templates/kc-edit.html',
            controller: controller,
            resolve: {
                title: function() {
                    return title;
                }
            }
        });
    };
    return dialog;
});

module.factory('Notifications', function($rootScope, $timeout) {
    // time (in ms) the notifications are shown
    var delay = 5000;

    var notifications = {};
    notifications.current = { display: false };
    notifications.current.remove = function() {
        if (notifications.scheduled) {
            $timeout.cancel(notifications.scheduled);
            delete notifications.scheduled;
        }
        delete notifications.current.type;
        delete notifications.current.header;
        delete notifications.current.message;
        notifications.current.display = false;
        console.debug("Remove message");
    }

    $rootScope.notification = notifications.current;

    notifications.message = function(type, header, message) {
        notifications.current.remove();

        notifications.current.type = type;
        notifications.current.header = header;
        notifications.current.message = message;
        notifications.current.display = true;

        notifications.scheduled = $timeout(function() {
            notifications.current.remove();
        }, delay);

        console.debug("Added message");
    }

    notifications.info = function(message) {
        notifications.message("info", "Info!", message);
    };

    notifications.success = function(message) {
        notifications.message("success", "Success!", message);
    };

    notifications.error = function(message) {
        notifications.message("danger", "Error!", message);
    };

    notifications.warn = function(message) {
        notifications.message("warning", "Warning!", message);
    };

    return notifications;
});

module.factory('Companies', function($resource) {
    return $resource(authUrl + '/rest/companies', {
    });
});

//module.factory('Company', function($resource) {
//    return $resource(authUrl + '/rest/companies/:id', {
//        id : '@company'
//    });
//});

module.factory('Currencies', function($resource) {
    return $resource(authUrl + '/rest/currencies', {
    });
});


module.factory('Current', function(Companies, $route, $rootScope) {
    console.log("Current executed");
    var current = {
    };

    $rootScope.$on('$routeChangeStart', function() {
        current.companies = Companies.get(null, function(companies) {
//            var currentRealm = null;
//            if ($route.current.params.company) {
//                for (var i = 0; i < companies.length; i++) {
//                    if (companies[i].company == $route.current.params.company) {
//                        currentCompany =  companies[i];
//                    }
//                }
//            }
            current.companies = companies;
        });
    });

    return current;
});

module.factory('TimeUnit', function() {
    var t = {};

    t.autoUnit = function(time) {
        if (!time) {
            return 'Hours';
        }

        var unit = 'Seconds';
        if (time % 60 == 0) {
            unit = 'Minutes';
            time  = time / 60;
        }
        if (time % 60 == 0) {
            unit = 'Hours';
            time = time / 60;
        }
        if (time % 24 == 0) {
            unit = 'Days'
            time = time / 24;
        }
        return unit;
    }

    t.toSeconds = function(time, unit) {
        switch (unit) {
            case 'Seconds': return time;
            case 'Minutes': return time * 60;
            case 'Hours': return time * 3600;
            case 'Days': return time * 86400;
            default: throw 'invalid unit ' + unit;
        }
    }

    t.toUnit = function(time, unit) {
        switch (unit) {
            case 'Seconds': return time;
            case 'Minutes': return Math.ceil(time / 60);
            case 'Hours': return Math.ceil(time / 3600);
            case 'Days': return Math.ceil(time / 86400);
            default: throw 'invalid unit ' + unit;
        }
    }

    return t;
});

module.factory('TimeUnit2', function() {
    var t = {};

    t.asUnit = function(time) {

        var unit = 'Minutes';

        if (time) {
            if (time == -1) {
                time = -1;
            } else {
                if (time < 60) {
                    time = 60;
                }

                if (time % 60 == 0) {
                    unit = 'Minutes';
                    time = time / 60;
                }
                if (time % 60 == 0) {
                    unit = 'Hours';
                    time = time / 60;
                }
                if (time % 24 == 0) {
                    unit = 'Days'
                    time = time / 24;
                }
            }
        }

        var v = {
            unit: unit,
            time: time,
            toSeconds: function() {
                switch (v.unit) {
                    case 'Minutes':
                        return v.time * 60;
                    case 'Hours':
                        return v.time * 3600;
                    case 'Days':
                        return v.time * 86400;
                }
            }
        }

        return v;
    }

    return t;
});

module.factory('MyMath', function() {
    var math = {};

    math.round = function(num) {
        return Math.round((num + Number.EPSILON) * 100) / 100
    }

    return math;
});


