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

const PVP_MODE = false;
var uuid;
var board;
var turn;
var botTurn = false;
var gameOver = false;
var startPos = null;
var endPos = null;
var promotion = "";

$(document).ready(function () {
    setupDeselect();
    resetGraphics();
    startGame();
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
}

function updateBoard(serverBoard) {
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

function setCursor(square) {
    if (((square.innerHTML === "" || !isValidPiece(square)) && startPos == null) || gameOver || botTurn)
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

function startGame() {
    axios({
        method: 'post',
        url: '/start',
        data: {}
    })
        .then(res => {
            uuid = res.data.uuid;
            updateBoard(res.data.board);
            resetGraphics();
        })
        .catch(err => console.log(err));
}

function restartGame() {
    axios({
        method: 'post',
        url: '/restart',
        data: {
            "uuid": uuid
        }
    })
        .then(res => {
            gameOver = false;
            updateBoard(res.data.board);
            resetGraphics();
        })
        .catch(err => console.log(err));
}

function clicked(square) {
    if (!gameOver && !botTurn) {
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

function validate() {
    axios({
        method: 'post',
        url: '/validate',
        data: {
            "uuid": uuid,
            "move": startPos + endPos
        }
    })
        .then(res => {
            if (res.data.valid === true) {
                alertLabel.style.display = "none";
                if (res.data.promotion) showPromotion();
                else playerMove();
            } else {
                alertLabel.innerHTML = "INVALID MOVE";
                alertLabel.style.display = "block";
                clearStartPos();
            }
        })
        .catch(err => console.log(err));
}

function playerMove() {
    axios({
        method: 'post',
        url: '/move',
        data: {
            "uuid": uuid,
            "move": startPos + endPos + promotion
        }
    })
        .then(res => {
            updateBoard(res.data.board);
            clearStartPos();
            if (!PVP_MODE) botMove();
        })
        .catch(err => console.log(err));
}

function botMove() {
    botTurn = true;
    thinkingLabel.style.display = "block";
    thinkingLabel.innerHTML = "THINKING";
    var i = 0;
    var dots = setInterval(() => {
        if (i % 6 === 0) thinkingLabel.innerHTML = "THINKING"
        else thinkingLabel.innerHTML += ".";
        i++;
    }, 1000)

    axios({
        method: 'post',
        url: '/go',
        data: {"uuid": uuid}
    })
        .then(res => {
            botTurn = false;
            thinkingLabel.style.display = "none";
            updateBoard(res.data.board);
            clearInterval(dots);
        })
        .catch(err => console.log(err))
}
