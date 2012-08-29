/*
 * The main contributor to this project is Institute of Materials Research,
 * Helmholtz-Zentrum Geesthacht,
 * Germany.
 *
 * This project is a contribution of the Helmholtz Association Centres and
 * Technische Universitaet Muenchen to the ESS Design Update Phase.
 *
 * The project's funding reference is FKZ05E11CG1.
 *
 * Copyright (c) 2012. Institute of Materials Research,
 * Helmholtz-Zentrum Geesthacht,
 * Germany.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

/**
 * Only one instance of this controller is allowed per application.
 */
WizardEngine = MVC.Controller.Stateful.extend('Wizard',
    /* @Prototype */
    {
        title:"Basic Wizard With Progress Bar",
        $element:null, //jQuery object that contains wizard wrapper
        options:null,
        isInitialized:false,
        forms:[],
        defaultOptions:{
            submit:".submit",
            afterSelect:function (event, state) {
                $("#progressbar").progressbar("value", state.percentComplete);
                $("#location").text("(" + state.stepsComplete + "/" + state.stepsPossible + ")");
            },
            stepsWrapper:"#wizard-steps",
            afterForward:function (event, state) {
                if (window.location.hash) {
                    window.location.hash = "#" + state.stepIndex;//+ "@" + elementId;
                } else {
                    window.location += "#" + state.stepIndex;//+ "@" + elementId;
                }
            },
            afterBackward:function (event, state) {
                if (window.location.hash) {
                    window.location.hash = "#" + state.stepIndex;//+ "@" + elementId;
                } else {
                    window.location += "#" + state.stepIndex;//+"@"+ elementId;
                }
            }
        },
        submitHandler:function (params) {
            Controller.publish("Wizard.submit", params);
        },
        /**
         * Creates new wizard instance. Before further usage newly created instance should be initialized via initialize([]) method.
         *
         * @param elementId an id of the wrapper element on the main page, i.e. <div id="myWizard"></div>
         * @param title of this Wizard instance. Appears on the page up to progressbar.
         * @param options
         * @param submitHandler custom handler for submit button
         */
        init:function (elementId, title, options, submitHandler) {
            if (!document.getElementById(elementId)) {
                throw 'Wizard#init(elementId,options): elementId can not be null';
            }

            function defineNewOptions(options, thiz) {
                function mergeFunctions(defaultFunction, userFunction) {
                    return function (event, state) {
                        userFunction(event, state);
                        defaultFunction(event, state);
                    }
                }

                options = options || {};
                //merge essential options
                //this is needed to support proper browser history for wizard's steps
                if (options.afterForward) {
                    options.afterForward = mergeFunctions(thiz.defaultOptions.afterForward, options.afterForward);
                }
                if (options.afterBackward) {
                    options.afterBackward = mergeFunctions(thiz.defaultOptions.afterBackward, options.afterBackward);
                }

                var newOptions = MVC.Object.extend(thiz.defaultOptions, options);
                return newOptions;
            }

            var newOptions = defineNewOptions(options, this);

            this.options = newOptions;

            this._super(MVC.$E(elementId));

            this.title = title || this.title;
            this.submitHandler = submitHandler || this.submitHandler;
            this.$element = $(this.element);
        },
        /**
         * Finalizes instance creation. Returns itself for convenience.
         *
         * @param data an array of form objects: {id,toHtml()}. Overrides this instance forms collection.
         */
        initialize:function (data) {
            //TODO validate forms compatibility
            this.forms = data || this.forms;

            this.render({
                to:this.element,
                action:'initialize'
            });

            $("#progressbar").progressbar();

            //initialize jQuery.wizard
            this.$element.wizard(this.options);

            this.isInitialized = true;
            return this;
        },
        "button.forward click":function (params) {
            if (!this.isInitialized) {
                throw "Wizard instance was not initialized. Call Wizard.initialize(data) first."
            }
        },
        "button.backward click":function (params) {
            if (!this.isInitialized) {
                throw "Wizard instance was not initialized. Call Wizard.initialize(data) first."
            }
        },
        "button.submit click":function (params) {
            if (!this.isInitialized) {
                throw "Wizard instance was not initialized. Call Wizard.initialize(data) first."
            }
            this.submitHandler(params);
        },
        /**
         * Adds a form to the instance forms collection. Returns itself for convenience.
         *
         * Call this method before initialization to override forms to be rendered.
         *
         * @param form {id,toHtml()}
         */
        addForm:function (form) {
            //TODO validate form for compatibility
            this.forms.push(form);
            return this;
        }
    }
);