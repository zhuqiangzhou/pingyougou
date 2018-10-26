app.controller('indexController',function ($http,$controller,loginService,$scope) {
    //读取当前登陆人
    $scope.showLoginName=function () {
        loginService.loginName().success(
            function (response) {
                $scope.loginName=response.loginName;
            }
        )
    }
})