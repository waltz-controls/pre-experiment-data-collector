/**
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 26.08.13
 */
MVC.Controller = MVC.Controller.extend(
    {},
    {
        render:function(options){
            if(typeof options == "string"){
                var result = new View({url:options}).render(this, {});
                options = {
                    text: result,
                    to:this.element
                }
            }

            this._super(options);
        }
    });