import org.gradle.FileUtil;
import org.gradle.JSONUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


/**  
 * date: 2014-4-23 下午7:40:50 
 *
 * @author chaoyang.wang@ttpod.com 
 */
public class JsoupTest {

    static BufferedWriter onlineBw = FileUtil.getBufferedWriter("f://onlineresult6.txt");

    static BufferedWriter testBw = FileUtil.getBufferedWriter("f://testresult6.txt");

    public static void main(String[] args) {
        JsoupTest jt = new JsoupTest();
        BufferedReader br = FileUtil.getBufferedReader("f://hot_query.log");
        try {
            String str;
            int i = 0;
            while((str = br.readLine())!=null ){
                String	strUrl = URLEncoder.encode(str, "utf-8");
                System.out.println(i++);
               String ansjTo = jt.getAnsjAnalyzer(str,strUrl);
                String ansjIndex = jt.getAnsjAnalyzerIndex(str,strUrl);
               String ali = jt.getAliAnalyzer(str,strUrl);
//                if(!ansj.equals(ali)){
                    testBw.write(ansjTo+"|"+ansjIndex+"|"+ali.toLowerCase().replaceAll("//","/"));
                    testBw.newLine();
//                }
            }
            testBw.flush();
            testBw.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    String getAnsjAnalyzer(String str,String strUrl){

        StringBuilder value = new StringBuilder();
        try {
            URL url = new URL("http://192.168.8.12:6080/song/analysis/field?wt=json&analysis.showmatch=true&analysis.fieldvalue="+strUrl+"&analysis.fieldtype=text_ansj_to&_=1416389445024");
//            URL url = new URL("http://113.31.130.5:18027/sys/index.html#/song/analysis?analysis.fieldtype=text_ansj_to&verbose_output=1&analysis.fieldvalue=" + str);
            Reader reader = new InputStreamReader(new BufferedInputStream(url.openStream()));
            int c;
            StringBuilder sb = new StringBuilder();
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }

            Map mp = JSONUtil.jsonToMap(sb.toString());
            Map docs = (Map) ((Map) ((Map) mp.get("analysis")).get("field_types")).get("text_ansj_to");
            List list = (List) docs.get("index");

            List list1 = (List)list.get(3);
            for(int i=0;i<list1.size();i++){
                Map tmp = (Map)list1.get(i);
                value.append(tmp.get("text")+"/");
            }
        }catch (Exception e){
            System.out.println("error:"+str);
            e.printStackTrace();
        }
        return value.toString();
    }

    String getAnsjAnalyzerIndex(String str,String strUrl){

        StringBuilder value = new StringBuilder();
        try {
            URL url = new URL("http://192.168.8.12:6080/song/analysis/field?wt=json&analysis.showmatch=true&analysis.fieldvalue="+strUrl+"&analysis.fieldtype=text_ansj_index&_=1416389445024");
//            URL url = new URL("http://113.31.130.5:18027/sys/index.html#/song/analysis?analysis.fieldtype=text_ansj_to&verbose_output=1&analysis.fieldvalue=" + str);
            Reader reader = new InputStreamReader(new BufferedInputStream(url.openStream()));
            int c;
            StringBuilder sb = new StringBuilder();
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }

            Map mp = JSONUtil.jsonToMap(sb.toString());
            Map docs = (Map) ((Map) ((Map) mp.get("analysis")).get("field_types")).get("text_ansj_index");
            List list = (List) docs.get("index");

            List list1 = (List)list.get(3);
            for(int i=0;i<list1.size();i++){
                Map tmp = (Map)list1.get(i);
                value.append(tmp.get("text")+"/");
            }
        }catch (Exception e){
            System.out.println("error:"+str);
            e.printStackTrace();
        }
        return value.toString();
    }

    String getAliAnalyzer(String str,String strUrl) {

        StringBuilder sb = new StringBuilder();
        try {
            str = URLEncoder.encode(str, "gb2312");
//            URL url = new URL("http://bj-algo.proxy.taobao.org/aliws_demo/aliws_demo.php?keyword="+str+"&catid=");
//
//            Reader reader = new InputStreamReader(new BufferedInputStream(url.openStream()));
//            int c;
//            StringBuilder sbt = new StringBuilder();
//            while ((c = reader.read()) != -1) {
//                sbt.append((char) c);
//            }
//		Document doc = Jsoup.parse(sbt.toString());
            Document doc = Jsoup.connect("http://bj-algo.proxy.taobao.org/aliws_demo/aliws_demo.php?keyword="+str+"&catid=").get();
            Element content = doc.body();//getElementById("table");
            Elements links = content.getElementsByTag("tr");
            for (Element link : links) {
                Element element = link.getElementsByTag("td").first();
                if (element != null) {
                    sb.append(element.text()).append("/");
                }
            }
//            System.out.println(doc.text());
        } catch (Exception e) {
            e.printStackTrace();
        }
    return sb.toString();
    }
}
