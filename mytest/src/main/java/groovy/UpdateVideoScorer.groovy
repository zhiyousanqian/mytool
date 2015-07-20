package groovy

import com.mongodb.*

import java.util.regex.Matcher
import java.util.regex.Pattern

//@Grapes([
//        @Grab('org.mongodb:mongo-java-driver:2.9.2'),
//        @GrabConfig(systemClassLoader = true)
//])
class UpdateVideoScorer {
    static def mongoT = new Mongo(new MongoURI("mongodb://113.31.130.5:27132"))
//    static def mongoT = new Mongo(new MongoURI("mongodb://192.168.8.12:27017"))

    static def videoCollT = mongoT.getDB("ttpod_video").getCollection("videos")
    static def scorerVideo =  mongoT.getDB("ttpod_video").getCollection("video_scorer")

    static def hotColl = mongoT.getDB("zookeeper").getCollection("hot_data")

    static $$(Map map){return new BasicDBObject(map)}

    static long last7day = (System.currentTimeMillis()/1000 as int) - 604800;

    public static void main(String[] args) {
      println "start update video scorer "
        DBCursor dbCursor = videoCollT.find().batchSize(10000)
        int i = 0;
        while(dbCursor.hasNext()){
            BasicDBObject basicDBObject = dbCursor.next();
            updateVideoScorer(basicDBObject);

            if(i++ % 10000==0){
                println "update count = "+ i;
            }
        }
        println "update video scorer count="+i+" end!"
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
            if(matcherMoive.find()){
                scorer += 2.0f;
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
        videoCollT.save(basicDBObject);
    }
}


