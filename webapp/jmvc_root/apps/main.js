include.css('all', 'validationEngine.jquery');
include.resources(
    'main/string', 'main/wizard_step_view_helpers', 'main/init_helper',
    'jquery.validationEngine-2.6.4', 'languages/jquery.validationEngine-en', 'webtoolkit.base64',
    'main/noty_helper'
);
include.engines('Wizard', 'FileUpload', 'MdlDialog');
include.plugins(
    'controller', 'controller/stateful',
    'view', 'view/helpers',
    'dom/element',
    'model/jsonp',
    'io/ajax'
);

include(function () { //runs after prior includes are loaded
    include.models('main/DataSet', 'main/WelcomeStep', 'main/WizardStep', 'main/FinalStep');
    include.controllers('main', 'Wizard', 'main/Welcome');
    include.views('views/main/WelcomeStep', 'views/main/FinalStep',
        'views/main/wizard.step.field',
        'views/main/wizard.step.fieldset', 'views/main/wizard.step.multichoice', 'views/main/wizard.step.upload',
        'views/main/final.value',
        'views/main/Welcome/create', 'views/main/Welcome/dataset_row', 'views/main/Welcome/upload'
    );
});