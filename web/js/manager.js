(function () {

    var pageManager = new PageManager();
    var saldoButton, reportButton, tabMenu, speseTable, storicoTable, addButton, addModal;

    window.addEventListener("load", () => {
            pageManager.start();
            pageManager.update();
    }, false);


    function PageManager() {
        const saldaConto = document.getElementById("saldaConto");
        const saldaContoText = document.getElementById("saldaContoText");
        const reportButtonElement = document.getElementById("reportButton");
        const firstTab = document.getElementById("nav-spese-tab");
        const secondTab = document.getElementById("nav-storico-tab");
        const speseTableBody = document.getElementById("speseTableBody");
        const storicoTableBody = document.getElementById("storicoTableBody");

        const addSpesaButton = document.getElementById("aggiungiSpesa");
        const addSpesaButtonText = document.getElementById("aggiungiSpesaText");

        const addSpesaModalButton = document.getElementById("addModalButton");
        const addSpesaModal = document.getElementById("addModal");
        const addSpesaModalError = document.getElementById("addModalError");

        this.start = function() {
            saldoButton = new SaldoButton(saldaConto, saldaContoText);
            saldoButton.init();
            reportButton = new ReportButton(reportButtonElement);
            reportButton.init();
            tabMenu = new TabMenu(firstTab, secondTab);
            tabMenu.init();
            speseTable = new SpeseTable(speseTableBody);
            storicoTable = new StoricoTable(storicoTableBody);
            addButton = new AggiungiSpesaButton(addSpesaButton, addSpesaButtonText);
            addButton.init();
            addModal = new AddModal(addSpesaModal, addSpesaModalButton, addSpesaModalError);
            addModal.init();

        }

        this.updateSpese = function() {
            speseTable.clear();
            speseTable.load();
        }

        this.updateStorico = function () {
            storicoTable.clear();
            storicoTable.load();
        }

        this.update = function () {
            this.updateSpese();
            this.updateStorico();
        }

        this.updateSaldo = function (saldo) {
            document.getElementById("totalSpese").innerText = saldo.toFixed(2)+"€";
        }

    }

    function SaldoButton(_divButton, _divText){
        this.button = _divButton;
        this.text = _divText;

        this.init = function () {
            var self = this;
            this.button.style.cursor = "pointer";
            this.button.onclick = function () {
                const self = this;
                postData(servletContext+"api/json/app/clearSpese", "action=clear", function (req){
                    if (req.readyState === XMLHttpRequest.DONE) {
                        switch (req.status) {
                            case 200:
                                pageManager.update();
                                break;
                            case 400: // bad request
                                console.log(req.responseText);
                                break;
                            case 401: // unauthorized
                                window.location.href = servletContext+"/login";
                                break;
                            case 500:
                                console.log(req.responseText);
                                break;
                        }
                    }
                });
            }
            this.button.onmouseover = function () {
                self.button.className = "card border-left-warning shadow h-100 py-2";
                self.text.className = "text-xs font-weight-bold text-warning text-uppercase mb-1";
            }

            this.button.onmouseout = function () {
                self.button.className = "card border-left-success shadow h-100 py-2";
                self.text.className = "text-xs font-weight-bold text-success text-uppercase mb-1";
            }
        }
    }

    function ReportButton(_button){
        this.button = _button;
        this.init = function () {
        }

        this.show = function () {
            this.button.className = "btn btn-sm btn-primary shadow-sm";
            this.button.style.display = "block";
        }

        this.hide = function () {
            this.button.className = "";
            this.button.style.display = "none";
        }
    }

    function TabMenu(_firstTab, _secondTab){
        this.firstTab = _firstTab;
        this.secondTab = _secondTab;

        this.init = function () {
            this.firstTab.onclick = function () {
                reportButton.button.href=servletContext+"home/reportGenerator?type=spese";
            }
            this.secondTab.onclick = function () {
                reportButton.button.href=servletContext+"home/reportGenerator?type=storico";
            }
        }
    }

    function SpeseTable(_tableBody) {
        this.tableBody = _tableBody;
        this.clear = function() {
            this.tableBody.innerHTML = "";
        }

        this.load = function() {
            const self = this;
            makeCall("GET", servletContext+"api/json/app/getSpese", null, function (req){
                if (req.readyState === XMLHttpRequest.DONE) {
                    switch (req.status) {
                        case 200:
                            self.insertData(JSON.parse(req.responseText));
                            break;
                        case 400: // bad request
                            console.log(req.responseText);
                            break;
                        case 401: // unauthorized
                            window.location.href = servletContext+"/login";
                            break;
                        case 500:
                            console.log(req.responseText);
                            break;
                    }
                }
            });
        }

        this.insertData = function (dataArray){
            var row, cell1, cell2, cell3, cell4;
            const self = this;
            var saldo = 0;
            dataArray.forEach(function(data) {
                row = document.createElement("tr");
                cell1 = document.createElement("td");
                cell1.textContent = data.description;
                row.appendChild(cell1);

                cell2 = document.createElement("td");
                cell2.textContent = data.date;
                row.appendChild(cell2);

                var value = data.value;
                cell3 = document.createElement("td");
                cell3.textContent = value.toFixed(2);
                saldo += value;
                if(value>0){
                    cell3.style.color = "#c40000";
                } else {
                    cell3.style.color = "#07c400";
                }
                row.appendChild(cell3);

                cell4 = document.createElement("td");
                cell4.textContent = "";
                row.appendChild(cell4);

                self.tableBody.appendChild(row);
            });
            pageManager.updateSaldo(saldo);
        }

    }

    function StoricoTable(_tableBody) {

        this.tableBody = _tableBody;

        this.clear = function() {
            this.tableBody.innerHTML = "";
        }

        this.load = function() {
            var self = this;
            makeCall("GET", servletContext+"api/json/app/getStorico", null, function (req){
                if (req.readyState === XMLHttpRequest.DONE) {
                    var message = req.responseText;
                    switch (req.status) {
                        case 200:
                            self.insertData(JSON.parse(message));
                            break;
                        case 400: // bad request
                            console.log(req.responseText);
                            break;
                        case 401: // unauthorized
                            window.location.href = "/login";
                            break;
                        case 500:
                            console.log(req.responseText);
                            break;
                    }
                }
            });
        }

        this.insertData = function (dataArray){
            var row, cell1, cell2, cell3, cell4;
            const self = this;
            dataArray.forEach(function(data) {
                row = document.createElement("tr");
                cell1 = document.createElement("td");
                cell1.textContent = data.description;
                row.appendChild(cell1);

                cell2 = document.createElement("td");
                cell2.textContent = data.date;
                row.appendChild(cell2);

                var value = data.value;
                cell3 = document.createElement("td");
                cell3.textContent = value.toFixed(2);
                if(value>0){
                    cell3.style.color = "#c40000";
                } else {
                    cell3.style.color = "#07c400";
                }
                row.appendChild(cell3);

                self.tableBody.appendChild(row);
            });
        }
    }

    function AggiungiSpesaButton(_div, _text){
        this.text = _text;
        this.button = _div;

        this.init = function () {
            var self = this;
            this.button.style.cursor = "pointer";
            this.button.onclick = function () {
                addModal.show();
            }
            this.button.onmouseover = function () {
                self.button.className = "card border-left-warning shadow h-100 py-2";
                self.text.className = "text-xs font-weight-bold text-warning text-uppercase mb-1";
            }

            this.button.onmouseout = function () {
                self.button.className = "card border-left-danger shadow h-100 py-2";
                self.text.className = "text-xs font-weight-bold text-danger text-uppercase mb-1";
            }
        }

    }

    function AddModal(_modal, _targetButton, _error){
        this.modal = _modal;
        this.saveButton = _targetButton;
        this.error = _error;

        this.init = function () {
            this.error.hidden = "hidden";
            this.error.innerText = "";
            this.saveButton.addEventListener("click", (e) => {
                const form = e.target.closest("form");
                if(form.checkValidity()){
                    var self = this;
                    makeCall("POST", servletContext+"api/json/app/addSpesa", form, function (req) {
                        if (req.readyState === XMLHttpRequest.DONE) {
                            var message = req.responseText;
                            switch (req.status) {
                                case 200:
                                    self.close();
                                    self.hideError();
                                    pageManager.updateSpese();
                                    break;
                                default: // server error
                                    self.showError(req.status + " - Si è verificato un errore");
                                    break;
                            }
                        }
                    }, true);
                } else {
                    form.reportValidity();
                }
            });
        }

        this.close = function (){
            $(this.modal).modal("hide");
        }
        this.show = function (){
            $(this.modal).modal();
        }

        this.showError = function (error){
            this.error.innerText = error;
            this.error.hidden = "";
        }

        this.hideError = function (){
            this.error.innerText = "";
            this.error.hidden = "hidden";
        }
    }

})();