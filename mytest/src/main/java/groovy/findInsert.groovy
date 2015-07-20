package groovy

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import com.mongodb.DBObject
import com.mongodb.Mongo
import com.mongodb.MongoURI

/**
 * Created by Administrator on 2015/1/27.
 */

Mongo mongo  = new Mongo(new MongoURI("mongodb://113.31.130.5:27132"));

DBCollection stand_coll = mongo.getDB("standard").getCollection("etalon_singers_base");
DBCollection correct_coll = mongo.getDB("ttpod_search").getCollection("correct_word");
DBCollection noise_coll = mongo.getDB("ttpod_search").getCollection("correct_noise");

static  $$(Map map) {new BasicDBObject(map)};

DBCursor correctCur = correct_coll.find($$(count:$$($gt:100)));
Set set = new HashSet()
while(correctCur.hasNext()){
    DBObject dbObject = correctCur.next();
    set.add(dbObject.get("keyword"));
}
println set.size();


DBCursor standCur = stand_coll.find(null,$$(standard_name: 1));

Set standSet = new HashSet();
while(standCur.hasNext()){
    DBObject dbObject = standCur.next();
    standSet.add(dbObject.get("standard_name"));
}
println standSet.size();


int a=0;
int b=0;
List<BasicDBObject> basicDBObjectList = new ArrayList<>()
Set hashSet = new HashSet();
new File("E://singer.txt").eachLine {
    String key = it.trim();
    if(!standSet.contains(key) && set.contains(key) && key.length()>1 && hashSet.add(key)){
        BasicDBObject basicDBObject= new BasicDBObject();
        basicDBObject.put("keyword",it);
        basicDBObjectList.add(basicDBObject);
        println "a="+ a++;
    }
}
noise_coll.insert(basicDBObjectList);
