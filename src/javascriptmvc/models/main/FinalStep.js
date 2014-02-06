/**
*
*/
FinalStep = MVC.Model.extend('FinalStep',
/* @Static */
{
    view:'views/main/FinalStep.ejs'
},
/* @Prototype */
{
    update:function(){
        //TODO load data set and display values
    },
    toHtml:function(){
        return new View({url:this.Class.view}).render();
    }
}
);