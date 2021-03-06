window.onload = function() {
    var button = document.getElementById('fetch-users')
    var buttonClicked = false;

    button.addEventListener('click', function() {
        if (buttonClicked) {
            return;
        }
        buttonClicked = true;
        var source = new EventSource('/sse/users/events');
        source.onmessage = function(event) {
            document.getElementById('result').innerHTML += '<div>' + event.data + '</div>';
        };
        source.addEventListener('post', function () {
            source.close();
        });
    });
}



