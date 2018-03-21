local ids =  redis.call("lrange", KEYS[1]..":multiwordIndex", "0" ,"-1");
local t = {};
for index,value in ipairs(ids)
   do 
   local spaceindex = string.find(value, "%s");
   spaceindex = spaceindex == nil and 0 or spaceindex;
   local multiwordindex = string.sub(value,spaceindex+1);
   local multiword = redis.call("LINDEX","multiwordpatternlist", multiwordindex ) ; 
    table.insert(t, multiword) ;
   end
return t;
