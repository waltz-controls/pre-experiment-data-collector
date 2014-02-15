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
    activate:function(){
        var options={
            error_timeout:3,//seconds
            parameters:{
                action:'create',//this seems ridiculous, but create acts as find as well
                name:kDataSetName
            },
            onComplete:function(data){
                if(data.errors){
                    $.each(data.errors,function(ndx){
                        MainController.error(data.errors[ndx]);
                    });
                    return;
                }
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
                MainController.success("Data has been successfully stored");
            },
            onFailure:function(url){
                MainController.error(url + " does not respond");
            }
        };

        //temporal work around server side race condition when it updates data after loading it
        setTimeout(function(){new MVC.JsonP(ApplicationContext.domain + "/Data.json",options);},500);
    },
    toHtml:function(){
        return new View({url:this.Class.view}).render();
    }
}
);