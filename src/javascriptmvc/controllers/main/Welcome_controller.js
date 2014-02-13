WelcomeController = MVC.Controller.extend('rows',
/* @Static */
{},
/* @Prototype */
{
    "a.delete click":function(params){
        var $btn = $(params.element);

        var name = $('input[type=radio]',$btn.parent()).val()

        var options = {
            error_timeout:3,//seconds
            parameters:{
                action:'delete',
                name:name
            },
            onComplete:function(data){
                window.location.href=ApplicationContext.domain;
                noty({
                    text: name + " has been deleted",
                    type:"success",
                    timeout:true
                });
            },
            onFailure:function(url){
                noty({
                    text: url + " does not respond",
                    type:"error"
                });
            }
        };
        new MVC.JsonP(ApplicationContext.domain + "/Data.json",options);
    }
}
);