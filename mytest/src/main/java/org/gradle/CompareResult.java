package org.gradle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CompareResult{
	static boolean a;

	static BufferedWriter onlineBw = FileUtil.getBufferedWriter("F://onlineresult6.txt");
	
	static BufferedWriter testBw = FileUtil.getBufferedWriter("F://testresult7.txt");
	
//	static BufferedWriter testBwId = FileUtil.getBufferedWriter("D://testresultId.txt");
	
	public static void main(String[] args) {
		CompareResult cr = new CompareResult();
		

		BufferedReader br = FileUtil.getBufferedReader("F://correct.log");
		try {
			String str;
			int i = 0;
			while((str = br.readLine())!=null ){
			 String	strUrl = URLEncoder.encode(str, "utf-8");
			 System.out.println(i++);
//			 if(i>1000) break;
//			 getTestline(strUrl);
//			 getOnline(strUrl);
			 getCorrect(strUrl);
			}
			onlineBw.close();
//			testBwId.close();
			testBw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getOnline(String key) throws Exception{
		URL url = new URL("http://10.0.5.63:6080/song/select?start=0&rows=6&wt=json&indent=true&q="+key);
		 Reader reader = new InputStreamReader(new BufferedInputStream(url.openStream()));
       int c;
       StringBuilder sb = new StringBuilder();
       while ((c = reader.read()) != -1) {
               sb.append((char)c);
       }
       
//       List<OnlineBean> obs = (List<OnlineBean>)JSON.parseObject(sb.toString(), OnlineBean.class);
       Map mp = JSONUtil.jsonToMap(sb.toString());
       Map obj = (Map)mp.get("response");
       List<Map> docs = (List<Map>)obj.get("docs");
       StringBuilder value = new StringBuilder();
       
       for(Map ob:docs){
       	value.append(ob.get("_id"));
       	value.append("@");
       	value.append(ob.get("name"));
       	value.append("@");
       	value.append(ob.get("singer_name"));
       	value.append("@");
       	value.append(ob.get("pick_count"));
       	value.append("|");
       }
//       getTestlineId(docs);
       value.append("\r\n");
       onlineBw.write(value.toString());
//       System.out.println(value.toString());
       reader.close(); 
       return "";
	}
	
	//http://10.0.5.75:6080/song/select?q=zhoujielun&start=0&rows=8&wt=json&indent=true
	public static String getTestline(String key) throws Exception{
		URL url = new URL("http://10.0.5.75:6080/song/select?start=0&rows=6&wt=json&indent=true&q="+key);
		 Reader reader = new InputStreamReader(new BufferedInputStream(url.openStream()));
        int c;
        StringBuilder sb = new StringBuilder();
        while ((c = reader.read()) != -1) {
                sb.append((char)c);
        }
        
//        List<OnlineBean> obs = (List<OnlineBean>)JSON.parseObject(sb.toString(), OnlineBean.class);
        Map mp = JSONUtil.jsonToMap(sb.toString());
        Map obj = (Map)mp.get("response");
        List<Map> docs = (List<Map>)obj.get("docs");
        StringBuilder value = new StringBuilder();
        
        for(Map ob:docs){
        	value.append(ob.get("_id"));
        	value.append("@");
        	value.append(ob.get("name"));
        	value.append("@");
        	value.append(ob.get("singer_name"));
        	value.append("@");
        	value.append(ob.get("pick_count"));
        	value.append("|");
        }
//        getTestlineId(docs);
        value.append("\r\n");
        testBw.write(value.toString());
//        System.out.println(value.toString());
        reader.close(); 
        return "";
	}
	
	public static String getCorrect(String key) throws Exception{
		URL url = new URL("http://113.31.130.5:18027/song/select?wt=json&start=0&rows=1&indent=true&debugQuery=true&q="+key);
		 Reader reader = new InputStreamReader(new BufferedInputStream(url.openStream()));
       int c;
       StringBuilder sb = new StringBuilder();
       while ((c = reader.read()) != -1) {
               sb.append((char)c);
       }
       
//       List<OnlineBean> obs = (List<OnlineBean>)JSON.parseObject(sb.toString(), OnlineBean.class);
       Map mp = JSONUtil.jsonToMap(sb.toString());
       Map docs = (Map)mp.get("debug");
       StringBuilder value = new StringBuilder();
       
       	value.append(docs.get("rawquerystring"));
       	value.append("@");
       	String tSearch = String.valueOf(docs.get("querystring"));
       	if(tSearch.contains(" OR ")){
       	value.append(tSearch.substring(tSearch.indexOf(" OR ")+4));
       	} else if(!tSearch.equals(docs.get("rawquerystring"))){
       		value.append(tSearch);
       	}
       
//       getTestlineId(docs);
       value.append("\r\n");
       testBw.write(value.toString());
//       System.out.println(value.toString());
       reader.close(); 
       return "";
	}
	
//	public static String getOnlineId(String key) throws Exception{
//		URL url = new URL("http://192.168.8.12:18080/songs/search?q="+key);
//		 Reader reader = new InputStreamReader(new BufferedInputStream(url.openStream()));
//        int c;
//        StringBuilder sb = new StringBuilder();
//        while ((c = reader.read()) != -1) {
//                sb.append((char)c);
//        }
//        
////        List<OnlineBean> obs = (List<OnlineBean>)JSON.parseObject(sb.toString(), OnlineBean.class);
//        Map mp = JSONUtil.jsonToMap(sb.toString());
//        List<Map> list = (List<Map>)mp.get("data");
//        StringBuilder value = new StringBuilder();
//        List<Integer> ids = new ArrayList<Integer>();
//        for(Map ob:list){
//        	ids.add((Integer)ob.get("song_id"));
//        }
//        
//        sortList(ids);
//        
//        value.append(list2String(ids)+"\r\n");
//        onlineBw.write(value.toString());
//        reader.close(); 
//        return "";
//	}
//	
//	public static String getTestlineId(List<Map> docs) throws Exception{
//        StringBuilder value = new StringBuilder();
//        
//        List<Integer> ids = new ArrayList<Integer>();
//        for(Map ob:docs){
//        	ids.add((Integer)ob.get("_id"));
//        }
//        
//        sortList(ids);
//        
//        value.append(list2String(ids)+"\r\n");
//        testBwId.write(value.toString());
//        return "";
//	}
//	
//	public static void sortList(List<Integer> list){
//		
//	Collections.sort(list, new Comparator() {
//
//		public int compare(Object o1, Object o2) {
//			Integer o11 = (Integer)o1;
//			Integer o22 = (Integer)o2;
//			
//			if ((Integer)o11 > (Integer)o22)
//				return -1;
//			else return 1;
//		}
//	});
//	}
//	public static String list2String(List<Integer> list){
//		StringBuilder sb = new StringBuilder();
//		for(Integer id: list){
//			sb.append(id+"@");
//		}
//		return sb.toString();
//	}
}