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
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 24.05.12
 */
MainController = MVC.Controller.extend('main',
    /* @Static */
    {
        toggleLoading  : function () {
            $('#loading-box').toggle();
            $('#example-2').toggle();
        },
        /**
         * Creates and initializes Wizard according to meta (meta.forms are wrapped by Wizard steps)
         *
         * @param meta
         */
        processMetaData: function (meta, values) {
            var wizard = new WizardEngine("Wizard", "Tomography Pre Experiment Data Wizard", {
                beforeForward: function (event, state) {
                    var currentStep = state.step.prevObject.get(state.stepIndex - 1);
                    var currentForm = $(currentStep);

                    var isValid = currentForm.validationEngine('validate');
                    if (isValid) {
                        //storing the data in the model object
                        Wrapped.publish("update", { element: currentForm });
                        //allow user to move forward
                        return true;
                    } else {
                        //prevent user from moving forward
                        return false;
                    }
                },
                afterForward : function (event, state) {
                    //workaround for Jquery-ui accordion height set to zero issue
                    //see http://forum.jquery.com/topic/accordion-height-set-to-zero-issue
                    //"You must have the accordion visible when the height is calculated" [Scott Gonzales].
                    $('div.accordion', state.step).accordion({
                        /**
                         * Set active tab to true, other - false.
                         *
                         * @param event
                         * @param ui
                         */
                        create: function (event, ui) {
                            var id = $('h3[tabindex=0]', $(this)).attr('choice-id');
                            $('#' + id, $(this)).val(true);

                            $('h3[tabindex=-1]', $(this)).each(function () {
                                var id = $(this).attr('choice-id');
                                $('#' + id).val(false);
                            });
                        },
                        /**
                         * Exchange tab values: newActive -> true, oldActive -> false
                         *
                         * @param event
                         * @param ui
                         */
                        change: function (event, ui) {
                            $('#' + ui.oldHeader.attr('choice-id'), ui.oldContent).val(false);

                            $('#' + ui.newHeader.attr('choice-id'), ui.newContent).val(true);
                        }
                    });
                }
            });

            $.each(meta.forms, function (i, frm) {
                wizard.addForm({
                    id    : frm.id,
                    toHtml: function () {
                        return MVC.View.Helpers.prototype.printForm(i, frm, values);
                    }
                });
            });


            wizard.addForm({
                id    : "frmDataSubmit",
                toHtml: function () {
                    return new View({ url: 'views/main/wizard.form.submit.ejs' }).render();
                }
            }).initialize();

            $.each(meta.forms, function (i, form) {
                if (form.type == 'upload') {
                    //                    new FileUpload(form.id,{data:form});
                    Controller.publish("FileUpload.initialize", { data: form });
                    var frmValues = values[form.id];
                    $.each(form.fields, function (ii, fld) {
                        var data = frmValues[fld.id] ? frmValues[fld.id].split(';') : [];
                        Controller.publish("FileUpload.add_file_names", { id: form.id, data: data });
                    });
                }
            });

            $('.regular-form').validationEngine();

            $('ul.icons li').hover(
                function () {
                    $(this).addClass('ui-state-hover');
                },
                function () {
                    $(this).removeClass('ui-state-hover');
                }
            );
        }
    },
    /* @Prototype */
    {
        load                      : function (params) {
            //TODO create wizard with welcome form (displays all datasets)
            //TODO request meta and add it to wizard
            //TODO end review form to the wizard
            //TODO initialize wizard


            DataSetName.find_all({}, {
                onSuccess: function (data) {
                    MainController.toggleLoading();
                    DataSetName.publish("find_all", { data: data });
                },
                onFailure: function (data) {
                    //TODO request timeouted
                    alert("Request timeouts.");
                }
            });
        },
        /**
         * Wizard submit handler. Gathers all the data and posts it to the server.
         *
         * @param params
         */
        "Wizard.submit subscribe" : function (params) {
            var frm = $('#frmDataSubmit');

            var data = DataSet.store.find_one(ApplicationContext.crtDataSetId).data.attributes();

            var encoded = Base64.encode(JSON.stringify(data));

            $("<input type='hidden' name='data'>")
                .val(encoded)
                .appendTo(frm);

            var crtDataSetId = ApplicationContext.crtDataSetId;
            $("<input type='hidden' name='data-set-name'>")
                .val(crtDataSetId)
                .appendTo(frm);

            frm.submit();
        },
        "ul.icons li.add click"   : function (params) {
            var el = params.element;

        },
        "ul.icons li.remove click": function (params) {
            var el = params.element;
        }
    }
);