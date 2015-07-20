package org.gradle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * 文件操作工具类
 *
 */
public class FileUtil {


	static BufferedReader br = null;

	static BufferedWriter bw = null;
/**
 * 得到上次更新时间，并写入当前时间
 * @param filePath
 * @return
 */
	public static String getLastUpdateTime(String filePath) {
		String lastUpdateTime = "";
		try {
			long now = System.currentTimeMillis() / 1000;
			BufferedReader bReader = getBufferedReader(filePath);
			lastUpdateTime = bReader.readLine();
			bReader.close();

			BufferedWriter bWriter = getBufferedWriter(filePath);
			bWriter.write("" + now);
			bWriter.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		return lastUpdateTime;
	}

	 // 获得要带缓存的文件读对象
	public static BufferedReader getBufferedReader(String str) {
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					new File(str))));
		} catch (Exception e) {
			System.out.println(e);
		}
		return br;
	}

	// 获得要带缓存的文件读对象
	public static BufferedWriter getBufferedWriter(String str) {
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(str))));
		} catch (Exception e) {
			System.out.println(e);
		}
		return bw;
	}
}
