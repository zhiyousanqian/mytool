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

DBCollection ad_coll = mongo.getDB("ttpod_index").getCollection("adwords");
DBCollection song_coll = mongo.getDB("zookeeper").getCollection("hot_data");

static  $$(Map map) {new BasicDBObject(map)};

Map<String,Set<String>> map = new HashMap();

int a=0,b=0;
String tag = null;
new File("E://tag2Tag.log").eachLine {
    it=it.trim()
    String[] lines = it.split("\\t");
    println b++;
    if (lines.length == 2) {
        tag = lines[0]
        Set set = map.get(lines[0]);
        if(set==null){
            set = new HashSet();
            map.put(tag,set);
        }
        set.add(lines[1]);
    } else if (lines.length == 1) {
            map.get(tag).add(lines[0]);
    }
}

for(String str:map.keySet()){
    StringBuilder sb = new StringBuilder();
    for(String key:map.get(str)){
        if(!str.equals(key)) {
            sb.append(key).append("|");
        }
    }
    if(sb.length()>1) {
        println str + ">" + sb.substring(0, sb.length() - 1)
    }
}

