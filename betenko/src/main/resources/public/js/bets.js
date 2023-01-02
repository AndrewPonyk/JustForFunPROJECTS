console.log("betenko..");
var footballHttp = "https://jsonbox.io/box_a689710efb7c6c3a93bb/5fcd5a6a8abbd4001773c598";
var tennisHttp = "https://jsonbox.io/box_a689710efb7c6c3a93bb/5fcfe96bbdbb73001796890c";

var footballHttpV2 = "https://jsonstorage.net/api/items/bed7ca77-a12d-403c-b5c8-09ca3a04d4e6";
var tennisHttpV2 = "https://jsonstorage.net/api/items/15d42a34-4677-4cfb-8a9b-c433669d0ddc";


var historyUrl = "player/sortedHistory";

$(document).ready(function () {
    $("#history-panel").hide();
    $("#table-tennis-panel").hide();
    $("#bet-placing-panel").hide();
    $("#show-history-btn").click(function (e) {
        $("#history-panel").show();
        retrieveDrawHistory();
    });

    $("#hide-history-btn").click(function (e) {
        $("#history-panel").hide();
    });

    $("#show-football-panel").click(function () {
        $("#football-panel").show();
        $("#table-tennis-panel").hide();
    })

    $("#show-table-tennis").click(function () {
        $("#football-panel").hide();
        $("#table-tennis-panel").show();
    })

    setInterval(retrieveDrawBets, 3000);
})

function fillFootball() {
    $.get(footballHttpV2, function (data) {
        var footballData = JSON.stringify(data.value);
        var footballDataArr = footballData.split(",");

        $("#football-bets-table").find("tr:gt(0)").remove();

        for (var elem of footballDataArr) {
            var betParts = elem.split(";;");
            var title = betParts[0].trim();
            if (title.indexOf("\"") >= 0) {
                title = title.replace("\"", "");
            }
            var score = "";
            if (isCharNumber(title[title.length - 1]) && isCharNumber(title[title.length - 2])) {
                score = title.substring(title.length - 2, title.length);
                title = title.substring(0, title.length - 2);
            }

            $("#football-bets-table").append("<tr><td>" + title + "</td>" +
                "<td>" + score + "</td>" +
                "<td class='active-odds' attr='1' onclick='onOddsClick(this)'>" + betParts[2] + "</td>" +
                "<td class='active-odds' attr='2' onclick='onOddsClick(this)'>" + betParts[3] + "</td></tr>");
        }
    });
}

function fillTableTennis() {
    $.get(tennisHttpV2, function (data) {
        var tableTennisData = JSON.stringify(data.value);
        var tableTennisArrStrings = tableTennisData.split(",");
        var tableTennis2dArr = [];
        for (var elem of tableTennisArrStrings) {
            tableTennis2dArr.push(elem.split(";;"));
        }

        $("#table-tennis-bets-table").find("tr:gt(0)").remove();
        //group table tennis items by key '4' - which is competition
        var grouppedByCompetition = groupBy(tableTennis2dArr, 4);

        for (var competition in grouppedByCompetition) {
            if (competition.toLowerCase().indexOf("czech") >= 0 ||
                competition.toLowerCase().indexOf("liga") >= 0
            ) {
                continue;
            }

            $("#table-tennis-bets-table").append("<tr><td>" + competition + "</td>" +
                "<td></td>" +
                "<td></td>" +
                "<td></td></tr>");

            for (var competionsItem of grouppedByCompetition[competition]) {
                $("#table-tennis-bets-table").append("<tr><td>" + competionsItem[0] + "</td>" +
                    "<td>" + competionsItem[1] + "</td>" +
                    "<td class='active-odds' attr='1' onclick='onOddsClick(this)'>" + competionsItem[2] + "</td>" +
                    "<td class='active-odds' attr='2' onclick='onOddsClick(this)'>" + competionsItem[3] + "</td></tr>");
            }
        }
    });
}

function retrieveDrawBets() {
    fillPlayerInfo();
    fillFootball();
    //fillTableTennis();
}

function fillPlayerInfo() {
    $.get(historyUrl, function (data) {
        $("#user-sum").text(parseFloat(data.sum).toFixed(2));
        $("#user-name").text(data.name);
    });
}

function retrieveDrawHistory() {
    $.get(historyUrl, function (data) {
        var playerHistory = data.betItems;
        $("#history-table").find("tr:gt(0)").remove();
        for (var index in playerHistory) {
            var colorClass = "";
            var returned = "";
            if (playerHistory[index].status == false) {
                colorClass = "red";
                returned = 0;
            } else if (playerHistory[index].status == true) {
                colorClass = "lime";
                returned = Math.round(playerHistory[index].odd * playerHistory[index].sum * 100) / 100;
            }

            $("#history-table").append("<tr><td>" + playerHistory[index].created + "</td>" +
                "<td>" + playerHistory[index].title + "</td>" +
                "<td>" + playerHistory[index].chose + "</td>" +
                "<td>" + playerHistory[index].odd + "</td>" +
                "<td>" + playerHistory[index].sum + "</td>" +
                "<td class='" + colorClass + "'>" + returned + "</td>" +
                "<td>" + playerHistory[index].finalResult + "</td>" +
                "</tr>");
        }
    });
}

function onOddsClick(e) {
    var elem = $(e);
    var offset = elem.offset();
    elem.toggleClass("selected-cell");
    $("#bet-placing-panel").show();
    $("#bet-placing-panel").css({top: offset.top, left: offset.left + 130});

    var eventTitle = elem.parent().children(":first").text();
    $("#bet-title").val(eventTitle);
    $("#bet-chose").val(elem.attr("attr"));
    $("#bet-odd").val(elem.text());
}

var groupBy = function (xs, key) {
    return xs.reduce(function (rv, x) {
        (rv[x[key]] = rv[x[key]] || []).push(x);
        return rv;
    }, {});
};

function isCharNumber(c) {
    return c >= '0' && c <= '9';
}

function placeBet() {
    var betItem = {};
    betItem.title = $("#bet-title").val();
    betItem.chose = $("#bet-chose").val();
    betItem.sum = $("#bet-sum").val();
    betItem.odd = $("#bet-odd").val();
    $.ajax("/placeBet/", {
        data: JSON.stringify(betItem),
        contentType: 'application/json',
        type: 'POST'
    });
}

function refreshUserInfo() {

}