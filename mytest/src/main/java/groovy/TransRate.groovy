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

Mongo mongo = new Mongo(new MongoURI("mongodb://113.31.130.5:27132"));
DBCollection fail_coll = mongo.getDB("search_failed").getCollection("2015-03-26");
//13 14 17
def $$(Map map){return new BasicDBObject(map)}

Set <String> hashSet = new HashSet<>()
new File("F://zuizhong_tag.log").eachLine{
    for(String word:it.split("\\|")){
        if(hashSet.add(word)){
        DBCursor cur = fail_coll.find($$(keyword:word))
        if(cur.hasNext()){
            DBObject object = cur.next();
            println object.get("keyword")+"|"+object.get("count")+"|"+object.get("opened")+"|"+object.get("trans_rate");
        }else{
            println word+"|"+0+"|"+0+"|"+0;
        }
        }
    }
}
