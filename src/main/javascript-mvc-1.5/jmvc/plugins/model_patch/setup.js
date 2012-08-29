/**
 * Created by
 * User: khokhria
 * Date: 18.01.12
 */
include.plugin('model');

MVC.Model.prototype._setAttribute = function(attribute, value) {
    if (MVC.Array.include(this.Class._associations, this.Class.attributes[attribute]))
        this._setAssociation(attribute, value);
    else
        this._setProperty(attribute, value);
};
MVC.Model.prototype._setAssociation = function(attribute, values) {
    var me = this;
    me[attribute] = (function(attribute, values) {
        var association = me.Class.attributes[attribute];
        if (! MVC.String.is_singular(association)) association = MVC.String.singularize(association);
        var associated_class = window[association];
        if (!associated_class) return values;
        return associated_class.create_as_existing(values);
    })(attribute, values);
};

/**
 * Returns attributes of the model, i.e. clear jsObject with only defined in this.Class attributes.
 *
 */
MVC.Model.prototype.attributes = function() {
            var attributes = {};
            var cas = this.Class.attributes;
            for (var attr in cas) {
                if (cas.hasOwnProperty(attr)) {
                    if (MVC.Array.include(this.Class._associations, this.Class.attributes[attr]))
                        attributes[attr] = this[attr].attributes();
                    else
                        attributes[attr] = this[attr];
                }
            }
            //for (var i=0; i<this.attributes.length; i++) attributes[this._properties[i]] = this[this._properties[i]];
            return attributes;
};