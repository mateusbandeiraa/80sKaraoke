let lyrics = [
    { word: "So", duration: 2.2, wait: 1 },
    { word: "long,", duration: 0.9 },
    { word: "I've", duration: 0.3, wait: 0.9 },
    { word: "been", duration: 0.2, wait: 0.1 },
    { word: "looking", duration: 1 },
    { word: "too", duration: 0.2 },
    { word: "hard", duration: 0.5, lineBreak: true },
    { word: "I've", duration: 0.2 },
    { word: "been", duration: 0.5 },
    { word: "waiting", duration: 1 },
    { word: "too", duration: 0.5 },
    { word: "long", duration: 0.5 },
];

document.addEventListener("DOMContentLoaded", function () {
    drawLines();
    setWipings();
});

function drawLines() {
    let lineHTML = "";
    let count = 0;
    let needsSpaceCharacter = false;
    for (let element of lyrics) {
        let displayText = element.word;
        // let displayText = (needsSpaceCharacter ? " " : "") + element.word;
        lineHTML += `<span class="word-outer" data-word-id="${count++}">`;
        lineHTML += `<span class="word-inner" data-text="${displayText}" data-duration="${element.duration}">${displayText}</span>`;
        lineHTML += '</span>';


        if (element.lineBreak) {
            drawSingleLine(lineHTML)
            lineHTML = "";
            needsSpaceCharacter = false;
        } else {
            needsSpaceCharacter = true;
        }
    }
    if (lineHTML != "") {
        drawSingleLine(lineHTML);
    }
}

function drawSingleLine(lineHTML) {
    // let outerLineHTML = `<div class="line outer">${lineHTML}</div>`;
    let lineNode = document.createElement("div");
    lineNode.classList.add("line-outer");
    lineNode.innerHTML = lineHTML;
    document.getElementById("lyrics-container").appendChild(lineNode);
}

function setWipings() {
    let currentTime = 0;
    let count = 0;
    for (let element of lyrics) {
        let htmlNode = document.querySelector(`[data-word-id="${count}"] .word-inner`);
        if (element.wait) {
            currentTime += element.wait;
        }
        setTimeout(() => {
            htmlNode.setAttribute('style', `transition-duration: ${element.duration*1000}ms`);
            htmlNode.classList.add("active");
        }, currentTime * 1000);
        currentTime += element.duration;

        count++;
    }
}