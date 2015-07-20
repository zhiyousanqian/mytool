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

def $$(Map map){return new BasicDBObject(map)}
int a=0;
new File("D://dd.txt").eachLine {
    println a++;
    singerColl.save($$(_id:it as int,"created_at" : 1409559063))
}
//DBCollection pictureColl = mongo.getDB("ttpod_picture").getCollection("picture");
////13 14 17
//
//
//DBCursor picCur = pictureColl.find($$(type:1),$$(refId:1)).batchSize(10000);
//Set <String> hashSet = new HashSet<>()
//int a=0;
//while(picCur.hasNext()){
//    println a++;
//    Integer singerId = (Integer)picCur.next().get("refId");
//    println "update singerId="+ singerId;
//    singerColl.update($$(_id:singerId,hasPic:null),$$($set:[haspic:1]));
//}

