WelcomeController = MVC.Controller.extend('rows',
    /* @Static */
    {},
    /* @Prototype */
    {
        "a.delete click"   : function (params) {
            var $btn = $(params.element);

            var name = $('input[type=radio]', $btn.parent()).val()

            var options = {
                error_timeout: 3,//seconds
                parameters   : {
                    action: 'delete',
                    name  : name
                },
                onComplete   : function (data) {
                    window.location.href = ApplicationContext.domain;
                    noty({
                        text   : name + " has been deleted",
                        type   : "success",
                        timeout: true
                    });
                },
                onFailure    : function (url) {
                    noty({
                        text: url + " does not respond",
                        type: "error"
                    });
                }
            };
            new MVC.JsonP(ApplicationContext.domain + "/Data.json", options);
        },
        "a#btnCreate click": function (params) {
            var frmWelcome = WelcomeStep.find_by_element(MVC.$E('WelcomeStep_frmWelcome'));
            var $dlgCreate = $('#dlgCreate');
            if ($dlgCreate.length == 0) {
                $dlgCreate = $(new View({url: 'views/main/Welcome/create.ejs'}).render(frmWelcome)).appendTo(document.body)
                    .dialog({
                    autoOpen: false,
                    height  : 300,
                    width   : 350,
                    modal   : true,
                    buttons : {
                        Confirm: function () {
                            //TODO validate duplicated name
                            var $txtNewDataSetName = $('#txtNewDataSetName');
                            var newDataSet = $txtNewDataSetName.val();
                            if (!newDataSet) {
                                $txtNewDataSetName.addClass("ui-state-error");
                                return;
                            }
                            $('#hdnNewDataSetName').val(newDataSet);
                            $('#hdnTmplName').val($('#tmplName').val());
                            $('#flgCreateNew').val(true);
                            $(this).dialog("close");
                            WizardController.wizard.$next.click();
                        },
                        Cancel : function () {
                            $(this).dialog("close");
                        }
                    },
                    close   : function () {
                        $('#txtNewDataSetName').removeClass("ui-state-error");
                    }
                });
            }
            $dlgCreate.dialog('open');
        }
    }
);