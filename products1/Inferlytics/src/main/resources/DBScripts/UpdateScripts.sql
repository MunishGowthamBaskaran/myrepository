UPDATE  setiwordnet_data SET sentiment=0.6 WHERE word = 'great'

UPDATE  setiwordnet_data SET word='hope_?_listening' WHERE id = 38309

INSERT INTO setiwordnet_data(word,sentiment) VALUES('i_hope_?_is_listening',0.6)

INSERT INTO smileys(smiley,VALUE) VALUES(';)',0.5)
DELETE FROM setiwordnet_data WHERE id=38309

SELECT * FROM setiwordnet_data WHERE word= 'i_hope_?_is_listening'

SELECT * FROM modifiers WHERE MODIFIER='hell'

SELECT * FROM smileys WHERE smiley=':)'

SELECT * FROM posts WHERE content LIKE '%pre-ordered%'


INSERT INTO mst_paterns SELECT * FROM mst_paterns_21mar13
INSERT INTO word_pattern_score SELECT * FROM word_pattern_score_21mar13


ALTER TABLE entity
ADD UNIQUE (VALUE)


ALTER TABLE feedback MODIFY COLUMN WEAK_POSITIVE INT NOT NULL

ALTER TABLE feedback MODIFY COLUMN POSITIVE INT NOT NULL

ALTER TABLE feedback MODIFY COLUMN STRONG_POSITIVE INT NOT NULL

ALTER TABLE feedback MODIFY COLUMN WEAK_NEGATIVE INT NOT NULL

ALTER TABLE feedback MODIFY COLUMN NEGATIVE INT NOT NULL

ALTER TABLE feedback MODIFY COLUMN STRONG_NEGATIVE INT NOT NULL 

ALTER TABLE feedback MODIFY COLUMN NEUTRAL INT NOT NULL 





ALTER TABLE `rm_lexicondb`.`feedback` 
   CHANGE `WEAK_POSITIVE` `WEAK_POSITIVE` INT(11) DEFAULT '0' NOT NULL, 
   CHANGE `POSITIVE` `POSITIVE` INT(11) DEFAULT '0' NOT NULL, 
   CHANGE `STRONG_POSITIVE` `STRONG_POSITIVE` INT(11) DEFAULT '0' NOT NULL, 
   CHANGE `WEAK_NEGATIVE` `WEAK_NEGATIVE` INT(11) DEFAULT '0' NOT NULL, 
   CHANGE `NEGATIVE` `NEGATIVE` INT(11) DEFAULT '0' NOT NULL, 
   CHANGE `STRONG_NEGATIVE` `STRONG_NEGATIVE` INT(11) DEFAULT '0' NOT NULL, 
   CHANGE `NEUTRAL` `NEUTRAL` INT(11) DEFAULT '0' NOT NULL
   
   
   ALTER TABLE `rm_lexicondb`.`noun` CHANGE COLUMN `NounWord` `NounWord` VARCHAR(100) NOT NULL  ;
   
   UPDATE setiwordnet_data SET word =  REPLACE(word, '\-', '_')  WHERE word LIKE '%-%'
   
   alter table `rm_lexicondb`.`posts` 
   change `id` `id` varchar(50) NOT NULL

   
      
   ALTER TABLE `rm_lexicondb`.`post_sentiment` 
   ADD COLUMN `DETAILED_SENTIMENT` VARCHAR(50) NOT NULL AFTER `SENTIMENT`,
   CHANGE `POST_ID` `POST_ID` BIGINT(11) NOT NULL, 
   CHANGE `ENTITY_ID` `ENTITY_ID` INT(11) NOT NULL, 
   CHANGE `SENTIMENT` `SENTIMENT` SMALLINT(6) NOT NULL, 
   CHANGE `SCORE` `SCORE` DOUBLE NOT NULL
   
   INSERT INTO NounStopword(NounStopword) VALUES('I\'m'),('don\'t'),('target'),('rt'),('lol'),('wasn\'t'),('it's'),('ty'),('cl'),('cr7'),('dgk'),('?'),('wht'),('blk')
   ;

   
   