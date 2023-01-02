var oracleUrl = "historicdata/";

function toggleColor(element, classAttribute) {
    $(element).toggleClass(classAttribute);
}

function trimAndStripString(str) {
    var result = str.trim();
    result = result.replaceAll("%20", " ");
    result = trimChar(result, ')');
    result = trimChar(result, '(');

    if (result.indexOf("(") > 0) {
        result = result.substring(result.indexOf("(") + 1);
    }
    return result;
}


function trimChar(string, charToRemove) {
    while (string.charAt(0) == charToRemove) {
        string = string.substring(1);
    }

    while (string.charAt(string.length - 1) == charToRemove) {
        string = string.substring(0, string.length - 1);
    }

    return string;
}

$(document).ready(function () {
    console.log("history data page");
    var queryString = window.location.search;

    filterAndDrawBets(queryString);


    $("#filter").click(function () {
        var player1Title = $("#player1Title").val();
        var player2Title = $("#player2Title").val();
        var sport = $("#sportSelect").val();
        console.log(player1Title + "|" + player2Title + "|" + sport + "|")

        var queryString = "player1Title=" + player1Title + "&player2Title=" + player2Title + "&sport=" + sport;
        console.log(queryString);
        filterAndDrawBets(queryString);
    });

    function filterAndDrawBets(params) {
        const rootElem = $("#historic");
        rootElem.empty();
        if (!params.startsWith("?")) {
            params = "?" + params;
        }
        params = params.replace("%20", " ");
        const players = [];
        $.get(oracleUrl + params, function (data) {
            var historicData = data.data;

            for (var titleDateTimeKey in historicData) {
                var itemString = "<div class='betInfo'><b>" + titleDateTimeKey + "</b>";
                var boldTitle = "";
                var player1Title = "";
                var player2Title = "";

                if (titleDateTimeKey.indexOf(",") >= 0) {
                    var titlePart = titleDateTimeKey.split(",");

                    var splitter = "-";
                    if (titlePart[0].indexOf(" - ") >= 0) {
                        splitter = " - ";
                    }

                    if (titlePart[0].indexOf(splitter) >= 0) {
                        player1Title = titlePart[0].split(splitter)[0];
                        if (player1Title.startsWith("(")) {
                            //this situation is because titleDateTikeKey looks like '(Dubyna Ihor - Maltsev Denys,2022-02-13T23:29:58.781)'
                            player1Title = player1Title.substring(1);
                        }
                        player2Title = titlePart[0].split(splitter)[1];

                        boldTitle = "<span ondblclick='toggleColor(this, \"red\")' onclick='toggleColor(this, \"green\")'>"
                            + "<a href='?player1Title=" + trimAndStripString(player1Title) + "'>link</a>"
                            + player1Title + "</span>" +
                            "-<span ondblclick='toggleColor(this, \"red\")' onclick='toggleColor(this, \"green\")'>"
                            + "<a href='?player2Title=" + trimAndStripString(player2Title) + "'>link</a>"
                            + player2Title + "</span>";
                        boldTitle += titleDateTimeKey.split(",")[1];
                    }
                }

                if (boldTitle.length > 0) {
                    itemString = "<div class='betInfo'><b>" + boldTitle + "</b>";
                }

                var lastScore = "";
                for (var syncItem in historicData[titleDateTimeKey]) {
                    if (itemString.indexOf("|") < 0) {
                        //retrieve initial odds:
                        itemString += "<span class='bolder'>" + historicData[titleDateTimeKey][syncItem].coef1 + "," +
                            historicData[titleDateTimeKey][syncItem].coef2 + "</span>";
                    }
                    //here add scores
                    lastScore = historicData[titleDateTimeKey][syncItem].score;
                    itemString += "<span>" + historicData[titleDateTimeKey][syncItem].score + "| </span>";
                }

                itemString += "</div>";
                rootElem.append(itemString);
                if (!containsPlayer(players, player1Title)) {
                    players.push(player1Title + gameColor(lastScore, 1));
                } else {
                    for (let i = 0; i < players.length; i++) {
                        if(players[i].startsWith(player1Title)){
                            players[i] = players[i] + gameColor(lastScore, 1);
                        }
                    }
                }
                if (!containsPlayer(players, player2Title)) {
                    players.push(player2Title + gameColor(lastScore, 2));
                } else {
                    for (let i = 0; i < players.length; i++) {
                        if(players[i].startsWith(player2Title)){
                            players[i] = players[i] + gameColor(lastScore, 2);
                        }
                    }
                }
            }
            drawPlayers(players);
        });

    }

    function drawPlayers(players) {
        console.log("draw players colors" + players)
        var counter = 1;
        $("#players").empty();
        for (let player of players) {
            $("#players").append("<div>" + counter + ")" + player + "</div>")
            counter++;
        }
    }

    function containsPlayer(players, title) {
        for (let player of players) {
            if (player.startsWith(title)) {
                return true;
            }
        }
        return false;
    }

    /**
     * score in form '3 11 8 4 6 10 1 2'
     */
    function gameColor(score, playerPosition) {
        var scoreParts = score.split(" ");
        scoreParts = scoreParts.map((elem)=> parseInt(elem));

        var length = scoreParts.length;
        if(length < 6) {
            return "<b>■</b>";
        }
        if (scoreParts[length - 1] == "2" && scoreParts[length - 3] >= 8 &&
            scoreParts[length - 3] > scoreParts[length - 4]) {
            //situation where player2 WIN
            if (playerPosition == 2) {
                return "<b class='green-color'>■</b>";
            } else {
                return "<b class='red-color'>■</b>";
            }
        }

        if (scoreParts[length - 2] == "2" && scoreParts[length - 4] >= 8 &&
            scoreParts[length - 4] > scoreParts[length - 3]) {
            //situation where player1 WIN
            if (playerPosition == 1) {
                return "<b class='green-color'>■</b>";
            } else {
                return "<b class='red-color'>■</b>";
            }
        }
        return "<b>■</b>";
    }
})
