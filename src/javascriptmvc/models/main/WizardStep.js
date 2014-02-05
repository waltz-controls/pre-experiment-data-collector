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
    updateValues:function(values){
        for(var v in values){
            if(this.values.hasOwnProperty(v))
                this.values[v] = values[v];
        }
    },
    save:function(cbs){
        //TODO send to server
    },
    toHtml:function(){
        return new View({url:this.Class.view.replace("###",this.type)}).render(this,WizardStepViewHelpers);
    }
});