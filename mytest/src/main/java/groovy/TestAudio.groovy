package groovy

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import com.mongodb.DBObject
import com.mongodb.Mongo
import com.mongodb.MongoURI
import com.mongodb.util.JSON

/**
 * Created by Administrator on 2015/1/27.
 */


new File("F:\\afp_patch_109\\record_afp").listFiles().each {
    String path =it.absolutePath;
    int start = path.lastIndexOf("\\");
    int end = path.lastIndexOf("afp");
    String id =  path.substring(start+1,end-1);
    FileInputStream fin = new FileInputStream(it);
    DataInputStream da = new DataInputStream(fin);
    StringBuilder sb = new StringBuilder();
    HashSet set = new HashSet();
    int a=0,b=0;
    while(da.available()>=4){
        int finger = da.readInt();
        if(set.add(finger)&&finger>0){
            b++;
            sb.append(finger+" ");
        }
        if(++a >=1024 ||++b>=256){
            break;
        }

    }

    int i=0;
    boolean hit =false;
            try {
                String url1 = "http://localhost:8080/fingerFuzzy/select?q=${URLEncoder.encode(sb.toString(), 'utf8')}&wt=json&indent=true&sm=5&rows=50"
                println url1;
                def url = new URL(url1)
                def hitjson = (Map) JSON.parse(url.getText("utf8"))
                int time = ((Map)hitjson.get("responseHeader")).get("QTime");
                List list = (List<Map>) ((Map)hitjson.get("response")).get("docs")
                for(;i<list.size();i++){
                    String auid = list.get(i).get(id);
                    if(auid.equals(id)) {
                        hit = true;
                        break;
                    }
                }

                println id+"|"+i+"|"+hit+"|"+time;
            } catch (e) {
                e.printStackTrace()
                println id;
            }

}
//noise_coll.insert(basicDBObjectList);
