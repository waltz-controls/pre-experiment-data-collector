/**
*
*/
WelcomeStep = MVC.Model.JsonP.extend('WelcomeStep',
/* @Static */
{
    domain: ApplicationContext.domain,
    attributes:{
        data:'string[]'
    },
    view:'views/main/WelcomeStep.ejs'
},
/* @Prototype */
{
    toHtml:function(){
        return new View({url:this.Class.view}).render(this.attributes());
    }
}
);