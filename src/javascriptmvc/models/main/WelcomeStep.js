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
            var $this = $(this.element());

            //if no data set has been chosen nor a new one was created - validation fails
            if($("input[name=datasets]:checked", $this).length == 0 &&
                $('#flgCreateNew',$this).val() === 'false'){
                MainController.error('Choose data set or create a new one!');
                return false;
            }

            return true;
        },
        update: function () {
            var $this = $(this.element());
            var dataSetName;
            var template;
            if ($('#flgCreateNew',$this).val() === 'true') {
                dataSetName = $('#hdnNewDataSetName', $this).val();
                template = $('#hdnTmplName', $this).val();
                this.data.push(dataSetName);
            }else {
                dataSetName = $("input[name=datasets]:checked", $this).val();
                template = "none";
            }
            //set global data set name
            kDataSetName = dataSetName;
            var options = {
                error_timeout:3,//seconds
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
                    //iterate over upload type forms and populate them with files
                    $('form.step[type="upload"]').each(
                        function () {
                            var upload = WizardStep.find_by_element($(this).get(0));
                            $.each(upload.fields, function (nfx, fld) {
                                var files = $.map(data[fld.id], function (fileName) {
                                        return {
                                            name: fileName,
                                            url: ApplicationContext.domain + "/home/" + ApplicationContext.userName + "/upload/" + fileName
                                        };
                                });
                                Controller.publish("FileUpload.add_files",
                                    {
                                        id: upload.element_id(),
                                        data:files
                                    }
                                );
                            })
                        });

                    MainController.success("Data has been loaded successfully!");
                },
                onFailure:function(url){
                    MainController.error(url + " does not respond");
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