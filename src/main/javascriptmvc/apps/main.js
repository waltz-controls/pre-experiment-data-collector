include.css('main/all', 'validationEngine.jquery');
include.resources(
    'jsonp.utility.functions', 'jquery.validationEngine', 'languages/jquery.validationEngine-en', 'webtoolkit.base64'
);
include.engines();
include.plugins(
'controller','controller/stateful',
'view','view/helpers',
'dom/element',
'model/jsonp'
);

include(function(){ //runs after prior includes are loaded
include.models();
include.controllers('main','main/Welcome');
include.views();
});