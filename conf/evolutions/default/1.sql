-- !Ups

CREATE TABLE `user` (
  `userID` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `birthday` date DEFAULT NULL,
  PRIMARY KEY (`userID`),
  UNIQUE KEY `id_UNIQUE` (`userID`)
);
CREATE TABLE `friends` (
  `userID` int NOT NULL,
  `friendID` int NOT NULL,
  `confirmed` int DEFAULT '0',
  PRIMARY KEY (`userID`,`friendID`),
  KEY `fk_user_idx` (`userID`),
  KEY `fk_friend_idx` (`friendID`),
  CONSTRAINT `fk_user1` FOREIGN KEY (`userID`) REFERENCES `user` (`userID`),
  CONSTRAINT `fk_user2` FOREIGN KEY (`friendID`) REFERENCES `user` (`userID`)
);
CREATE TABLE `post` (
  `postID` int NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `date` datetime DEFAULT CURRENT_TIMESTAMP,
  `author` int DEFAULT NULL,
  PRIMARY KEY (`postID`),
  KEY `fk_author_idx` (`author`),
  CONSTRAINT `fk_author` FOREIGN KEY (`author`) REFERENCES `user` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE `liked_posts` (
  `postID` int NOT NULL,
  `userID` int NOT NULL,
  PRIMARY KEY (`postID`,`userID`),
  KEY `fk_postID_idx` (`postID`),
  KEY `fk_userID_idx` (`userID`),
  CONSTRAINT `fk_postID` FOREIGN KEY (`postID`) REFERENCES `post` (`postID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_userID` FOREIGN KEY (`userID`) REFERENCES `user` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE `comment` (
  `commentID` int NOT NULL AUTO_INCREMENT,
  `userID` int NOT NULL,
  `text` mediumtext,
  `date` datetime DEFAULT CURRENT_TIMESTAMP,
  `postID` int DEFAULT NULL,
  PRIMARY KEY (`commentID`),
  KEY `fk_user_comment_idx` (`userID`),
  KEY `fk_post_comment_idx` (`postID`),
  CONSTRAINT `fk_post_comment` FOREIGN KEY (`postID`) REFERENCES `post` (`postID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_comment` FOREIGN KEY (`userID`) REFERENCES `user` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE
)

-- !Downs
DROP TABLE `user` IF EXISTS;
DROP TABLE `friends` IF EXISTS;
DROP TABLE `posts` IF EXISTS;
DROP TABLE `liked_post` IF EXISTS;
DROP TABLE `comment` IF EXISTS