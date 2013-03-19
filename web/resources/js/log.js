(function () {
    "use strict";

//    if (window.blah == null) {
    if (!window.ploxworm) {
        window.ploxworm = {};
    }

    var currentLog = "";
    var textarea = $("#log");

    window.ploxworm.log = function logz(message) {
        currentLog = message + "\n" + currentLog;
        currentLog = currentLog.substr(0, 1024);
        textarea.text(currentLog);
    };

})();