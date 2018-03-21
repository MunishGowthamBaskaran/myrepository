


DROP PROCEDURE IF EXISTS insert_update_feedback ;

DELIMITER $$
CREATE PROCEDURE insert_update_feedback(varId BIGINT, columnUnderConsideration VARCHAR(20) )
BEGINinsert_update_feedback

-- 1. Get the count from feedback based on primary key
  DECLARE vatToTest INT;
  SELECT COUNT(*)  FROM feedback WHERE Id = varId INTO vatToTest;
  
  -- 2. if it's "GOOD" then in increment the "GOOD" by 1 otherwise insert new value 
  --    Convert the "columnUnderConsideration" to upper case to make it case-insensitive.
  IF UPPER(columnUnderConsideration) = 'GOOD' THEN
		  IF vatToTest  THEN
			  UPDATE feedback SET GOOD=GOOD+1 WHERE Id=varId ;
		  ELSE
			 INSERT INTO feedback(Id, GOOD) VALUES (varId, 1) ;
		  END IF;
  ELSEIF UPPER(columnUnderConsideration) = 'BAD' THEN
		  IF vatToTest  THEN
			  UPDATE feedback SET BAD=BAD+1 WHERE Id=varId ;
		  ELSE
			 INSERT INTO feedback(Id, BAD) VALUES (varId, 1) ;
		  END IF;
   ELSEIF UPPER(columnUnderConsideration) = 'NEUTRAL' THEN
		  IF vatToTest  THEN
			  UPDATE feedback SET NEUTRAL=NEUTRAL+1 WHERE Id=varId ;
		  ELSE
			 INSERT INTO feedback(Id, NEUTRAL) VALUES (varId, 1) ;
		  END IF;
   END IF ;
END;
$$