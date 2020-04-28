const track = {
    title: "Waiting for a girl like you",
    artist: "Foreigner",
    year: "1981",
    lyrics: {
        lines: [
            {
                startAt: 29,
                remain: 1,
                words: [
                    { content: "...", duration: 2, wait: 0.7 },
                    { content: "So", duration: 3 },
                    { content: "long,", duration: 0.9 },
                    { content: "I've", duration: 0.3, wait: 1 },
                    { content: "been", duration: 0.2, wait: 0.1 },
                    { content: "looking", duration: 0.8 },
                    { content: "too", duration: 0.2 },
                    { content: "hard", duration: 0.5 },
                ]
            },
            {
                startAt: 37,
                remain: 1,
                words: [
                    { content: "I've", duration: 0.2, wait: 2 },
                    { content: "been", duration: 0.5 },
                    { content: "waiting", duration: .6 },
                    { content: "too", duration: 0.5 },
                    { content: "long", duration: 0.8 },
                ]
            },
            {
                startAt: 41,
                remain: 0.5,
                words: [
                    { content: "Sometimes", duration: 0.2, wait: 1.3 },
                    { content: "I", duration: 0.3 },
                    { content: "don't", duration: 0.3 },
                    { content: "know", duration: 0.5 },
                    { content: "what", duration: 0.8 },
                    { content: "I", duration: 0.5 },
                    { content: "will", duration: 0.5 },
                    { content: "find,", duration: 0.5 },
                ]
            },
            {
                startAt: 46,
                remain: 1,
                words: [
                    { content: "I", duration: 0.2, wait: 1 },
                    { content: "only", duration: 0.3 },
                    { content: "know", duration: 0.5 },
                    { content: "it's", duration: 0.5 },
                    { content: "a", duration: 0.5 },
                    { content: "matter", duration: 0.5 },
                    { content: "of", duration: 0.5 },
                    { content: "time", duration: 0.5 },
                ]
            },
        ]
    }
};

/* PAGE SETUP */
let trackPlayer;
document.addEventListener("DOMContentLoaded", function () {
    drawHeader(track);
    setControlsListeners();
    setInterval(update, 100);
    trackPlayer = new Audio('assets/songs/song1.mp3');
    trackPlayer.play();
});


function drawHeader(track) {
    document.getElementById("track-title").textContent = track.title;
    document.getElementById("track-artist").textContent = track.artist;
    document.getElementById("track-year").textContent = track.year;
}

function setControlsListeners() {
    document.getElementById("button-backwards").addEventListener("click", () => {
        trackPlayer.currentTime = 0;
        update();
    });

    document.getElementById("button-play-pause").addEventListener("click", () => {
        let htmlButtonElement = document.getElementById("button-play-pause");
        if (trackPlayer.paused) {
            trackPlayer.play();
            htmlButtonElement.classList.remove("fa-play");
            htmlButtonElement.classList.add("fa-pause");
        } else {
            trackPlayer.pause();
            htmlButtonElement.classList.remove("fa-pause");
            htmlButtonElement.classList.add("fa-play");
        }
        update();
    });

    document.getElementById("button-forward").addEventListener("click", () => {
        trackPlayer.currentTime = 25;
        update();
    });
}

/* UPDATE METHODS */
function update() {
    let currentTrackTimecode = getCurrentTrackTimecode();
    updateStatus(trackPlayer);
    drawTimecodeIndicator(currentTrackTimecode);
    const visibleLines = getLinesFromTimecode(track, currentTrackTimecode);
    removeOutOfSyncLines(currentTrackTimecode);
    updateLines(track, visibleLines);
    updateWipings(track, visibleLines, currentTrackTimecode);
}

function updateStatus(trackPlayer) {
    let htmlStatusTextElement = document.getElementById("status-text");
    let htmlStatusIconElement = document.getElementById("status-icon");
    if (trackPlayer.paused && htmlStatusTextElement.textContent != "PAUSE") {
        htmlStatusTextElement.textContent = "PAUSE";
        htmlStatusIconElement.setAttribute("data", "assets/icons/pause.svg");
    } else if (!trackPlayer.paused && htmlStatusTextElement.textContent != "PLAY") {
        htmlStatusTextElement.textContent = "PLAY";
        htmlStatusIconElement.setAttribute("data", "assets/icons/play.svg");
    }
}

function updateLines(track, lines) {
    let lineCount = 0;

    let indexOfFirstVisibleLine = track.lyrics.lines.indexOf(lines[0]);
    let currentHTMLLine;
    while (lineCount <= 2 && lineCount < lines.length) {
        let line = lines[lineCount];
        if (isLineCurrentlyDisplayed(line, track)) {
            lineCount++;
            continue;
        }
        currentHTMLLine = (indexOfFirstVisibleLine + lineCount) % 3;

        let lineElement = document.getElementById(`line${currentHTMLLine}`);
        lineElement.innerHTML = "";
        let needsSpaceCharacter = false;
        for (let word of line.words) {
            const outerWordNode = createWordNode(word, line.words.indexOf(word), needsSpaceCharacter);

            if (!needsSpaceCharacter) {
                needsSpaceCharacter = true;
            }
            lineElement.appendChild(outerWordNode);
        }
        lineElement.setAttribute("data-line-start-timecode", line.startAt);
        lineElement.setAttribute("data-line-ending-timecode", getLineEndingTimecode(line));
        lineElement.setAttribute("data-line-id", indexOfFirstVisibleLine + lineCount);
        lineElement.classList.remove("hidden");
        lineCount++;
    }
}

function updateWipings(track, lines, timecode) {
    for (let line of lines) {
        if (line.startAt < timecode) {
            let relativeTime = timecode - line.startAt;
            let wordCount = 0;
            for (let word of line.words) {
                if (word.wait) {
                    relativeTime -= word.wait;
                }

                let percentageSpent = parseInt((relativeTime / word.duration) * 100);
                if (percentageSpent > 100) {
                    percentageSpent = 100;
                }

                relativeTime -= word.duration;


                let lineId = track.lyrics.lines.indexOf(line);
                let style = document.querySelector(`style[data-line-id="${lineId}"][data-word-id="${wordCount}"]`);
                if (!style) {
                    style = document.createElement("style");
                    style.setAttribute("data-line-id", lineId);
                    style.setAttribute("data-word-id", wordCount);
                    style.classList.add("removeWithLine");
                    style.innerHTML = createCSSWithPercentageSpent(lineId, wordCount, percentageSpent);
                    document.querySelector("head").appendChild(style);
                } else {
                    style.innerHTML = createCSSWithPercentageSpent(lineId, wordCount, percentageSpent);
                }
                wordCount++;
            }
        }
    }
}

function removeOutOfSyncLines(timecode) {
    let htmlLines = document.querySelectorAll(".line");
    for (let lineElement of htmlLines) {
        let endingTimecode = lineElement.getAttribute("data-line-ending-timecode");
        let startTimecode = lineElement.getAttribute("data-line-start-timecode");
        if (startTimecode && endingTimecode) {
            startTimecode = parseFloat(startTimecode);
            endingTimecode = parseFloat(endingTimecode);
            if (startTimecode > timecode || endingTimecode < timecode) {
                lineElement.classList.add("hidden");
                lineElement.removeAttribute("data-line-id");
                document.querySelectorAll(`.removeWithLine[data-line-id="${lineElement.getAttribute("data-line-id")}"]`).forEach((element) => {
                    element.remove();
                });
            }
        }
    }
}

/* HELPER METHODS */

function getCurrentTrackTimecode() {
    if (trackPlayer) {
        return trackPlayer.currentTime;
    } else {
        return 0;
    }
}

function drawTimecodeIndicator(timecode) {
    timecode *= 1000;
    let text = new Date(timecode).toISOString().slice(14, 19);
    document.getElementById("timecode-indicator").textContent = text;
}

function getLinesFromTimecode(track, timecode) {
    let allLines = track.lyrics.lines;
    let visibleLines = [];
    for (let line of allLines) {
        if (line.startAt > timecode) {
            continue;
        }
        if (getLineEndingTimecode(line) < timecode) {
            continue;
        }
        visibleLines.push(line);
    }
    return visibleLines;
}

function getTotalLineDuration(line) {
    let duration = 0;
    for (let word of line.words) {
        if (word.wait) {
            duration += word.wait;
        }
        duration += word.duration;
    }
    return duration;
}

function getLineEndingTimecode(line) {
    return line.startAt + getTotalLineDuration(line) + line.remain;
}

function isLineCurrentlyDisplayed(line, track){
    let lineId = getLineId(line, track);
    if (document.querySelector(`.line[data-line-id="${lineId}"]`)){
        return true;
    } else {
        return false;
    }
}

function getLineId(line, track){
    return track.lyrics.lines.indexOf(line);
}

function createCSSWithPercentageSpent(lineId, wordId, percentageSpent) {
    return `.line[data-line-id="${lineId}"] [data-word-id="${wordId}"] .word-inner::before{
        width: ${percentageSpent}% !important;
    }`;
}

function createWordNode(word, wordId, needsSpaceCharacter) {
    let outerWordNode = document.createElement("span");
    outerWordNode.classList.add("word-outer");
    outerWordNode.setAttribute("data-word-id", wordId);

    const innerWordNode = createInnerWordNode(word, needsSpaceCharacter);
    outerWordNode.appendChild(innerWordNode);
    return outerWordNode;
}

function createInnerWordNode(word, needsSpaceCharacter) {
    let displayText, dataAttributeText;
    if (needsSpaceCharacter) {
        displayText = ` ${word.content}`;
        /* String#fromCharCode is needed because the attribute
         * was getting trimmed when appended to DOM
         */
        dataAttributeText = `${String.fromCharCode(160)}${word.content}`;
    } else {
        displayText = word.content;
        dataAttributeText = word.content;
    }

    let innerWordNode = document.createElement("span");
    innerWordNode.classList.add("word-inner");
    innerWordNode.setAttribute("data-text", dataAttributeText);
    innerWordNode.setAttribute("data-duration", word.duration);
    if (word.wait) {
        innerWordNode.setAttribute("data-wait", word.wait);
    }
    innerWordNode.textContent = displayText;
    return innerWordNode;
}