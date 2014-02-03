WelcomeController = MVC.Controller.extend('frmWelcome',
/* @Static */
{},
/* @Prototype */
{
    "WelcomeStep.incoming subscribe":function(data){
        alert(data.datasets);
    }
}
);