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
                noty({
                    text:e.message,
                    type:"error"
                });
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
                    noty({
                        text: "Unexpected error has occurred!",
                        type:"error"
                    });
                else
                    $.each(instance.errors,function(ndx){
                        noty({
                            text: instance.errors[ndx],
                            type:"error"
                        });
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