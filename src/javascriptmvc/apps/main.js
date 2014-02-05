include.css('main/all', 'validationEngine.jquery');
include.resources(
    'main/wizard_step_view_helpers',
    'jsonp.utility.functions', 'jquery.validationEngine', 'languages/jquery.validationEngine-en', 'webtoolkit.base64'
);
include.engines('Wizard');
include.plugins(
'controller','controller/stateful',
'view','view/helpers',
'dom/element',
'model/jsonp'
);

include(function(){ //runs after prior includes are loaded
include.models('main/WelcomeStep','main/WizardStep');
include.controllers('main','main/Welcome');
include.views('views/main/WelcomeStep','views/main/wizard.step.field',
    'views/main/wizard.step.fieldset','views/main/wizard.step.multichoice','views/main/wizard.step.upload');
});