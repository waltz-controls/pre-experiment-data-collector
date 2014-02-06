MainController = MVC.Controller.extend('main',
    /* @Static */
    {
        isLoaded     : {
            welcome: false,
            main   : false//main part of the wizard defined in yaml on the server
        },
        toggleLoading: function () {
            $('#loading-box').toggle();
            $('#example-2').toggle();
        },
        onLoaded     : function () {
            try {
                WizardController.initialize();
            } catch (e) {
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
        load: function () {
            var wizard = WizardController.newWizardEngine();

            function loadWizardSteps() {
                WizardStep.find_all({}, {
                    onComplete: function (instances) {
                        for (var i = 0, size = instances.length; i < size; ++i) {
                            wizard.addForm(instances[i]);
                        }
                        MainController.onLoaded();
                    },
                    onFailure : function (instances) {
                        alert(instances[0].errors);
                    }
                });
            }

            WelcomeStep.create({}, {
                onComplete: function (instance) {
                    wizard.addForm(instance);
                    loadWizardSteps();
                },
                onFailure : function (instance) {
                    alert(instance.errors);
                }
            });


            //TODO add final step
        }
    }
);