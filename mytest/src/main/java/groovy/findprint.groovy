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

//DBCollection tag_coll = mongo.getDB("ttpod_index").getCollection("adwords_add");

DBCollection cr_coll = mongo.getDB("ttpod_search").getCollection("copyright_data");

static  $$(Map map) {new BasicDBObject(map)};

//new File("tag.log").eachLine {
    DBCursor tagCur = cr_coll.find($$("urls.name":"咪咕音乐"));
tagCur.count()
    while (tagCur.hasNext()) {
        int a=0
        DBObject dbObject = tagCur.next();
        print dbObject.get("_id")+"|"+dbObject.get("name")+"|"+dbObject.get("singer_name")
        List<DBObject> objects = dbObject.get("urls");
        for(DBObject object : objects){
            if(object.get("name").equals("咪咕音乐"))
            println "|"+object.get("url");
        }
    }
//}
