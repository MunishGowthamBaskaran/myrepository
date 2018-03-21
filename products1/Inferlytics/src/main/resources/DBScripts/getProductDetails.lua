local productexists = redis.call("HEXISTS",ARGV[1],"ProductName");
if  (productexists == 0)
            then        return nil;
end
return  redis.call("HMGET",ARGV[1],"ProductName","ProductUrl","ProdImgUrl");
