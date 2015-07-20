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

Mongo mongo  = new Mongo(new MongoURI("mongodb://10.0.5.145:27018"));

DBCollection noise_coll = mongo.getDB("ttpod_search").getCollection("search_key");

static  $$(Map map) {new BasicDBObject(map)};

int today = new Integer(new Date().format("yyyyMMdd"))
int a=0;
List<BasicDBObject> list = new ArrayList<>();
new File("/data/search/keywordData/data/search/keywordData/keywordData_20150715.txt").eachLine {
    String [] line = it.split(",");
    if(line.length == 3){
        try{
            int search = line[1] as int;
            if(search>1) {
            int click = line[2] as int;
//            if(search>30 && (click/search)<0.4){
//                println it+","+click/search;
//            }
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put("keyword",line[0]);
            basicDBObject.put("search_count",search);
            basicDBObject.put("click_count",click);
            basicDBObject.put("trans_rate",click/search);
            basicDBObject.put("date",today);
            list.add(basicDBObject);
            }
            if(a++ % 5000 == 0)          {
                println a;
            }
        }catch(Exception e){

        }
    }
}
println "size ="+list.size();
println "finished="+ noise_coll.insert(list).getN();

