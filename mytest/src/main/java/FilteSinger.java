//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.StringReader;
//import java.util.HashSet;
//import java.util.Set;
//
//import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
//import org.apache.lucene.util.Version;
//import org.gradle.FileUtil;
//
//
///**
// * date: 2014-4-14 上午11:49:27
// *
// * @author chaoyang.wang@ttpod.com
// */
//public class FilteSinger {
//	private static StandardAnalyzer analyzer = new StandardAnalyzer(
//			Version.LUCENE_36, new HashSet<String>());
//
//	public static void main(String[] args) {
//
//		BufferedWriter bw = FileUtil.getBufferedWriter("D://singer.dic");
//
//		BufferedReader br = FileUtil.getBufferedReader("D://userLibrary.dic");
//		try {
////			Set<String> set =getSingerSet();
//			String str;
//			while ((str = br.readLine()) != null) {
////				 if(set.contains(str)){
//				TokenStream ts = analyzer.tokenStream(null, new StringReader(
//						str));
//				CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);
//				int i=0;
//				String term=null;
//				while (ts.incrementToken()) {
//					i++;
//					term =ta.toString();
//				}
//				if(i>1 && !isEnglish(str)){
//					bw.write(str+"\r\n");
//				}
////				term =null;
////			}
//			}
//bw.close();
//br.close();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public static Boolean isEnglish(String word) {
//		int charcount = 0;
//		int i = 0;
//		while (i < word.length()) {
//			char c = word.charAt(i);
//			if ((c <= 122 && c >= 97) || (c <= 90 && c >= 65)
//					|| (c <= 65370 && c >= 65345) || (c <= 65338 && c >= 65313)) {
//				charcount++;
//				i++;
//			} else if (c < 128 || (c <= 65305 && c >= 65296) || c == 65285
//					|| (c <= 65295 && c >= 65291)) {
//				i++;
//			} else {
//				return false;
//			}
//		}
//		if (charcount == 0) {
//			return false;
//		}
//		return true;
//	}
//
//	public static Set<String> getSingerSet() throws IOException{
//		BufferedReader br = FileUtil.getBufferedReader("D://small_singer.txt");
//		HashSet<String> set = new HashSet<String>();
//		String str;
//		while ((str = br.readLine()) != null) {
//			set.add(str);
//		}
//		return set;
//	}
//}
