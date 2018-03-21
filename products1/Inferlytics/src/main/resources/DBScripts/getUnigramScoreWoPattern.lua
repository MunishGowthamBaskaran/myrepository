local scoreindex = redis.call("get",ARGV[1]..":unigramscore");
if  (scoreindex == nil)
            then        return "0.0";
end
return  redis.call("lindex","wordscorelist",scoreindex);
