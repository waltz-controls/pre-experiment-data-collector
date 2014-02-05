MainController = MVC.Controller.extend('main',
/* @Static */
{
    isLoaded:{
        welcome:false,
        main:false//main part of the wizard defined in yaml on the server
    },
    toggleLoading  : function () {
        $('#loading-box').toggle();
        $('#example-2').toggle();
    },
    onLoaded:function(){
        if(!this.isLoaded.welcome) return;
        if(!this.isLoaded.main) return;

        try{
            WizardController.initialize();
        }catch(e){
            alert(e.message);
        }

        //initialize validation engine for each form
        //we need to call it after wizard has been initialized
        $('.regular-form').validationEngine();

        this.toggleLoading();
    }
},
/* @Prototype */
{
    load:function(){
        var wizard = WizardController.newWizardEngine();

        WelcomeStep.create({},{
            onComplete:function(instance){
                wizard.addForm(instance);
                MainController.isLoaded.welcome = true;
                MainController.onLoaded();
            },
            onFailure:function(instance){
                alert(instance.errors);
            }
        });

        WizardStep.find_all({},{
            onComplete:function(instances){
                for(var i = 0, size = instances.length; i < size; ++i){
                    wizard.addForm(instances[i]);
                }
                MainController.isLoaded.main = true;
                MainController.onLoaded();
            },
            onFailure:function(instances){
                alert(instances[0].errors);
            }
        });

        //TODO add final step
    }
}
);