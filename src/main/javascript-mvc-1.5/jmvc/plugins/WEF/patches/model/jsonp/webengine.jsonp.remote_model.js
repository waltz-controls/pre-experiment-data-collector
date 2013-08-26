/**
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 26.08.13
 */
/**
 * Extends {@link MVC.Model.JsonP}
 *
 * The following changes are applied:
 *
 *   <li> add_standard_params method always adds action param</li>
 *   <li> new delete_many static method</li>
 *   <li> new find static method</li>
 *
 * @type {*}
 */
MVC.Model.JsonP = MVC.Model.JsonP.extend(
    /* @Static */
    {
        /**
         * Overwrites default function
         *
         * @param params
         * @param callback_name
         */
        add_standard_params:function(params, callback_name){
            if(!params.referer) params.referer = window.location.href;
            if(!params.action) params.action = callback_name;
        },
        /**
         * Server always returns single object in case of error.
         *
         * This method works around this limitation.
         *
         * @param instances an array of raw objects from the server
         * @return {Array} an array of newly created objects
         */
        create_many_as_existing:function (instances) {
            if (!instances) return null;
            if (instances.errors) return [this.create_as_existing(instances)];

            return this._super(instances);
        },
        delete_many:function(){
            //TODO make request with action delete
        },
        find_by_query:function(query){

        }
    },
    /* @Prototype */
    {}
);