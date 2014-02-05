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
    }
},
/* @Prototype */
{
    load:function(params){
        var wizard = WizardController.newWizardEngine();

        WelcomeStep.create({},{
            onComplete:function(instance){
                wizard.addForm(instance);
                OpenAjax.hub.publish("WelcomeStep.loaded", instance);
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
                OpenAjax.hub.publish("WizardSteps.loaded", instances);
            },
            onFailure:function(instances){
                alert(instances[0].errors);
            }
        });

        //TODO add final step
    },
    "WelcomeStep.loaded subscribe":function(data){
        this.Class.isLoaded.welcome = true;
        this.onLoaded();
    },
    "WizardSteps.loaded subscribe":function(data){
        this.Class.isLoaded.main = true;
        this.onLoaded();
    },
    onLoaded:function(){
        if(!this.Class.isLoaded.welcome) return;
        if(!this.Class.isLoaded.main) return;

        try{
            WizardController.initialize();
        }catch(e){
            alert(e.message);
        }

        this.Class.toggleLoading();
    }
}
);