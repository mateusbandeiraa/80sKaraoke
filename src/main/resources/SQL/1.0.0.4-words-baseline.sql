CREATE TABLE Words(
lineId INT NOT NULL,
wordOrder INT NOT NULL,
content VARCHAR(64) NOT NULL,
duration FLOAT NOT NULL,
wait FLOAT NOT NULL,
PRIMARY KEY (lineId, wordOrder),
FOREIGN KEY (lineId) REFERENCES `Lines` (lineId)
);