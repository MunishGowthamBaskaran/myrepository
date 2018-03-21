local pospatterns =  redis.call("keys", ARGV[1]..":posPattern*");
local continue = false
local score = 0

for index1,posPattern in ipairs(pospatterns)
   do 
   local posIndex =  redis.call("lrange", posPattern, 0, -1);

   continue = false
   
   	for index2,index2value in ipairs(posIndex)
  	do 
   
	local spaceindex = string.find(index2value, "%s");
   	spaceindex = spaceindex == nil and 0 or spaceindex;
   	local scoreindex = string.sub(index2value,spaceindex+1);
 
	if index2==1 then
	if scoreindex == "1"
		then continue = true		
	else 
	  break
        end
	end
       
       if index2 == 2 and continue==true then         
          local pospatternvalue = redis.call("lindex","pospatternlist",scoreindex)      
          if not (string.find(ARGV[2],pospatternvalue) == nil)
            then
           continue = true
 	   elseif not (string.find(pospatternvalue,ARGV[2]) == nil)
	    then
           continue = true
          else  continue = false
            end 
	end

       if index2 == 3 and continue==true  then
          score =  redis.call("lindex","wordscorelist",scoreindex)  
          break;
          end	

        
   	end

   end
 if not (score == 0) then return score else
 return  redis.call("lindex","wordscorelist",redis.call("get",ARGV[1]..":unigramscore"))  end
