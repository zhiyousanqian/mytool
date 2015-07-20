package org.gradle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ShenmaResult{
	static boolean a;
	public static void main(String[] args) {
		URL url;

		BufferedWriter bw = FileUtil.getBufferedWriter("D://单曲result.txt");

		BufferedReader br = FileUtil.getBufferedReader("D://单曲名.txt");
		try {
			String str;
			while((str = br.readLine())!=null ){
			 String	strUrl = URLEncoder.encode(str, "utf-8");
			 url = new URL("http://api.shenma.itlily.com/s?q="+strUrl);
			 Reader reader = new InputStreamReader(new BufferedInputStream(url.openStream()));
             int c;
             StringBuilder sb = new StringBuilder();
             while ((c = reader.read()) != -1) {
                     sb.append((char)c);
//            	 System.out.print((char)c);
             }

             Document doc = DocumentHelper.parseText(sb.toString());
             Element rootElt = doc.getRootElement();
             Element hits =	 rootElt.element("hits");
             Element hit =	 hits.element("hit");
             Element songlist =	 hit.element("songlist");
//             Element item =	 rootElt.element("item");

             Iterator iter = songlist.elementIterator("item");

             bw.write(str+"-----------------------\r\n");
             while (iter.hasNext()) {
            	 Element recordEle = (Element) iter.next();
            	 String singer = recordEle.elementTextTrim("singers");
            	 String song = recordEle.elementTextTrim("song_name");
            	 String play = recordEle.elementTextTrim("play_count");
            	 if(Integer.parseInt(play)<5000){
            	 bw.write(song+"@"+singer+"@"+play+"\r\n");
            	 }
             }
             reader.close();
			}
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}