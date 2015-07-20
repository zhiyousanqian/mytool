package groovy

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import com.mongodb.Mongo
import com.mongodb.MongoURI


Mongo mongo = new MongoURI("mongo://10.0.5.145:27018,10.0.5.146:27018,10.0.5.147:27018");

DBCollection hot_Coll = mongo.getDB("zookeeper").getCollection("hot_data");

DBCollection tag_Coll = mongo.getDB("ttpod_search").getCollection("gd_crawler");

String [] dd = ["歌曲","音乐","流行","红歌","金曲","最热的","最火的","最新的","铃声","大全","精选","下载"];

static  $$(Map map)  {new BasicDBObject(map)};


DBCursor hot_cur = hot_Coll.find($$(listen_count:$$('$gte':10)));
Map<Integer,Integer> map = {};
while (hot_cur.hasNext()){
    BasicDBObject basicDBObject = hot_cur.next();
    map.put(basicDBObject.get("song_id"),basicDBObject.get("listen_count"));

}

DBCursor tag_cur = tag_Coll.find($$(tags:$$('$ne':[])));

while(tag_cur.hasNext()){
    BasicDBObject basicDBObject = tag_cur.next();
    List<String> tagList = basicDBObject.get("tags");

}




