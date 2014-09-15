module.controller('GlobalController', function($scope) {
  $scope.sleepInterval = 1000;
});

module.controller('SimpleUserController1', function($scope, SimpleUser) {
  console.log('SimpleUserController1 invoked ');

  $scope.user = SimpleUser.get( { sleep: $scope.sleepInterval }, function() {
       $scope.user.loaded = true;
  });
});

module.controller('SimpleUserController2', function($scope, loadedUser) {
  console.log('SimpleUserController2 invoked ');

  $scope.user = loadedUser;
});

