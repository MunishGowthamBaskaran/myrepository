CREATE DATABASE IF NOT EXISTS `rm_lexicondb` ;



USE `rm_lexicondb`;

CREATE TABLE `mst_paterns` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `PATTERN` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PATTERN` (`PATTERN`)
) 
CREATE TABLE `word_pattern_score` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `WORD` VARCHAR(50) DEFAULT NULL,
  `PATTERN_ID` INT(11) NOT NULL,
  `SCORE` FLOAT DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `PATTERN_ID` (`PATTERN_ID`),
  CONSTRAINT `patternID_fk` FOREIGN KEY (`PATTERN_ID`) REFERENCES `mst_paterns` (`ID`)
)
CREATE TABLE `hashtags` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag` varchar(50) NOT NULL,
  `value` double NOT NULL,
  PRIMARY KEY (`id`)
)
CREATE TABLE `sentence_modifiers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `modifier` varchar(100) NOT NULL,
  `value` double NOT NULL,
  PRIMARY KEY (`id`)
) 

CREATE TABLE `noun` (
  `NounId` bigint(11) NOT NULL AUTO_INCREMENT,
  `PostId` bigint(11) DEFAULT NULL,
  `NounWord` varchar(11) NOT NULL,
  `WordId` int(11) DEFAULT NULL,
  `EntityId` bigint(20) DEFAULT NULL,
  `CreatedDate` datetime NOT NULL,
  PRIMARY KEY (`NounId`),
  KEY `FK_noun` (`EntityId`),
  KEY `FK_noun_sentimentword` (`WordId`),
  CONSTRAINT `FK_noun` FOREIGN KEY (`EntityId`) REFERENCES `entity` (`id`),
  CONSTRAINT `FK_noun_sentimentword` FOREIGN KEY (`WordId`) REFERENCES `word_pattern_score` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1


CREATE TABLE `nounstopword` (
  `NounStopwordId` bigint(11) NOT NULL AUTO_INCREMENT,
  `NounStopword` varchar(50) NOT NULL,
  PRIMARY KEY (`NounStopwordId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1

