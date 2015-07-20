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

Map<String> map = new HashMap<>();

def mongo = new Mongo(new MongoURI('mongodb://113.31.130.5:27132'))

static $$(Map map) { new BasicDBObject(map) }

def search_fail = mongo.getDB("search_failed").getCollection("2015-04-22")
def black = mongo.getDB("ttpod_search").getCollection("song_blacklist")

List<String> keyList = new ArrayList<String>();

//Set set = new HashSet();

DBCursor curBlack =  black.find()

println curBlack.count()
while(curBlack.hasNext()){
    DBObject dbObject = curBlack.next();
    String key = ((String)dbObject.get("_id")).toLowerCase().replaceAll("[\\s]+"," ").trim();
    String value = dbObject.get("sub_class")+"|"+dbObject.get("word_class");
    map.put(key,value);
}

//DBCursor cur =  search_fail.find($$(count:$$('$gte':10)))

DBCursor cur =  search_fail.find($$("count":$$('$gt':4),"opened" : 0))

println "2015-04-22"
println cur.count()

int i=0;
while(cur.hasNext()){
    DBObject dbObject = cur.next();
    String str = ((String)dbObject.get("keyword")).toLowerCase()
    String word = str+","+dbObject.get("count")+"|"+getHitCount(str);
    if(map.containsKey(str)){
        word = word+"|"+ map.get(str)
    }else{
        word = word+"|"+getSpecial(str);
    }
        println word;
}

public static String getSpecial (String str){
    boolean special = true;
    for(char c:str.toCharArray()){
        if(Character.isDigit(c)||Character.isLetter(c)){
            special = false;
        }
    }
    if(special==true){
        return "输入错误"
    }
    int hit = 0;
    try {
        def url = new URL("http://10.125.11.230:9000/appkey=fdfs&type=song&src=web&order=weight&page=1&limit=10&uid=9&category=1&is_pub=y&key=${URLEncoder.encode(str, 'utf8')}")
//        println url.getText("utf8")
        def hitjson = (Map) JSON.parse(url.getText("utf8"))
        def map =  (Map)hitjson.songs;
        hit = map.count as Integer;
    } catch (e) {}
    if(hit==0){
        return "输入错误"
    }
    return "搜索问题"
}



public static int getHitCount(String suggest) {
    int hit = 0;
    try {
        def url = new URL("http://so.ard.iyyin.com/v2/songs/search?q=${URLEncoder.encode(suggest, 'utf8')}&size=1")
        def hitjson = (Map) JSON.parse(url.getText("utf8"))
        if (hitjson.code) {
            hit = hitjson.count as Integer;
        }
    } catch (e) {}
    return hit;
}
