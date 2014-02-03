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
        WelcomeStep.create({},{
            onComplete:function(instance){
                MainController.toggleLoading();
                document.getElementById("example-2").innerHTML = instance.toHtml();
            },
            onFailure:function(instance){
                alert(instance.errors);
            }
        });

        //TODO load meta and parse it to forms
        //TODO put all forms to wizard
    }
}
);