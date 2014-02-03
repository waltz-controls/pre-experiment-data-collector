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
        this.Class.toggleLoading();

        WelcomeStep.create({},{
            onComplete:function(instance){
                document.getElementById("example-2").innerHTML = instance.toHtml();
            },
            onFailure:function(instance){
                alert(instance.errors);
            }
        });

    }
}
);