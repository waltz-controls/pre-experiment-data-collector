/**
 * Created by IntelliJ IDEA.
 * User: Ingvord
 * Date: 19.02.12
 * Time: 0:02
 * To change this template use File | Settings | File Templates.
 */
include.plugin('model/jsonp');

MVC.Model.JsonP.add_standard_params = function(params, callback_name){
    if(!params.referer) params.referer = window.location.href;
    if(!params.action) params.action = callback_name;
};

