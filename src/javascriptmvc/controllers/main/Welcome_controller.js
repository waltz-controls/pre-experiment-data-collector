WelcomeController = MVC.Controller.extend('rows',
/* @Static */
{},
/* @Prototype */
{
    "a.delete click":function(params){
        var $btn = $(params.element);

        var name = $('input[type=radio]',$btn.parent()).val()

        var options = {
            parameters:{
                action:'delete',
                name:name
            },
            onComplete:function(data){
                window.location.href=ApplicationContext.domain;
            }
        };
        new MVC.JsonP(ApplicationContext.domain + "/Data.json",options);
    }
}
);