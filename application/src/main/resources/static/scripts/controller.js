'use strict';

App.controller('Controller', ['$scope', 'Service', function ($scope, service) {
  var self = this;
  self.blog = {feedURL: '', name: '', link: ''};
  self.items = [];

  self.fetchItems = function () {
    service.fetchItems()
    .then(
        function (d) {
          self.items = d;
        },
        function (errResponse) {
          console.error('Error while fetching Currencies');
        }
    );
  };

  self.createBlog = function (blog) {
    service.createBlog(blog)
    .then(
        self.fetchItems,
        function (errResponse) {
          console.error('Error while creating User.');
        }
    );
  };

  self.fetchItems();

  self.submit = function () {
    console.log('Saving New Blog', self.blog);
    self.createBlog(self.blog);
    self.reset();
  };

  self.reset = function () {
    self.blog = {feedURL: '', name: '', link: ''};
    $scope.myForm.$setPristine(); //reset Form
  };

}]);