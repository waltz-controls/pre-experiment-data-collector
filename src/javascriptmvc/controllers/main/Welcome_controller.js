WelcomeController = MVC.Controller.Stateful.extend('Welcome',
/* @Static */
{},
/* @Prototype */
{
    init:function(){
        this._super(MVC.$E('frmWelcome'));//must not be null
    },
    toHtml:function(){
        return this.render({action:'toHtml'});//action name is being set when method is invoked through delegator
    }
}
);