MainController = MVC.Controller.extend('main',
/* @Static */
{
    toggleLoading  : function () {
        $('#loading-box').toggle();
        $('#example-2').toggle();
    }
},
/* @Prototype */
{
    load:function(params){
        this._Class.toggleLoading();

        var welcome = new WelcomeController();
        document.getElementById("example-2").innerHTML = welcome.toHtml();
    }
}
);