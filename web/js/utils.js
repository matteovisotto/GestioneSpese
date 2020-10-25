function makeCall(method, url, formElement, cback, reset = true) {
    var req = new XMLHttpRequest(); // visible by closure
    req.onreadystatechange = function() {
        cback(req)
    }; // closure
    req.open(method, url);
    if (formElement == null) {
        req.send();
    } else {
        req.send(new FormData(formElement));
    }
    if (formElement !== null && reset === true) {
        formElement.reset();
    }
}

function postData(url, data, cback) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function (){
        cback(req)
    };
    req.open("POST", url);
    req.setRequestHeader('Content-Type', "application/x-www-form-urlencoded");
    if(data == null){
        req.send();
    } else {
        req.send(data);
    }
}