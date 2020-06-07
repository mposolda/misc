module.controller('GlobalCtrl', function($scope, $http, Auth, Current, $location, Notifications) {
    $scope.authUrl = authUrl;
    $scope.resourceUrl = resourceUrl;
    $scope.auth = Auth;

    $scope.$watch(function() {
        return $location.path();
    }, function() {
        $scope.fragment = $location.path();
        $scope.path = $location.path().substring(1).split("/");
    });
});

module.controller('HomeCtrl', function(Realm, Auth, Current, $location) {

    Realm.query(null, function(realms) {
        var realm;
        if (realms.length == 1) {
            realm = realms[0];
        } else if (realms.length == 2) {
            if (realms[0].realm == Auth.user.realm) {
                realm = realms[1];
            } else if (realms[1].realm == Auth.user.realm) {
                realm = realms[0];
            }
        }
        if (realm) {
            Current.realms = realms;
            Current.realm = realm;
            var access = getAccessObject(Auth, Current);
            if (access.viewRealm || access.manageRealm) {
                $location.url('/realms/' + realm.realm );
            } else if (access.queryClients) {
                $location.url('/realms/' + realm.realm + "/clients");
            } else if (access.viewIdentityProviders) {
                $location.url('/realms/' + realm.realm + "/identity-provider-settings");
            } else if (access.queryUsers) {
                $location.url('/realms/' + realm.realm + "/users");
            } else if (access.queryGroups) {
                $location.url('/realms/' + realm.realm + "/groups");
            } else if (access.viewEvents) {
                $location.url('/realms/' + realm.realm + "/events");
            }
        } else {
            $location.url('/realms');
        }
    });
});

