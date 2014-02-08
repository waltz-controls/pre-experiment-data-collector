/**
 *
 */
WelcomeStep = MVC.Model.JsonP.extend('WelcomeStep',
    /* @Static */
    {
        domain: ApplicationContext.domain,
        attributes: {
            id: 'string',
            data: 'string[]'
        },
        default_attributes: {
            id: 'frmWelcome'
        },
        view: 'views/main/WelcomeStep.ejs'
    },
    /* @Prototype */
    {
        validate: function () {
            //TODO validate data set name
            return true;
        },
        update: function () {
            var $this = $(this.element());
            var dataSetName = $("input[name=datasets]:checked", $this).val();
            var template = "none";
            if ('new' == dataSetName) {
                dataSetName = $('#txtNewDataSetName', $this).val();
                template = $('#tmplName', $this).val()
            }
            //set global data set name
            kDataSetName = ApplicationContext["data-set-name"] = dataSetName;
            var options = {
                parameters: {
                    action: "create",
                    name: dataSetName,
                    template: template
                },
                //update UI with values
                onComplete: function (data) {
                    for (var v in data) {
                        $(MVC.$E(v)).val(data[v]);
                    }
                    $('form.step[type="upload"]').each(
                        function () {
                            var upload = WizardStep.find_by_element($(this).get(0));
                            $.each(upload.fields, function (nfx, fld) {
                                var files = $.map(data[fld.id], function (fileName) {
                                        return {
                                            name: fileName
                                        };
                                });
                                Controller.publish("FileUpload.add_files",
                                    {
                                        id: upload.element_id(),
                                        data:files
                                    }
                                );
                            })
                        })
                }
            };
            new MVC.JsonP(this.Class.domain + "/Data.json", options);
        },
        /**
         *
         * @returns {HTML}
         */
        toHtml: function () {
            return new View({url: this.Class.view}).render(this);
        }
    }
);