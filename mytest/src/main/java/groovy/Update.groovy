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

DBCollection noise_coll = mongo.getDB("ttpod_search").getCollection("songs");

static  $$(Map map) {new BasicDBObject(map)};

int a=0;
new File("E://ids.log").eachLine {
    String [] line = it.split(",");
    if(line.length == 3){
        try{
//              println it;
            int search = line[1] as int;
            int click = line[2] as int;
            if(search>30 && (click/search)<0.4){
                println it+","+click/search;
            }
        }catch(Exception e){

        }
    }
    noise_coll.update($$(_id:ids),$$('$set':[status:322,"modified_at" :1433158929]));
}
