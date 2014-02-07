WizardController = MVC.Controller.extend('Wizard',
/* @Static */
{
    wizard:null,
    /**
    *
    * @return a new WizardEngine instance
    */
    newWizardEngine:function(){
        this.wizard = new WizardEngine("Wizard","PreExperiment Data Collector",{
            beforeForward: function (event, state) {
                var currentStep = state.step.prevObject.get(state.stepIndex - 1);
                var wizardStep;
                if(state.stepIndex == 1)
                    wizardStep = WelcomeStep.find_by_element(currentStep);
                else
                    wizardStep = WizardStep.find_by_element(currentStep);

                var isValid = wizardStep.validate();
                if (isValid) {
                    //storing the data in the model object
                    wizardStep.update();
                    //allow user to move forward
                    return true;
                } else {
                    //prevent user from moving forward
                    return false;
                }
            },
            afterForward : function (event, state) {
                //workaround for Jquery-ui accordion height set to zero issue
                //see http://forum.jquery.com/topic/accordion-height-set-to-zero-issue
                //"You must have the accordion visible when the height is calculated" [Scott Gonzales].
                $('div.accordion', state.step).accordion({
                    /**
                     * Set active tab to true, other - false.
                     *
                     * @param event
                     * @param ui
                     */
                    create: function (event, ui) {
                        var id = $('h3[tabindex=0]', $(this)).attr('choice-id');
                        $('#' + id, $(this)).val(true);

                        $('h3[tabindex=-1]', $(this)).each(function () {
                            var id = $(this).attr('choice-id');
                            $('#' + id).val(false);
                        });
                    },
                    /**
                     * Exchange tab values: newActive -> true, oldActive -> false
                     *
                     * @param event
                     * @param ui
                     */
                    change: function (event, ui) {
                        $('#' + ui.oldHeader.attr('choice-id'), ui.oldContent).val(false);

                        $('#' + ui.newHeader.attr('choice-id'), ui.newContent).val(true);
                    }
                });

                //load all values and display them
                if(state.stepIndex == state.stepsPossible)
                    FinalStep.find_by_element(
                    /*we need this long expression to get HTML dom object, but step is a jQuery obj*/
                        state.step.prevObject.get(state.stepIndex)
                    ).update();
            }
        });
        return this.wizard;
    },
    initialize:function(){
        this.wizard.initialize();
    }
},
/* @Prototype */
{}
);