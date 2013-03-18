(function () {
    "use strict";

    $(document).ready(function () {

        var ws;

        var canvas = $("#canvas")[0];
        var context = canvas.getContext('2d');
        var board;

        var mouseX;
        var mouseY;

        $("#start-game").click(function () {
            webSocketInit();
        });

        $("#stop-game").click(function () {
            webSocketStop();
        });

        function webSocketInit() {
            if ("WebSocket" in window) {
                window.ploxworm.log("Starting init!");

                ws = new WebSocket('ws://'+window.location.hostname + ':' + location.port+'/chat');
                ws.onopen = function () {
                    window.ploxworm.log("Connection open!");
                    ws.send("HELLO THIS IS MESSAGE");

                };
                ws.onmessage = function (evt) {
                    var received_msg = evt.data;
                    window.ploxworm.log("Received: " + received_msg);
                    var msgJson = $.parseJSON(received_msg);
//                    console.log("msgJson: " + msgJson);
//                    console.log("msgJson.type: " + msgJson.type);
                    if (msgJson.type === 'frame') {
                        render(msgJson.data);
                    } else if (msgJson.type === 'board') {
                        saveBoard(msgJson.data);
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

        function saveBoard(jsonData) {
            board = jsonData;
            renderBoard();
        }

        function render(jsonData) {
            console.log("render: " + jsonData);

            renderFrame(jsonData);
            renderBoard();
        }

        function renderFrame(jsonData) {
            var worms = jsonData.worms;

            $.each(worms, function () {
                var first = true;
                context.beginPath();
                $.each(this, function () {
                    if (first) {
                        context.moveTo(this.x, this.y);
                        first = false;
                    } else {
                        context.lineTo(this.x, this.y);
                    }
                });
                context.stroke();
            });
        }

        function renderBoard() {
//            console.log('renderboard: '+board);
            var obstacles = board.obstacles;
            context.beginPath();
            $.each(obstacles, function () {
                var data = this.data;
                if (this.type === 'rectangle') {
                    context.rect(data.left, data.top, data.right - data.left, data.bottom - data.top);
                } else if (this.type === 'circle') {
                    console.log('circle: ' + data.x + ' ' + data.y + ' ' + data.radius);
                    context.arc(data.x, data.y, data.radius, 0, 2 * Math.PI, false);
                } else {
                    console.log('wtf unknown type: ' + this.type);
                }
            });
            context.fillStyle = 'gray';
            context.fill();
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

        $("#canvas").mousemove(function (event) {
            //taken from http://stackoverflow.com/questions/1114465/getting-mouse-location-in-canvas
            if (event.offsetX) {
                mouseX = event.offsetX;
                mouseY = event.offsetY;
            } else if (event.layerX) {
                mouseX = event.layerX;
                mouseY = event.layerY;
            }
//            window.ploxworm.log("move: " + mouseX + ", " + mouseY);
        });

        window.ploxworm.log("loaded");
    });

})();