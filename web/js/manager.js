(function () {

    var pageManager = new PageManager();
    var saldoButton;

    window.addEventListener("load", () => {
            pageManager.start();
    }, false);
    function PageManager() {
        const saldaConto = document.getElementById("saldaConto");
        const saldaContoText = document.getElementById("saldaContoText");

        this.start = function() {
            saldoButton = new SaldoButton(saldaConto, saldaContoText);
            saldoButton.init();
        }

    }

    function SaldoButton(_divButton, _divText){
        this.button = _divButton;
        this.text = _divText;

        this.init = function () {
            var self = this;
            this.button.onclick = function () {
                //Call API and set all transaction to payed=1
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

})();