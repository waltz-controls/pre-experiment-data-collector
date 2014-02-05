/**
*
*/
WizardStep = MVC.Model.JsonP.extend('WizardStep',
/* @Static */
{
    domain: ApplicationContext.domain,
    attributes:{
        id:"string",
        help:"text",
        label:"string",
        type:"string",
        fields:"Object[]",
        values:"Object"
    },
    view:'views/main/wizard.step.###.ejs'
},
/* @Prototype */
{
    /**
     * [Constructor]
     * @param proto
     */
    init:function(proto){
        this._super(proto);

        this.values = {};

        for(var i = 0, size = this.fields.length; i<size; ++i){
            var fld = this.fields[i];
            this.values[fld.id] = null;
        }
    },
    /**
     * Updates this values and sends them to the server
     */
    update:function(){
        //TODO if we create a dedicated model for each fld we can store $ wrapper there
        //TODO additionally this code will be much clear
        $.each(this.fields,MVC.Function.bind(function(ndx,fld){
            this.values[fld.id] = $(MVC.$E(fld.id)).val();
        }),this);
        WizardStep.update(this.id,this.values/*,callback that will show notification*/);
    },
    /**
     *
     * @returns {boolean}
     */
    validate:function(){
        var $this = $(this.element());
        return $this.validationEngine('validate');
    },
    toHtml:function(){
        return new View({url:this.Class.view.replace("###",this.type)}).render(this,WizardStepViewHelpers);
    }
});