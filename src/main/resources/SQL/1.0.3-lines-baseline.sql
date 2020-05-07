CREATE TABLE `Lines`(
lineId INT AUTO_INCREMENT,
lineOrder int NOT NULL,
startAt FLOAT NOT NULL,
remain FLOAT NOT NULL,
lyricsId INT NOT NULL,
PRIMARY KEY (lineId, lineOrder),
FOREIGN KEY (lyricsId) REFERENCES Lyrics (lyricsId)
);