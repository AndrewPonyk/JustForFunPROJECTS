String.prototype.format = function () {
    var args = arguments;
    return this.replace(/{(\d+)}/g, function (match, number) {
        return typeof args[number] != 'undefined'
            ? args[number]
            : match
            ;
    });
};

random = function (max) {
    return Math.floor(Math.random(100) * max)
}

randomArray = function (max, length) {
    var result = [];
    for (var i = 0; i < length; i++) {
        result.push(random(max));
    }

    return result;
}

sumArray = function (arr) {
    return arr.reduce(function (a, b) {
        return a + b;
    }, 0)
}