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
        var me = this;
        //populate fields before update server


        $.each(this.fields,function(ndx,fld){
            if(this.type=='file'){
                me.values[fld.id] = [];
                $('tr.file-row > td.name', $(me.element())).each(function () {
                    me.values[fld.id].push($(this).text());
                });
            }else {
                me.values[fld.id] = $(MVC.$E(fld.id)).val();
            }
            //multichoice special case
            if(fld.fields)
                $.each(fld.fields,function(ndx,fld){
                    me.values[fld.id] = $(MVC.$E(fld.id)).val();
                });
        });
        //TODO pass actual data set name instead of 'test'
        var options = {};
        options.parameters = {
            //data set name
            name:kDataSetName,
            action:"update"
        };
        MVC.Object.extend(options.parameters, this.values);
        options.onComplete = function(){
            MainController.alert("Data has been updated successfully!");
        };

        new MVC.JsonP(ApplicationContext.domain + "/Data.json",options);
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