include.css('main/all', 'validationEngine.jquery');
include.resources(
    'main/string','main/wizard_step_view_helpers',
    'jsonp.utility.functions', 'jquery.validationEngine', 'languages/jquery.validationEngine-en', 'webtoolkit.base64'
);
include.engines('Wizard','FileUpload');
include.plugins(
'controller','controller/stateful',
'view','view/helpers',
'dom/element',
'model/jsonp'
);

include(function(){ //runs after prior includes are loaded
include.models('main/WelcomeStep','main/WizardStep','main/FinalStep');
include.controllers('main','main/Wizard');
include.views('views/main/WelcomeStep','views/main/FinalStep','views/main/wizard.step.field',
    'views/main/wizard.step.fieldset','views/main/wizard.step.multichoice','views/main/wizard.step.upload',
    'views/main/final.value'
);
});