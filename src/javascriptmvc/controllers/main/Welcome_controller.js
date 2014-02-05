WelcomeController = MVC.Controller.extend('WizardStep_frmWelcome',
/* @Static */
{},
/* @Prototype */
{
    "WelcomeStep.incoming subscribe":function(data){
        alert(data.datasets);
    }
}
);