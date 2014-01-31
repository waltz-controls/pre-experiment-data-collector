WelcomeController = MVC.Controller.Stateful.extend('Welcome',
/* @Static */
{},
/* @Prototype */
{
    init:function(){
        this._super(MVC.$E('frmWelcome'));
    },
    toHtml:function(){
        return this.render();
    }
}
);