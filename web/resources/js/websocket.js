define(["jquery", "jquery-ui-1.10.2.custom.min", "log"],

    function () {
        "use strict";

        var websocketConstructor = function (game) {

            var websocket = function websocket() {

                var ws;

                websocket.webSocketInit = function webSocketInit() {
                    if ("WebSocket" in window) {
                        window.ploxworm.log("Opening websocket");

                        var webSocketUrl = getWsUrl();
                        window.ploxworm.log("webSocketUrl: " + webSocketUrl);
                        ws = new WebSocket(webSocketUrl);

                        ws.onopen = function () {
                            window.ploxworm.log("Connection open!");
                            sendGameRequest();
                        };

                        ws.onmessage = function (evt) {
                            var received_msg = evt.data;
                            var msgJson = $.parseJSON(received_msg);
                            if (msgJson.type === 'frame') {
                                game.render(msgJson.data);
                            } else if (msgJson.type === 'scoreboard') {
                                game.updateScoreboard(msgJson.data);
                            } else if (msgJson.type === 'match') {
                                //currently this is our "game started" signal
                                game.startGame(msgJson.data);
                            } else if (msgJson.type === 'end_round') {
                                game.endRound(msgJson.data);
                            } else if (msgJson.type === 'death') {
                                game.death(msgJson.data);
                            } else if (msgJson.type === 'put_in_queue') {
                                game.putInQueue(msgJson.data);
                            } else if (msgJson.type === 'show_title') {
                                game.showTitle(msgJson.data);
                            } else if (msgJson.type === 'hide_title') {
                                game.hideTitle();
                            } else if (msgJson.type === 'show_message') {
                                game.showMessage(msgJson.data);
                            } else if (msgJson.type === 'hide_message') {
                                game.hideMessage();
                            } else {
                                window.ploxworm.log('unknown data: ' + msgJson);
                                window.ploxworm.log('unknown data 2: ' + msgJson.type);
                            }
                        };
                        ws.onclose = function (evt) {
                            window.ploxworm.log("Connection closed!");
                            game.gameRunning = false;
                        };
                        ws.onerror = function (evt) {
                            window.ploxworm.log("error!");
                            game.gameRunning = false;
                        };
                    } else {
                        // The browser doesn't support WebSocket
                        alert("WebSocket NOT supported by your Browser! GTFO!");
                    }
                };

                websocket.prepareGame = function prepareGame() {
                    if (!ws || ws.readyState !== 1) {
                        if (ws) {
                            window.ploxworm.log("websocket readystate: " + ws.readyState);
                        } else {
                            window.ploxworm.log("websocket: " + ws);
                        }
                        websocket.webSocketInit();
                    } else {
                        sendGameRequest();
                    }
                };

                websocket.sendPosition = function sendPosition(x, y, scaleX, scaleY) {
                    var directionMessage = {};
                    directionMessage.type = 'direction';
                    directionMessage.data = {};
                    directionMessage.data.x = x / scaleX;
                    directionMessage.data.y = y / scaleY;
                    ws.send(JSON.stringify(directionMessage));
                };

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
                    data.player_name = $("#player_name").val();
                    data.winning_message = $("#winning_message").val();
                    ws.send(JSON.stringify(matchRequest));
                }

                function getPort() {
                    if (typeof location.port !== "undefined" && location.port !== "") {
                        window.ploxworm.log("found port lol: \"" + location.port + "\"");
                        return location.port;
                    } else if (location.protocol === 'http') {
                        return 80;
                    } else if (location.protocol === 'https') {
                        return 443;
                    } else if (typeof location.port === "undefined") {
                        window.ploxworm.log("Port was undefined! Going with 8080!");
                        return 8080;
                    } else if (location.port === "") {
                        window.ploxworm.log("Port was \"\"! Going with 8080!");
                        return 8080;
                    } else {
                        window.ploxworm.log("FUCK! No port found! Going with 8080!");
                        return 8080;
                    }
                }

                function getWsUrl() {
                    // ugly hax to get around that I failed to forward websockets through apache2
                    if (window.location.hostname === "ploxworm.com") {
                        return 'ws://' + window.location.hostname + ':' + getPort() + '/PloxWormWeb/chat';
                    } else {
                        return 'ws://' + window.location.hostname + ':' + getPort() + '/chat';
                    }
                }

                websocket.webSocketStop = function webSocketStop() {
                    window.ploxworm.log("webSocketStop");
                    if (ws) {
                        ws.close();
                    }
                };

                console.log("websocket loaded 1");
                window.ploxworm.log("websocket loaded 1");

                return websocket;
            };  //websocket
            return websocket();
        };  //constructor
        console.log("websocket loaded 2");
        window.ploxworm.log("websocket loaded 2");
        return websocketConstructor;
    }
);

