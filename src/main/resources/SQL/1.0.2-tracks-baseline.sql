CREATE TABLE Tracks(
trackId INT AUTO_INCREMENT,
title VARCHAR(64) NOT NULL,
lyricsId INT NOT NULL,
artist VARCHAR(64) NOT NULL,
trackYear YEAR,
PRIMARY KEY (trackId),
FOREIGN KEY (lyricsId) REFERENCES Lyrics (lyricsId)
);