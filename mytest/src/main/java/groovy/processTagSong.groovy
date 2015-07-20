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

DBCollection ad_coll = mongo.getDB("ttpod_index").getCollection("adwords_add");
DBCollection song_coll = mongo.getDB("ttpod_search").getCollection("songs");

static  $$(Map map) {new BasicDBObject(map)};

Map<String,List<DBObject>> map = new HashMap();

Set hashSet = new HashSet();

int a=0,b=0;
String tag = null;
new File("E://ad_persion.log").eachLine {
    String[] lines = it.trim().split("\\t");
    println b++;
    if (lines.length == 3) {
        println a++ +"  "+it;
        tag = lines[0]
        List<DBObject> mapList = new ArrayList<>();
        map.put(lines[0],mapList);
        DBObject dbObject = getid_listen(song_coll,lines[1],lines[2]);
        if(dbObject!=null&&hashSet.add(tag + dbObject.get("_id"))){
           mapList.add(dbObject);
        }
    } else if (lines.length == 2) {
        DBObject dbObject = getid_listen(song_coll,lines[0],lines[1]);
        if(dbObject!=null&&hashSet.add(tag + dbObject.get("_id"))){
            map.get(tag).add(dbObject);
        }
    }
}

//List<DBObject> tagList = new ArrayList<>();
for(String key:map.keySet()){
    List<DBObject> songs= map.get(key);
    Collections.sort(songs,new Comparator<DBObject>() {
        @Override
        public int compare(DBObject o1, DBObject o2) {
            return ((Integer) o2.get("pick_count")).compareTo((Integer) o1
                    .get("pick_count"));
        }
    })


    BasicDBObject basicDBObject = new BasicDBObject()
    basicDBObject.put("tag",key);
//    int size = songs.size();
//    if (size>50){
//        size = 50
//    }
    basicDBObject.put("songList",songs);
    ad_coll.insert(basicDBObject);
//    StringBuilder sb = new StringBuilder();
//    StringBuilder nameSb = new StringBuilder();
//    for(DBObject song:songs){
//        sb.append(song.get("song_id")).append(",");
//        nameSb.append(song.get("name")+"_"+song.get("singer_name")+",");
//    }
}


DBObject getid_listen(DBCollection song_coll,String name,String singer_name){
    DBCursor cur = song_coll.find($$(name:name,singer_name:singer_name,status:1),$$(name:1,singer_name:1,pick_count:1,_id:1)).sort($$(pick_count:-1));
    if (cur.hasNext()) {
        DBObject dbObject = cur.next();
        return dbObject;
    }else if(singer_name.indexOf("/")>-1){
        singer_name = singer_name.replaceAll("/"," & ");
        DBCursor cur1 = song_coll.find($$(name:name,singer_name:singer_name,status:1),$$(name:1,singer_name:1,pick_count:1,_id:1)).sort($$(pick_count:-1));
        if(cur1.hasNext()){
            return cur1.next();
        }
    }
    return null;
}

