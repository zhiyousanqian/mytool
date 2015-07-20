package org.gradle;

import com.mongodb.util.JSON;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
/**  
 * date: 2014-3-6 上午10:03:05 
 *
 * @author chaoyang.wang@ttpod.com 
 */
public class runtest {
    static final String makeImgUrl(int videoId){
        StringBuilder sb = new StringBuilder("http://img.mv.ttpod.com/mv_pic/mv_pic_");
        sb.append(videoId/100000);
        sb.append("/160_90/");
        sb.append(videoId/255);
        sb.append("/");
        sb.append(videoId/7);
        sb.append("/");
        sb.append(videoId);
        sb.append("_20.jpg");
        return sb.toString();
    }
    static boolean isChinese(String word) {
        int i = 0;
        char[] chars = word.toCharArray();
        for (char c : chars) {
            if (c >= 19968 && c <= 171941) {
                return true;
            }
        }
        return false;
    }

    static void fillPicUrl(int _id) {
        StringBuilder sb = new StringBuilder(
                "http://3p.pic.ttdtweb.com/3p.ttpod.com/album/");
        sb.append(_id%255);
        sb.append("/");
        sb.append(_id%7);
        sb.append("/");
        sb.append(_id).append(".jpg@150h_150w");
        System.out.println(sb.toString());
    }

    public static Integer getSongAlbumNum(Integer _id){
        int hit = 0;
        try {
            URL url = new URL("http://api.dongting.com/song/singer/"+_id+"?detail=true");

            Reader reader = new InputStreamReader(new BufferedInputStream(url.openStream()));
            int c;
            StringBuilder sb = new StringBuilder();
            while ((c = reader.read()) != -1) {
                sb.append((char)c);
            }

//       List<OnlineBean> obs = (List<OnlineBean>)JSON.parseObject(sb.toString(), OnlineBean.class);
//            Map mp = JSONUtil.jsonToMap(sb.toString());

            Map hitjson = (Map) JSON.parse(sb.toString());

            Map data = (Map)hitjson.get("data");
            System.out.println(data.get("songsCount"));
            System.out.println(data.get("albumsCount"));


        } catch (Exception e) {
            e.printStackTrace();
        }
        return hit;
    }

    private static String picFormat(String pix,Integer id){
//        new StringBuilder(50)
//        .append("http://pic.itlily.com/album/").append(pix)
//        .append('/').append(pix).append('/').append(id).append(".jpg")
//        .toString()
        StringBuilder sb = new StringBuilder(
                "http://3p.pic.ttdtweb.com/3p.ttpod.com/album/");
        sb.append(id % 255);
        sb.append("/");
        sb.append(id % 7);
        sb.append("/");
        sb.append(id).append(".jpg@"+pix+"h_"+pix+"w");
        return sb.toString();
    }


    public static void main(String[] args) throws IOException {
//        URL url=new URL("http://localhost:8080/s/finger?q=she");
//        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
//        OutputStream os = connection.getOutputStream();
//        OutputStreamWriter br = new OutputStreamWriter(connection.getOutputStream(),"UTF-8"));
//        br.write();
//        br.flush();
 runtest test = new runtest();
        test.uploadFile();

//        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
//        int ch;
//        while ((ch = in.read()) != -1) {
//            System.out.print((char) ch);
//        }


        System.out.println("104008,1,".split(",").length);
        System.out.println("jiafei(加菲)df)".replaceAll("\\(.*\\)", ""));
        System.out.println("S.H.E".replaceAll("<.+?>", ""));
System.out.println(Math.pow(2,22));
//        URL url = new URL("http://42.156.141.128:9999/song?key=liu");
//        Reader reader = new InputStreamReader(new BufferedInputStream(url.openStream()));
//        int c;
//        StringBuilder sb = new StringBuilder();
//        while ((c = reader.read()) != -1) {
//            sb.append((char)c);
//        }
        float a = 0.3f;
        System.out.println(new StringBuilder("20120301").insert(4,'-').insert(7,'-').toString());
	}

    public static void uploadFile() {
        try {
            // 换行符
            final String newLine = "\r\n";
            final String boundaryPrefix = "--";
            // 定义数据分隔线
            String BOUNDARY = "========7d4a6d158c9";
            // 服务器的域名
            URL url = new URL("http://localhost:8080/s/finger?q=she");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置为POST情
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求头参数
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            out.write("周卤蛋".getBytes("utf-8"));
            out.flush();
            out.close();
            // 定义BufferedReader输入流来读取URL的响应
	BufferedReader reader = new BufferedReader(new InputStreamReader(
	        conn.getInputStream()));
	String line = null;
	while ((line = reader.readLine()) != null) {
	    System.out.println(line);
	}
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
    }

}
