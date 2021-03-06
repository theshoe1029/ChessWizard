const B_PAWN = "&#9823";
const B_KNIGHT = "&#9822";
const B_BISHOP = "&#9821";
const B_ROOK = "&#9820";
const B_QUEEN = "&#9819";
const B_KING = "&#9818";

const W_PAWN = "&#9817";
const W_KNIGHT = "&#9816";
const W_BISHOP = "&#9815";
const W_ROOK = "&#9814";
const W_QUEEN = "&#9813";
const W_KING = "&#9812";

const upgradeBox = document.getElementById("upgrades");
const pieceNames = document.getElementById("pieceNames");
const turnLabel = document.getElementById("turnLabel");
const thinkingLabel = document.getElementById("thinkingLabel");
const alertLabel = document.getElementById("alertLabel");
const restart = document.getElementById("restart");
const start = document.getElementById("start");
const retry = document.getElementById("retry");

const PVP_MODE = false;
var board;
var turn;
var dots;
var retryCallback;
var serverDisconnected = true;
var botTurn = false;
var gameOver = false;
var startPos = null;
var endPos = null;
var promotion = "";

window.onresize = function(){ location.reload(); }

$(document).ready(function () {
    if ($(window).width() <= 600) {
        var cw = $('.chessboard').width();
        $('.chessboard').css({'height':cw+2+'px'});
    }
    setupDeselect();
    resetGraphics();
    if (getCookie("uuid") === "") startGame();
    else getGame();
});

function setupDeselect() {
    $("body").click((e) => {
        if (e.target.id === "chessboard" || $(e.target).parents("#chessboard").length) {
        } else {
            clearStartPos();
        }
    });
}

function resetGraphics() {
    turnLabel.style.display = "block";
    turnLabel.innerHTML = "WHITE TURN";

    alertLabel.style.margin = 0;

    thinkingLabel.style.display = "none";
    upgradeBox.style.display = "none";
    pieceNames.style.display = "none";
    alertLabel.style.display = "none";
    restart.style.display = "none";
    start.style.display = "none";
    retry.style.display = "none";
}

function updateBoard(game) {
    const serverBoard = game.engine.board;
    board = _.map(serverBoard.state, row => _.map(row, piece => piece.notation));
    drawBoard();

    if (serverBoard.gameOver) {
        gameOver = true;
        turnLabel.style.display = "none";
        alertLabel.style.display = "block";
        restart.style.display = "block";
        alertLabel.innerHTML = "CHECKMATE";
        alertLabel.style["margin-top"] = "15px";
    } else if (serverBoard.inCheck) {
        alertLabel.style.display = "block";
        alertLabel.innerHTML = "CHECK";
        updateTurn(serverBoard);
    } else {
        alertLabel.style.display = "none";
        updateTurn(serverBoard);
    }
}

function updateTurn(serverBoard) {
    turnLabel.innerHTML = serverBoard.turn + " TURN";
    turn = serverBoard.turn;
}

function initBoardColor(game) {
    const color = game.playerColor;
    if (color === "WHITE") playerWhiteBoard();
    else {
        playerBlackBoard();
        botMove();
    }
}

function updateRecord(game) {
    document.getElementById("wins").innerHTML = game.record[0] == 1 ? "1 WIN" : game.record[0] + " WINS";
    document.getElementById("losses").innerHTML = game.record[1] == 1 ? "1 LOSS" : game.record[0] + " LOSSES";
}

function playerWhiteBoard() {
    for (var x = 0; x < 8; x++) {
        for (var y = 0; y < 8; y++) {
            var boardSquare = document.getElementById(numToLetter(7 - y) + (7 - x + 1));
            boardSquare.id = numToLetter(y) + (x + 1);
        }
    }
}

function playerBlackBoard() {
    for (var x = 0; x < 8; x++) {
        for (var y = 0; y < 8; y++) {
            var boardSquare = document.getElementById(numToLetter(y) + (x + 1));
            boardSquare.id = numToLetter(7 - y) + (7 - x + 1);
        }
    }
}

function drawBoard() {
    for (var x = 0; x < 8; x++) {
        for (var y = 0; y < 8; y++) {
            var boardSquare = document.getElementById(numToLetter(y) + (x + 1));
            var piece = board[x][y];
            if (piece === "-")
                boardSquare.innerHTML = "";
            else {
                if (piece === "P")
                    boardSquare.innerHTML = W_PAWN;
                if (piece === "N")
                    boardSquare.innerHTML = W_KNIGHT;
                if (piece === "R")
                    boardSquare.innerHTML = W_ROOK;
                if (piece === "B")
                    boardSquare.innerHTML = W_BISHOP;
                if (piece === "Q")
                    boardSquare.innerHTML = W_QUEEN;
                if (piece === "K")
                    boardSquare.innerHTML = W_KING;
                if (piece === "p")
                    boardSquare.innerHTML = B_PAWN;
                if (piece === "n")
                    boardSquare.innerHTML = B_KNIGHT;
                if (piece === "r")
                    boardSquare.innerHTML = B_ROOK;
                if (piece === "b")
                    boardSquare.innerHTML = B_BISHOP;
                if (piece === "q")
                    boardSquare.innerHTML = B_QUEEN;
                if (piece === "k")
                    boardSquare.innerHTML = B_KING;
            }
        }
    }
}

function promote(piece) {
    promotion = piece.id;
    playerMove();
    hidePromotion();
}

function showPromotion() {
    const knight = document.getElementById("n");
    const bishop = document.getElementById("b");
    const rook = document.getElementById("r");
    const queen = document.getElementById("q");
    document.getElementById(startPos).innerHTML = "";
    if ("WHITE" === "WHITE") {
        document.getElementById(endPos).innerHTML = W_PAWN;
        pieceNames.style.display = "block";
        upgradeBox.style.display = "block";
        turnLabel.style.display = "none";
        alertLabel.style.display = "none";
        knight.innerHTML = W_KNIGHT;
        bishop.innerHTML = W_BISHOP;
        rook.innerHTML = W_ROOK;
        queen.innerHTML = W_QUEEN;
    } else {
        document.getElementById(endPos).innerHTML = B_PAWN;
        pieceNames.style.display = "block";
        upgradeBox.style.display = "block";
        turnLabel.style.display = "none";
        alertLabel.style.display = "none";
        knight.innerHTML = B_KNIGHT;
        bishop.innerHTML = B_BISHOP;
        rook.innerHTML = B_ROOK;
        queen.innerHTML = B_QUEEN;
    }
}

function hidePromotion() {
    promotion = "";
    turnLabel.style.display = "block";
    pieceNames.style.display = "none";
    upgradeBox.style.display = "none";
}

function numToLetter(number) {
    if (number === 0)
        return "a"
    else if (number === 1)
        return "b"
    else if (number === 2)
        return "c"
    else if (number === 3)
        return "d"
    else if (number === 4)
        return "e"
    else if (number === 5)
        return "f"
    else if (number === 6)
        return "g"
    else if (number === 7)
        return "h"
}

function letterToNum(letter) {
    if (letter === "a")
        return 0;
    else if (letter === "b")
        return 1;
    else if (letter === "c")
        return 2;
    else if (letter === "d")
        return 3;
    else if (letter === "e")
        return 4;
    else if (letter === "f")
        return 5;
    else if (letter === "g")
        return 6;
    else if (letter === "h")
        return 7;
}

function getPiece(id) {
    const x = letterToNum(id[0]);
    const y = parseInt(id[1] - 1);
    return board[y][x];
}

function isValidPiece(square) {
    const piece = getPiece(square.id);
    if (piece === "-") return true;
    if (piece === piece.toUpperCase()) {
        return turn === "WHITE";
    }
    if (piece === piece.toLowerCase()){
        return turn === "BLACK";
    }
}

function setCookie(name, value, days) {
    var d = new Date();
    d.setTime(d.getTime() + (days*24*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = name + "=" + value + ";" + expires + ";";
}

function getCookie(cookieName) {
    var name = cookieName + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function clicked(square) {
    if (!gameOver && !botTurn && !serverDisconnected) {
        var pos = square.id;
        if (startPos == null && isValidPiece(square) && square.innerHTML !== "") {
            setStartPos(square, pos);
        } else if (startPos === pos) {
            clearStartPos();
        } else if (startPos != null && isValidPiece(square) && square.innerHTML !== "") {
            clearStartPos();
            setStartPos(square, pos);
        } else if (startPos != null) {
            endPos = pos;
            validate();
        }
    }
}

function setCursor(square) {
    if (((square.innerHTML === "" || !isValidPiece(square)) && startPos == null) || gameOver || botTurn || serverDisconnected)
        square.style.cursor = "default";
    else
        square.style.cursor = "pointer";
}

function setStartPos(square, pos) {
    startPos = pos;
    square.style.background = "#dbdbdb";
}

function clearStartPos() {
    if (startPos !== null) {
        const square = document.getElementById(startPos);
        if (square.className === "white") square.style.backgroundColor = "#ffffff";
        else square.style.backgroundColor = "#ababab";
        startPos = null;
    }
}

function displayThinkingMessage(message) {
    thinkingLabel.style.display = "block";
    thinkingLabel.innerHTML = message;
    var i = 0;
    dots = setInterval(() => {
        if (i % 6 === 0) thinkingLabel.innerHTML = message;
        else thinkingLabel.innerHTML += ".";
        i++;
    }, 1000);
}

function startGame() {
    axios({
        method: 'post',
        url: '/start',
        data: {}
    })
        .then(res => {
            handleServerConnect();
            setCookie("uuid", res.data.uuid, 1);
            resetGraphics();
            initBoardColor(res.data.game);
            updateBoard(res.data.game);
            updateRecord(res.data.game);
        }).catch(getErrHandler(startGame));
}

function restartGame() {
    axios({
        method: 'post',
        url: '/restart',
        data: {
            "uuid": getCookie("uuid")
        }
    })
        .then(res => {
            handleServerConnect();
            gameOver = false;
            resetGraphics();
            initBoardColor(res.data.game);
            updateBoard(res.data.game);
            updateRecord(res.data.game);
        }).catch(getErrHandler(restartGame));
}

function getGame() {
    displayThinkingMessage("LOADING");
    axios({
        method: 'post',
        url: '/get',
        data: {
            "uuid": getCookie("uuid")
        }
    })
        .then(res => {
            handleServerConnect();
            thinkingLabel.style.display = "none";
            clearInterval(dots);
            resetGraphics();
            res.data.game.playerColor === "WHITE" ? playerWhiteBoard() : playerBlackBoard();
            updateBoard(res.data.game);
            updateRecord(res.data.game);
        }).catch(getErrHandler(getGame));
}

function validate() {
    axios({
        method: 'post',
        url: '/validate',
        data: {
            "uuid": getCookie("uuid"),
            "move": startPos + endPos
        }
    })
        .then(res => {
            handleServerConnect();
            if (res.data.valid === true) {
                alertLabel.style.display = "none";
                if (res.data.promotion) showPromotion();
                else playerMove();
            } else {
                alertLabel.innerHTML = "INVALID MOVE";
                alertLabel.style.display = "block";
                clearStartPos();
            }
        }).catch(getErrHandler(validate));
}

function playerMove() {
    axios({
        method: 'post',
        url: '/move',
        data: {
            "uuid": getCookie("uuid"),
            "move": startPos + endPos + promotion
        }
    })
        .then(res => {
            handleServerConnect();
            updateBoard(res.data.game);
            updateRecord(res.data.game);
            clearStartPos();
            if (!PVP_MODE) botMove();
        }).catch(getErrHandler(playerMove));
}

function botMove() {
    botTurn = true;
    displayThinkingMessage("THINKING");
    axios({
        method: 'post',
        url: '/go',
        data: {
            "uuid": getCookie("uuid")
        }
    })
        .then(res => {
            handleServerConnect();
            thinkingLabel.style.display = "none";
            clearInterval(dots);
            botTurn = false;
            updateBoard(res.data.game);
            updateRecord(res.data.game);
        }).catch(getErrHandler(botMove));
}

function getErrHandler(callback) {
    return err => {
        if (err.response) handleInvalidId();
        else if (err.request) handleServerDisconnect(callback);
    }
}

function handleServerConnect() {
    serverDisconnected = false;
    alertLabel.style.display = "none";
    retry.style.display = "none";
}

function handleServerDisconnect(callback) {
    serverDisconnected = true;
    retryCallback = callback;
    alertLabel.innerHTML = "SERVER DISCONNECTED";
    alertLabel.style.display = "block";
    retry.style.display = "block";
    if (dots !== undefined) {
        thinkingLabel.style.display = "none";
        clearInterval(dots);
    }
}

function handleInvalidId() {
    handleServerConnect();
    setCookie("uuid", "", 1);
    alertLabel.innerHTML = "INVALID GAME ID";
    alertLabel.style.display = "block";
    start.style.display = "block";
    if (dots !== undefined) {
        thinkingLabel.style.display = "none";
        clearInterval(dots);
    }
}
