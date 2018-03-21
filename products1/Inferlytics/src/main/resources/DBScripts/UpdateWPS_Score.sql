




-- Step1. First take the TotalCount & Weighed TotalScore
-- Step2. now calculate the mean (1st multiply the Numerator with (1.0) just to make it decimal at any-how 
-- Step3. join the Word_Pattern_Score with this subquery, basedon ID & Word_Id 
-- Step4. update the Score of word_pattern_score with the matched "Score" from sub-query
UPDATE word_pattern_score 
JOIN (
SELECT 
  WORD_ID 
, (
   SUM(WEAK_POSITIVE) * 0.35 
	+ SUM(POSITIVE) * 0.6 
	+ SUM(STRONG_POSITIVE) * 0.75  
	+ SUM(WEAK_NEGATIVE) * (-0.35) 
	+ SUM(NEGATIVE) * (-0.6) 
	+ SUM(STRONG_NEGATIVE) * (-0.75) 
  ) * 1.0 /    ( 
  SUM(WEAK_POSITIVE)  
  + SUM(POSITIVE)  
  + SUM(STRONG_POSITIVE)   
  + SUM(WEAK_NEGATIVE)  
  + SUM(NEGATIVE)  
  + SUM(STRONG_NEGATIVE)   
  ) Score

FROM feedback
GROUP BY WORD_ID)  subQuery ON word_pattern_score.ID = subQuery.WORD_ID
SET word_pattern_score.SCORE = subQuery.Score