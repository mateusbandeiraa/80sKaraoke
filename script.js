const track = {
    title: "Waiting for a girl like you",
    artist: "Foreigner",
    year: "1981",
    lyrics: {
        lines: [
            {
                startAt: 1,
                words: [
                    { content: "So", duration: 2.2, wait: 1 },
                    { content: "long,", duration: 0.9 },
                    { content: "I've", duration: 0.3, wait: 0.9 },
                    { content: "been", duration: 0.2, wait: 0.1 },
                    { content: "looking", duration: 1 },
                    { content: "too", duration: 0.2 },
                    { content: "hard", duration: 0.5 },
                ]
            },
            {
                startAt: 10,
                words: [
                    { content: "I've", duration: 0.2 },
                    { content: "been", duration: 0.5 },
                    { content: "waiting", duration: 1 },
                    { content: "too", duration: 0.5 },
                    { content: "long", duration: 0.5 },
                ]
            },
        ]
    }
};

document.addEventListener("DOMContentLoaded", function () {
    drawHeader(track);
    const lineNodes = drawLines(track.lyrics);
    setWipings(lineNodes);
});

function drawHeader(track){
    document.getElementById("track-title").textContent = track.title;
    document.getElementById("track-artist").textContent = track.artist;
    document.getElementById("track-year").textContent = track.year;
}

function drawLines(lyrics) {
    let lineCount = 0;
    let wordCount = 0;

    let lineNodes = [];
    for (let line of lyrics.lines) {
        const lineNode = createLineNode(lineCount++);
        let needsSpaceCharacter = false;
        for (let word of line.words) {
            const outerWordNode = createWordNode(word, wordCount++, needsSpaceCharacter);

            if (!needsSpaceCharacter) {
                needsSpaceCharacter = true;
            }

            lineNode.appendChild(outerWordNode);
        }
        document.getElementById("lyrics-container").appendChild(lineNode);
        lineNodes.push(lineNode);
    }

    return lineNodes;
}

function createLineNode(lineId) {
    let lineNode = document.createElement("div");
    lineNode.classList.add("line");
    lineNode.setAttribute("data-line-id", lineId);
    return lineNode;
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

function setWipings(lineNodes) {
    let currentTime = 0;
    for (let line of lineNodes) {
        for (let outerWordNode of line.getElementsByClassName("word-outer")) {
            let innerWordNode = outerWordNode.querySelector(`.word-inner`);

            const duration = parseFloat(innerWordNode.getAttribute("data-duration"));
            const wait = parseFloat(innerWordNode.getAttribute("data-wait"));

            if (wait) {
                currentTime += wait;
            }
            setTimeout(() => {
                innerWordNode.setAttribute('style', `transition-duration: ${duration * 1000}ms`);
                innerWordNode.classList.add("active");
            }, currentTime * 1000);
            currentTime += duration;
        }
    }
}