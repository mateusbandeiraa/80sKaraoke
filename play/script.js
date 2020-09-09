/* METADATA */
const API_ENDPOINT = '/assets';
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const urlTrackId = urlParams.get("trackid");
const urlStartAt = urlParams.get("startat");
const startAt = parseFloat(urlStartAt);

/* PAGE SETUP */
let updateInterval;
const updatesPerSecond = 30;
const updateTimeout = 1000 / updatesPerSecond;
const transitionCSS = `<style>.word-inner{transition-duration: calc(1000ms/${updatesPerSecond}) !important}</style>`;
document.addEventListener("DOMContentLoaded", async function () {
    // <unmute.js>
    // https://github.com/swevans/unmute
    let context = (window.AudioContext || window.webkitAudioContext) ?
        new (window.AudioContext || window.webkitAudioContext)() : null;
    if (context) unmute(context);
    // </unmute.js>
    document.querySelector("head").innerHTML += transitionCSS;
    let trackPlayer;
    let track;
    await Promise.all([fetchTrackAudio(urlTrackId), fetchTrack(urlTrackId)]).then(([trackPlayerResult, trackResult]) => {
        trackPlayer = trackPlayerResult;
        track = trackResult;
        clearLoadingScreen();
    });
    drawHeader(track);
    setControlsListeners(track, trackPlayer);
    if (startAt) {
        trackPlayer.seek(startAt);
    }
    setUpdateInterval(update, updateTimeout, track, trackPlayer);
});


function drawHeader(track) {
    document.getElementById("track-title").textContent = track.title;
    document.getElementById("track-artist").textContent = track.artist;
    document.getElementById("track-year").textContent = track.year;
}

function clearLoadingScreen() {
    document.getElementById("main-container").classList.remove("loading");
}

/* CONTROLS */

function setControlsListeners(track, trackPlayer) {
    document.getElementById("button-backwards").addEventListener("click", () => {
        if (trackPlayer.paused) {
            trackPlayer.seek(getCurrentTrackTimecode(trackPlayer) + 0.5);
        } else {
            trackPlayer.seek(0);
        }
        update(track, trackPlayer);
    });

    document.getElementById("button-play-pause").addEventListener("click", () => { toggleTrackPlayPause(track, trackPlayer) });

    document.getElementById("button-forward").addEventListener("click", () => {
        if (trackPlayer.paused) {
            trackPlayer.seek(getCurrentTrackTimecode(trackPlayer) + 0.1);
        } else {
            trackPlayer.seek(startAt || 25);
        }
        update(track, trackPlayer);
    });

    document.getElementById("button-exit").addEventListener("click", () => {
        confirmExit(track, trackPlayer);
    })
}

function toggleTrackPlayPause(track, trackPlayer) {
    let controlsContainerElement = document.getElementById("controls-container");
    let backgroundVideoElement = document.getElementById("background-video");
    let buttonElement = document.getElementById("button-play-pause");
    if (trackPlayer.playing()) {
        pauseTrack(trackPlayer, controlsContainerElement, backgroundVideoElement, buttonElement);
    } else {
        playTrack(trackPlayer, controlsContainerElement, backgroundVideoElement, buttonElement, track, trackPlayer);
    }
    update(track, trackPlayer);
}

function playTrack(trackPlayer, controlsContainerElement, backgroundVideo, button, track, trackPlayer) {
    trackPlayer.play();
    backgroundVideo.play();
    setUpdateInterval(update, updateTimeout, track, trackPlayer);

    controlsContainerElement.classList.remove("track-paused");

    button.classList.remove("fa-play");
    button.classList.add("fa-pause");
}

function pauseTrack(trackPlayer, controlsContainerElement, backgroundVideo, button) {
    trackPlayer.pause();
    backgroundVideo.pause();
    clearUpdateInterval();

    controlsContainerElement.classList.add("track-paused");

    button.classList.remove("fa-pause");
    button.classList.add("fa-play");
}

function confirmExit(track, trackPlayer) {
    let controlsContainerElement = document.getElementById("controls-container");
    let backgroundVideoElement = document.getElementById("background-video");
    let buttonElement = document.getElementById("button-play-pause");
    let wasPlayingBeforeConfirmation = false;
    if (trackPlayer.playing()) {
        wasPlayingBeforeConfirmation = true;
        pauseTrack(trackPlayer, controlsContainerElement, backgroundVideoElement, buttonElement);
    }

    const confirmationDialogHTML =
        `<div class="message-title-container">
                <h1 class="message-title">CONFIRMATION</h1>
            </div>
            <div class="message-content"><p>Do you really want to exit?</p>
                <div class="message-actions-row">
                    <input type="button" class="button" value="YES">
                    <input type="button" class="button" value="NO">
                </div>
            </div>`;
    let messageElement = document.createElement("div");
    messageElement.classList.add('message', 'confirmation-message');
    messageElement.innerHTML = confirmationDialogHTML;
    messageElement.querySelector('.button[value="YES"]').addEventListener('click', function () {
        window.location.href = '../';
    });
    messageElement.querySelector('.button[value="NO"]').addEventListener('click', function () {
        messageElement.remove();
        if (wasPlayingBeforeConfirmation) {
            playTrack(trackPlayer, controlsContainerElement, backgroundVideoElement, buttonElement, track, trackPlayer);
        }
    });
    document.getElementById('main-container').appendChild(messageElement);
}

/* UPDATE METHODS */
function update(track, trackPlayer) {
    const currentTrackTimecode = getCurrentTrackTimecode(trackPlayer);
    updateStatus(trackPlayer);
    drawTimecodeIndicator(currentTrackTimecode, trackPlayer);
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
        htmlStatusIconElement.setAttribute("data", "/assets/icons/pause.svg");
    } else if (!trackPlayer.paused && htmlStatusTextElement.textContent != "PLAY") {
        htmlStatusTextElement.textContent = "PLAY";
        htmlStatusIconElement.setAttribute("data", "/assets/icons/play.svg");
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
        for (let word of line.words) {
            const outerWordNode = createWordNode(word, line.words.indexOf(word));
            lineElement.appendChild(outerWordNode);
            lineElement.innerHTML += " ";
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
                const lineId = lineElement.getAttribute("data-line-id");
                lineElement.classList.add("hidden");
                lineElement.removeAttribute("data-line-id");
                let elementsToRemove = document.querySelectorAll(`.removeWithLine[data-line-id="${lineId}"]`);
                for (let element of elementsToRemove) {
                    element.remove();
                }
            }
        }
    }
}

/* HELPER METHODS */

function fetchTrack(trackId) {
    return fetch(`${API_ENDPOINT}/tracks/json/track${trackId}.json`).then((response) => response.json());
}

function fetchTrackAudio(trackId) {
    let trackPlayer = new Howl({ src: [`${API_ENDPOINT}/tracks/audio/track${trackId}.mp3`]});
    let promise = new Promise(function (resolve, reject) {
        trackPlayer.once('load', () => {
            resolve(trackPlayer);
        });
        trackPlayer.once("loaderror", () => {
            reject();
        })
    });
    return promise;
}

function getCurrentTrackTimecode(trackPlayer) {
    if (trackPlayer) {
        return trackPlayer.seek();
    } else {
        return 0;
    }
}

function drawTimecodeIndicator(timecode, trackPlayer) {
    let text;
    if (timecode == 0 && trackPlayer.paused) {
        text = "——:——";
    } else {
        timecode *= 1000; // We want miliseconds.
        text = new Date(timecode).toISOString().slice(14, 19);
    }
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

function isLineCurrentlyDisplayed(line, track) {
    let lineId = getLineId(line, track);
    if (document.querySelector(`.line[data-line-id="${lineId}"]`)) {
        return true;
    } else {
        return false;
    }
}

function getLineId(line, track) {
    return track.lyrics.lines.indexOf(line);
}

function createCSSWithPercentageSpent(lineId, wordId, percentageSpent) {
    return `.line[data-line-id="${lineId}"] [data-word-id="${wordId}"] .word-inner::after{
        width: ${percentageSpent}% !important;
    }`;
}

function createWordNode(word, wordId) {
    let outerWordNode = document.createElement("span");
    outerWordNode.classList.add("word-outer");
    outerWordNode.setAttribute("data-word-id", wordId);

    const innerWordNode = createInnerWordNode(word);
    outerWordNode.appendChild(innerWordNode);
    return outerWordNode;
}

function createInnerWordNode(word) {
    const displayText = `${word.content}`;

    let innerWordNode = document.createElement("span");
    innerWordNode.classList.add("word-inner");
    innerWordNode.setAttribute("data-text", word.content);
    innerWordNode.setAttribute("data-duration", word.duration);
    if (word.wait) {
        innerWordNode.setAttribute("data-wait", word.wait);
    }
    innerWordNode.textContent = displayText;
    return innerWordNode;
}

function setUpdateInterval(updateFunction, interval, track, trackPlayer) {
    if (updateInterval) {
        clearUpdateInterval();
    }
    updateInterval = setInterval(updateFunction, interval, track, trackPlayer);
}

function clearUpdateInterval() {
    if (updateInterval) {
        clearInterval(updateInterval);
    }
}