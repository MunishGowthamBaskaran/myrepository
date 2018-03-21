local scoreid =  redis.call("lindex", KEYS[1]..":multiwordScore", ARGV[1]);
local spaceindex = string.find(scoreid, "%s");
   spaceindex = spaceindex == nil and 0 or spaceindex;
  local scoreindex = string.sub(scoreid,spaceindex+1);
return redis.call("lindex", "wordscorelist",scoreindex);
