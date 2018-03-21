DELETE FROM setiwordnet_data WHERE REPLACE(REPLACE(TRIM(word), CHAR(10), ''), CHAR(13), '')
  IN (SELECT REPLACE(REPLACE(TRIM(negation), CHAR(10), ''), CHAR(13), '') FROM negations )
-- Update smileys in sentiword with smileys in smileys table

 UPDATE setiwordnet_data
 JOIN smileys AS source ON setiwordnet_data.word = source.smiley
  SET setiwordnet_data.sentiment = source.value 
