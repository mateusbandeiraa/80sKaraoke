CREATE TABLE Tracks(
trackId INT AUTO_INCREMENT,
title VARCHAR(64) NOT NULL,
artist VARCHAR(64) NOT NULL,
trackYear SMALLINT UNSIGNED,
PRIMARY KEY (trackId)
);