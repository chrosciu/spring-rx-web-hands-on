var source = new EventSource('/sse/users/events');
var isOpenOnce = false;
source.onopen = function() {
    if(isOpenOnce) {
        source.close();
    } else {
        isOpenOnce = true;
    }
};
source.onmessage = function(event) {
    document.getElementById("result").innerHTML += event.data + "<br>";
};


