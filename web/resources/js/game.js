(function () {
    "use strict";

    var PUT_IN_QUEUE = "";

    $(document).ready(function () {

        var ws;

        var canvas = $("#canvas")[0];
        var context = canvas.getContext('2d');

//        context.beginPath();
//        context.moveTo(10, 10);
//        context.lineTo(100, 100);
//        context.lineTo(110, 100);
//        context.lineTo(120, 80);
//        context.stroke();


        $("#start-game").click(function () {
            webSocketInit();
        });

        $("#stop-game").click(function () {
            webSocketStop();
        });

        function webSocketInit() {
            if ("WebSocket" in window) {
                window.ploxworm.log("Starting init!");

                ws = new WebSocket("ws://localhost:8080/chat");
                ws.onopen = function () {
                    window.ploxworm.log("Connection open!");
                    ws.send("HELLO THIS IS MESSAGE");

                };
                ws.onmessage = function (evt) {
                    var received_msg = evt.data;
                    window.ploxworm.log("Received: " + received_msg);
                    var msgJson = $.parseJSON(received_msg);
                    console.log("msgJson: " + msgJson);
                    console.log("msgJson.type: " + msgJson.type);
                    if (msgJson.type === 'frame') {
                        render(msgJson.data);
                    }
                };
                ws.onclose = function (evt) {
                    window.ploxworm.log("Connection closed!");
                };
                ws.onerror = function (evt) {
                    window.ploxworm.log("error!");
                };
            } else {
                // The browser doesn't support WebSocket
                alert("WebSocket NOT supported by your Browser! GTFO!");
            }
        }

        function render(jsonData) {
            console.log("render: " + jsonData);
            var you = jsonData.you;
            var first = true;
            context.beginPath();
            $.each(you, function () {
                if (first) {
                    context.moveTo(this.x, this.y);
                    first = false;
                } else {
                    context.lineTo(this.x, this.y);
                }
            });
            context.stroke();
        }

        $(window).bind('beforeunload', function () {
            webSocketStop();
        });

        function webSocketStop() {
            window.ploxworm.log("webSocketStop");
            if (ws) {
                ws.close();
            }
        }

        window.ploxworm.log("loaded");
    });

})();