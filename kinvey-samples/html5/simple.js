var promise = Kinvey.init({
  appKey : 'kid_TP_gUxS3JO',
  appSecret : '9873266d76cc4a51bd44a31f9e92f8a9'
});
promise.then(function(activeUser) {
  console.log('Kinvey User Success. activeUser: ' + activeUser);
}, function(error) {
  console.log('Kinvey User Failed. Response: ' + error.description);
});

/*var promise2 = Kinvey.ping();*/