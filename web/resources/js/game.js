(function () {
    "use strict";

    $(document).ready(function () {

        var ws;

        var canvas = $("#canvas")[0];
        var context = canvas.getContext('2d');
        var match;

        var headX;
        var headY;
        var mouseX;
        var mouseY;

        var gameRunning = false;

        $("#start-game").click(function () {
            prepareGame();
        });

        $("#stop-game").click(function () {
            webSocketStop();
        });

        function prepareGame() {
            //TODO flytta tillbaka spelaren till initholder eller nåt när han dör
            if (!ws || ws.readyState !== 1) {
                if (ws) {
                    window.ploxworm.log("websocket readystate: " + ws.readyState);
                } else {
                    window.ploxworm.log("websocket: " + ws);
                }
                webSocketInit();
            } else {
                sendGameRequest();
            }
        }

        function webSocketInit() {
            if ("WebSocket" in window) {
                window.ploxworm.log("Opening websocket");

                ws = new WebSocket('ws://' + window.location.hostname + ':' + location.port + '/chat');
                ws.onopen = function () {
                    window.ploxworm.log("Connection open!");
                    sendGameRequest();
                };
                ws.onmessage = function (evt) {
                    var received_msg = evt.data;
//                    window.ploxworm.log("Received: " + received_msg);
                    var msgJson = $.parseJSON(received_msg);
//                    console.log("msgJson.type: " + msgJson.type);
                    if (msgJson.type === 'frame') {
                        render(msgJson.data);
                    } else if (msgJson.type === 'match') {
                        //currently this is our "game started" signal
                        saveMatchData(msgJson.data);
                        gameRunning = true;
                        startSendingPosition();
                    } else if (msgJson.type === 'end_round') {
                        window.ploxworm.log("Game ended!");
                        gameRunning = false;
                    } else {
                        window.ploxworm.log('unknown data: ' + msgJson);
                        window.ploxworm.log('unknown data 2: ' + msgJson.type);
                    }
                };
                ws.onclose = function (evt) {
                    window.ploxworm.log("Connection closed!");
                    gameRunning = false;
                };
                ws.onerror = function (evt) {
                    window.ploxworm.log("error!");
                    gameRunning = false;
                };
            } else {
                // The browser doesn't support WebSocket
                alert("WebSocket NOT supported by your Browser! GTFO!");
            }
        }

        function sendGameRequest() {
            window.ploxworm.log("Sendings game request!");

            var matchRequest = {};
            var data = {};
            matchRequest.type = "match_request";
            matchRequest.data = data;
            var gameType = $("#game-type").val();
            var level = $("#level").val();
            data.game_type = gameType;
            data.level = level;
            ws.send(JSON.stringify(matchRequest));
        }

        function saveMatchData(jsonData) {
//            window.ploxworm.log('saveMatchData your_number: ' + jsonData.your_number);
            match = jsonData;
            renderBoard();
        }

        function render(jsonData) {
//            console.log("render: " + jsonData);

            //TODO draw level on another canvas or something?
            context.clearRect(0, 0, 800, 800);
            renderFrame(jsonData);
            renderBoard();
        }

        function renderFrame(jsonData) {
            try {
                var worms = jsonData.worms;
                if (worms) {
                    $.each(worms, function (wormIndex, value) {
                        context.beginPath();
                        var lines = this;
                        var lastX;
                        var lastY;
                        $.each(this, function (index) {
                            // move context if this is the first step or if we just crossed a border
                            if (index === 0 || Math.abs(lastX - this.x) > 100 || Math.abs(lastY - this.y) > 100) {
                                context.moveTo(this.x, this.y);
                            } else {
                                context.lineTo(this.x, this.y);
                            }

                            if (index + 1 === lines.length && wormIndex === match.your_number) {
                                headX = this.x;
                                headY = this.y;
                            }
                            lastX = this.x;
                            lastY = this.y;
                        });
                        context.stroke();
                    });
                }
            } catch (e) {
                window.ploxworm.log("error in renderFrame: " + e.stack);
            }
        }

        function renderBoard() {
            try {
                var obstacles = match.obstacles;
                if (obstacles) {
                    context.beginPath();
                    $.each(obstacles, function () {
                        if (this.type === 'rectangle') {
                            context.rect(this.left, this.top, this.right - this.left, this.bottom - this.top);
                        } else if (this.type === 'circle') {
                            context.arc(this.x, this.y, this.radius, 0, 2 * Math.PI, false);
                        } else {
                            console.log('wtf unknown type: ' + this.type);
                        }
                    });
                    context.fillStyle = 'gray';
                    context.fill();
                }
            } catch (e) {
                window.ploxworm.log("error in renderBord: " + e.stack);
            }
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
        });

        function startSendingPosition() {
            setTimeout(updateWormDirection, 500);  //TODO time it takes for game to start, actually, or something lol

            function updateWormDirection() {
//                console.log('updateWormDirection');
                var x = mouseX - headX;
                var y = mouseY - headY;

                if (!isNaN(x) && !isNaN(y)) {
                    var directionMessage = {};
                    directionMessage.type = 'direction';
                    directionMessage.data = {};
                    directionMessage.data.x = x;
                    directionMessage.data.y = y;
                    console.log('sending direction: ' + x + ', ' + y);
                    ws.send(JSON.stringify(directionMessage));
                } else {
                    console.log('failed to get direction: ' + mouseX + ', ' + headX);
                }

                if (gameRunning) {
                    setTimeout(updateWormDirection, 50);
                }
            }
        }

        window.ploxworm.log("loaded");
    });

})();