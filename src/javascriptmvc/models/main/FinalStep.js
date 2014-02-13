/**
*
*/
FinalStep = MVC.Model.extend('FinalStep',
/* @Static */
{
    attributes:{
        id:'string'
    },
    default_attributes:{
        id:'frmFinal'
    },
    view:'views/main/FinalStep.ejs'
},
/* @Prototype */
{
    update:function(){
        var options={
            error_timeout:3,//seconds
            parameters:{
                action:'create',//this seems ridiculous, but create acts as find as well
                name:kDataSetName
            },
            onComplete:function(data){
                //TODO refactor this when Field model will be implemented
                //TODO reuse ViewHelpers#printField
                var $dataHolder = $(MVC.$E("dataHolder"));
                var values = [];
                var view = new View({url:'views/main/final.value.ejs'});
                for(var v in data)
                    values.push(view.render({
                        fld_id:v,
                        value:data[v]
                    }));
                $dataHolder.html(values.join("<br/>"));
                noty({
                    text: "Data has been successfully stored",
                    type:"success",
                    timeout:true
                });
            },
            onFailure:function(url){
                noty({
                    text: url + " does not respond",
                    type:"error"
                });
            }
        };
        new MVC.JsonP(ApplicationContext.domain + "/Data.json",options);
    },
    toHtml:function(){
        return new View({url:this.Class.view}).render();
    }
}
);