package groovy

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import com.mongodb.DBObject
import com.mongodb.Mongo
import com.mongodb.MongoURI

/**
 * Created by Administrator on 2015/2/5.
 */

//Mongo mongo = new Mongo(new MongoURI("mongodb://113.31.130.5:27777"));
Mongo mongo = new Mongo(new MongoURI("mongodb://113.31.130.5:27132"));
DBCollection singerColl = mongo.getDB("ttpod_search").getCollection("song_no_copyright");
DBCollection pictureColl = mongo.getDB("ttpod_picture").getCollection("picture");
//13 14 17
def $$(Map map){return new BasicDBObject(map)}

DBCursor picCur = pictureColl.find($$(type:1),$$(refId:1)).batchSize(10000);
Set <String> hashSet = new HashSet<>()
int a=0;
while(picCur.hasNext()){
    println a++;
    Integer singerId = (Integer)picCur.next().get("refId");
    println "update singerId="+ singerId;
    singerColl.update($$(_id:singerId,hasPic:null),$$($set:[haspic:1]));
}

