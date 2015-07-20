package groovy

import com.mongodb.BasicDBObject
import com.mongodb.DBCursor
import com.mongodb.DBObject
import com.mongodb.Mongo
import com.mongodb.MongoURI
//@Grapes([
//        @Grab('org.mongodb:mongo-java-driver:2.12.2'),
//        @Grab('com.ttpod:ttpod-cache:1.3.0'),
//        @Grab('org.apache.curator:curator-recipes:2.4.2')
//])
import com.mongodb.util.JSON

import java.util.regex.Matcher
import java.util.regex.Pattern

Set<String> set = new HashSet<>();

def mongo = new Mongo(new MongoURI('mongodb://113.31.130.5:27132'))

static $$(Map map) { new BasicDBObject(map) }

def hot_data = mongo.getDB("zookeeper").getCollection("hot_data")

def song = mongo.getDB("ttpod_search").getCollection("songs")

List <String> tagList = new ArrayList<String>();

//List<String> keyList = new ArrayList<String>();
//DBCursor cur =  search_fail.find($$(count:$$('$gte':10)))
//while(cur.hasNext()){
//    DBObject dbObject = cur.next();
//    keyList.add(dbObject.get("keyword")+","+dbObject.get("count"));
//}

Pattern patternMoive = Pattern.compile(".*《(.*)》.*");

Pattern patternSong = Pattern.compile("(.*)\\(.*\\)");

DBCursor cur = song.find($$(status:1),$$(pick_count:1,_id:1,name:1)).sort($$(modified_at:-1)).batchSize(5000)
Set hashSet = new HashSet()
List<DBObject> list = new ArrayList<>();
int aa=0;
StringBuilder sb = new StringBuilder()
while (cur.hasNext()){
    DBObject dBObject = cur.next();
    if(dBObject.get("pick_count")>5000 &&hashSet.add(dBObject.get("name"))){
        println aa++;
        sb.append(dBObject.get("_id")).append(",")
        if(aa>60){
            break;
        }
    }

}

println sb.toString();


