package org.gradle;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class CompareVideo {
	static boolean a;

	static BufferedWriter onlineBw = FileUtil.getBufferedWriter("f://onlineVideo.txt");
	
	static BufferedWriter testBw = FileUtil.getBufferedWriter("f://testVideo.txt");
	
//	static BufferedWriter testBwId = FileUtil.getBufferedWriter("D://testresultId.txt");
	
	public static void main(String[] args) {
		CompareVideo cr = new CompareVideo();

		BufferedReader br = FileUtil.getBufferedReader("E://search_word.log");
		try {
			String str;
			int i = 0;
			while((str = br.readLine())!=null ){
                System.out.println(i++);
//                if(i>10000) break;
			 String	strUrl = URLEncoder.encode(str, "utf-8");
//			 if(i>1000) break;
			 getTestline(strUrl);
//			 getOnline(strUrl);
//			 getCorrect(strUrl);
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
		URL url = new URL("http://so.ard.iyyin.com/v2/songs/search?page=1&size=10&q="+key);
		 Reader reader = new InputStreamReader(new BufferedInputStream(url.openStream()));
       int c;
       StringBuilder sb = new StringBuilder();
       while ((c = reader.read()) != -1) {
               sb.append((char)c);
       }
       
//       List<OnlineBean> obs = (List<OnlineBean>)JSON.parseObject(sb.toString(), OnlineBean.class);
       Map mp = JSONUtil.jsonToMap(sb.toString());
//       Map obj = (Map)mp.get("data");
       List<Map> docs = (List<Map>)mp.get("data");
       StringBuilder value = new StringBuilder();
       int count =0;
       for(Map ob:docs){
           count++;
           if(count >5) break;
       	value.append(ob.get("song_id"));
       	value.append("@");
       	value.append(ob.get("song_name"));
       	value.append("@");
       	value.append(ob.get("singer_name"));
       	value.append("@");
       	value.append(ob.get("pick_count"));
       	value.append("|");
//       	if(count++ >8) break;
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
		URL url = new URL("http://113.31.130.28:8080/s/video?page=1&size=10&q="+key);

		 Reader reader = new InputStreamReader(new BufferedInputStream(url.openStream()));
        int c;
        StringBuilder sb = new StringBuilder();
        while ((c = reader.read()) != -1) {
                sb.append((char)c);
        }
        
//        List<OnlineBean> obs = (List<OnlineBean>)JSON.parseObject(sb.toString(), OnlineBean.class);
        Map mp = JSONUtil.jsonToMap(sb.toString());
//      Map obj = (Map)mp.get("data");
      List<Map> docs = (List<Map>)mp.get("data");
      StringBuilder value = new StringBuilder();
      int count =0;
      for(Map ob:docs){
          count++;
//          if(count >5) break;
      	value.append(ob.get("videoName"));
      	value.append("@");
      	value.append(ob.get("singerName"));
      	value.append("@");
      	value.append(ob.get("songId"));
        value.append("@");
        value.append(ob.get("id"));
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
		URL url = new URL("http://113.31.130.5:28027/song/select?wt=json&start=0&rows=1&indent=true&debugQuery=true&q="+key);
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
       	if(tSearch.contains(">")){
         	value.append(tSearch.substring(tSearch.indexOf(">")+1));
       	} else if(tSearch.contains("|")){
            value.append(tSearch.substring(tSearch.indexOf("|")+1));
        }else if(!tSearch.equals(docs.get("rawquerystring"))){
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