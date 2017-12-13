'use strict';

App.factory('Service', ['$http', '$q', function ($http, $q) {

  return {

    fetchItems: function () {
      return $http.get('http://localhost:8082/api/v1/items?limit=25')
      .then(
          function (response) {
            return response.data;
          },
          function (errResponse) {
            console.error('Error while fetching users');
            return $q.reject(errResponse);
          }
      );
    },

    createBlog: function (blog) {
      return $http.post('http://localhost:8082/api/v1/blogs', blog)
      .then(
          function (response) {
            return response.data;
          },
          function (errResponse) {
            console.error('Error while creating user');
            return $q.reject(errResponse);
          }
      );
    },
  };

}]);