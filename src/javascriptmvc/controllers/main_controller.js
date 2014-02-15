MainController = MVC.Controller.extend('main',
    /* @Static */
    {
        isLoaded: {
            welcome: false,
            main: false//main part of the wizard defined in yaml on the server
        },
        toggleLoading: function () {
            $('#loading-box').toggle();
            $('#example-2').toggle();
        },
        error:function(msg){
            noty({
                text:msg,
                type:'error',
                timeout:3000
            });
        },
        alert:function(msg){
            noty({
                text:msg,
                timeout:1500
            });
        },
        success:function(msg){
            noty({
                text:msg,
                type:'success',
                timeout:1500
            });
        },
        onLoaded: function () {
            WizardController.wizard.addForm(new FinalStep());

            try {
                WizardController.initialize();


            $('form.step[type="upload"]').each(function () {
                //                    new FileUpload(form.id,{data:form});
                var form = WizardStep.find_by_element($(this).get(0));
                Controller.publish("FileUpload.initialize", { data: form });
            });
            } catch (e) {
                MainController.error(e.message);
            }

            //initialize validation engine for each form
            //we need to call it after wizard has been initialized
            $('.regular-form').validationEngine();

            this.toggleLoading();
        }
    },
    /* @Prototype */
    {
        load: function () {
            var wizard = WizardController.newWizardEngine();

            function onFailure(instance) {
                if(!instance.errors)
                    MainController.error("Unexpected error has occurred!");
                else
                    $.each(instance.errors,function(ndx){
                        MainController.error(instance.errors[ndx]);
                    });
            }

            function loadWizardSteps() {
                WizardStep.find_all({}, {
                    onComplete: function (instances) {
                        for (var i = 0, size = instances.length; i < size; ++i) {
                            wizard.addForm(instances[i]);
                        }
                        MainController.onLoaded();
                    },
                    onFailure:onFailure
                });
            }

            WelcomeStep.create({}, {
                onComplete: function (instance) {
                    wizard.addForm(instance);
                    loadWizardSteps();
                },
                onFailure:onFailure
            });
        }
    }
);