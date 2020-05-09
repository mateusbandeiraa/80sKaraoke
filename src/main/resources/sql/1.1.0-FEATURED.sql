CREATE TABLE featuredTracks (
featuredId INT AUTO_INCREMENT NOT NULL,
trackId INT NOT NULL,
PRIMARY KEY (featuredId),
FOREIGN KEY (trackId) REFERENCES Tracks (trackId)
)