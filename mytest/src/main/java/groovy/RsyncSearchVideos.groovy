package groovy

import com.mongodb.BasicDBObject
import com.mongodb.Bytes
import com.mongodb.DBCursor
import com.mongodb.DBObject
import com.mongodb.Mongo
import com.mongodb.MongoURI
import groovyjarjarantlr.StringUtils

import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Matcher
import java.util.regex.Pattern

//@Grapes([
//        @Grab('org.mongodb:mongo-java-driver:2.9.2'),
//        @GrabConfig(systemClassLoader = true)
//])
class RsyncSearchVideos {
    static def mongoF = new Mongo("10.0.5.25", 27019)
//    static def mongoF = new Mongo("58.241.28.215", 57017)

    static def videoCollF = mongoF.getDB("ttpod_video").getCollection("videos")

    static def videoFileCollF = mongoF.getDB("ttpod_video").getCollection("videoFile")

    static def mongoT = new Mongo(new MongoURI("mongodb://10.0.5.145:27018,10.0.5.146:27018,10.0.5.147:27018"))
//    static def mongoT = new Mongo(new MongoURI("mongodb://192.168.8.12:27017"))

    static def singerCollF = mongoF.getDB("ttpod_singer").getCollection("singer")

    static def videoCollT = mongoT.getDB("ttpod_video").getCollection("videos")

    static def songCollT = mongoT.getDB("ttpod_search").getCollection("songs")

    static def hotColl = mongoT.getDB("zookeeper").getCollection("hot_data")

    static long last7day = (System.currentTimeMillis()/1000 as int) - 604800;

    static $$(Map map){return new BasicDBObject(map)}

    public static void main(String[] args) {
        long lastRsyncTime = 0;
        long now = System.currentTimeMillis()/1000 as int;
        File markFile = new File("videoLastModify.mark");
        if(markFile.exists()){
            lastRsyncTime = markFile.lastModified();
        }else {
            markFile.createNewFile()
        }
        if(!markFile.canRead()){
            println "sync video view is running...."
            //  return;
        }
        markFile.setReadable(false);
        println "sync video view start!  last modify ="+lastRsyncTime
        DBCursor dbCursor = videoCollF.find($$(modifyAt:[$gte:(lastRsyncTime - 2000)])).batchSize(10000)
        int i = 0;
        while(dbCursor.hasNext()){
            BasicDBObject basicDBObject = dbCursor.next();
            renderVideo(basicDBObject);
            if(i++ % 10000==0){
                println "sync count = "+ i;
            }
        }
        println "sync video view count="+i+" end!"
        markFile.setReadable(true);
        markFile.setLastModified(now)
    }

    static def renderVideo(def o) {
        //更新歌手字段
        if(o.singerName == null||"".equals(o.singerName)){
            if(o.songId>0) {
                DBObject dbObject = songCollT.findOne($$(_id: o.songId))
                if (dbObject != null) {
                    o.singerName = dbObject.get("singer_name")
                }
            }else if(o.singerId>0){
                DBObject dbObject = singerCollF.findOne($$(_id: o.singerId))
                if (dbObject != null) {
                    o.singerName = dbObject.get("name")
                }
            }
        }
        List videoFiles = []
        boolean hasHD = false;
        boolean hasFile = false;
        DBCursor cur = videoFileCollF.find([videoId: o._id] as BasicDBObject)
        if(o._id == 122365){
            println "id:"+o._id;
        }
        while(cur.hasNext()){
            DBObject it = cur.next()
            if(o._id == 122365){
                println "id  .................:"+o._id;
            }
            hasFile =true;
            if(it.bitRate>=1000){
                hasHD = true;
            }
            videoFiles.add([
                    "_id": o._id,
                    "duration": it.duration,
                    "bitRate" : it.bitRate,
                    "path"    : it.path,
                    "size" : it.size,
                    "suffix": it.suffix,
                    "horizontal":it.horizontal,
                    "vertical": it.vertical,
                    "pathXiami":it.pathXiami
            ])
        }
        if(!hasFile){
            o.status = 0 as int;
        }
        def map = ["_id"         : o._id,
                   "song_id":o.songId,
                   "singer_id":o.singerId,
                   "name"        : o.videoName,
                   "singer_name": o.singerName,
                   "pick_count"  : o.pickCount,
                   "down_list"   : videoFiles,
                   "published_at": o.createAt,
                   "status"      : o.status as int,
                   "hasHD"     : hasHD,
                   "modify_at":  System.currentTimeMillis()/1000 as int]
        BasicDBObject basicDBObject = map as BasicDBObject
        //updateVideoScorer(basicDBObject);
        videoCollT.save(basicDBObject);
    }

    static  void  updateVideoScorer(BasicDBObject basicDBObject){
        if(basicDBObject.name==null|| basicDBObject.singer_name==null) {
            println "warn id:"+ basicDBObject._id
            return;
        }
        //更新分数
        Pattern patternSong = Pattern.compile("(.*)\\(.*\\)");
        Pattern patternMoive = Pattern.compile(".*《(.*)》.*");
        Matcher matcherSong = patternSong.matcher(basicDBObject.name);
        Matcher matcherMoive = patternMoive.matcher(basicDBObject.name);

        //标准格式的影视类mv加分
        float scorer = 0.0f
        String songName = null;
        if (matcherSong.find()) {
            songName = matcherSong.group(1);
            scorer -= 0.5f;
            if(matcherMoive.find()){
                scorer += 1.0f;
            }
        }

        //选取mv对应的最热歌曲分数。
        float tmpScorer = 0.0f
        DBCursor hotCur = hotColl.find($$(name:basicDBObject.name,singer_name:basicDBObject.singer_name));
        while(hotCur.hasNext()){
            DBObject dbObject = hotCur.next();
            tmpScorer = tmpScorer > dbObject.get("score")?tmpScorer:dbObject.get("score")
        }

        if(!songName.equals(basicDBObject.name)){
            DBCursor hotCur2 = hotColl.find($$(name:songName,singer_name:basicDBObject.singer_name));
            while(hotCur2.hasNext()){
                DBObject dbObject = hotCur2.next();
                tmpScorer = tmpScorer > (Float)dbObject.get("score")?tmpScorer :(Float)dbObject.get("score")
            }
        }
        basicDBObject.scorer = scorer + tmpScorer;
        if(basicDBObject.scorer <0.1f &&basicDBObject.song_id>0){
            DBCursor dbCursor = hotColl.find($$(song_id:basicDBObject.song_id))
            while(dbCursor.hasNext()){
                BasicDBObject dbObject = dbCursor.next()
                basicDBObject.scorer = basicDBObject.scorer > dbObject.get("score")/3.0?basicDBObject.scorer:dbObject.get("score")/3.0
            }
        }

        //给高清资源加分
        if(basicDBObject.hasHD){
            basicDBObject.scorer += 0.5f;
        }
        if(basicDBObject.published_at > last7day){
            basicDBObject.scorer += 0.2f;
        }
        basicDBObject.scorer = basicDBObject.scorer as float;
//        videoCollT.save(basicDBObject);
    }
}

