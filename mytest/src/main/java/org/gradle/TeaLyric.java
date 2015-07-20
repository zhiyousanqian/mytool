package org.gradle;


/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 13-3-5
 * Time: 下午2:35
 * 需要utf8编码
 */
public class TeaLyric {

    public static String title = "无声仿有声";
    public static String artist = "谢霆锋";
    public static int lrcid = 3353;

    public static String ToCode(String title, String artist, int lrcid) {
        int[] teaKey = makeTEAKey(title, artist, lrcid);
        int[] in = {lrcid, 0};
        String code = ttEncrypt(teaKey, in, (title + artist).length());
        return code;
    }

    private static String ttEncrypt(int[] k, int[] in, int round) {
        int y = in[0], z = in[1], sum = 0;
        int delta = 0x9e3779b9;
        long a = k[0], b = k[1], c = k[2], d = k[3];
        for (int i = 0; i < round; i++) { /* basic cycle start */
            sum += delta;
            y += (int) ((z << 4) + a) ^ (z + sum) ^ ((z >>> 5) + b);
            z += ((y << 4) + c) ^ (y + sum) ^ ((y >>> 5) + d); /* end cycle */
        }

        String code = String.format("%08x", y) + String.format("%08x", z);
        return code;
    }

    public static int[] ttEncryptBackInt(int[] k, int[] in, int round) {
        int y = in[0], z = in[1], sum = 0;
        int delta = 0x9e3779b9;
        long a = k[0], b = k[1], c = k[2], d = k[3];
        for (int i = 0; i < round; i++) { /* basic cycle start */
            sum += delta;
            y += (int) ((z << 4) + a) ^ (z + sum) ^ ((z >>> 5) + b);
            z += ((y << 4) + c) ^ (y + sum) ^ ((y >>> 5) + d); /* end cycle */
        }
        return new int[]{y, z};
    }

    private static String ttDecrypt(int[] k, int[] in, int round) {
        int y = in[0], z = in[1], sum = 0;
        int a = k[0], b = k[1], c = k[2], d = k[3];
        int delta = 0x9e3779b9; //这是算法标准给的值
        if (round == 32) {
            sum = 0xC6EF3720;
        } else if (round == 16) {
            sum = 0xE3779B90;
        } else {
            sum = delta * round;
        }
        for (int i = 0; i < round; i++) {
            z -= ((y << 4) + c) ^ (y + sum) ^ ((y >>> 5) + d);
            y -= ((z << 4) + a) ^ (z + sum) ^ ((z >>> 5) + b);
            sum -= delta;
        }
        return "";
    }

    private static int[] makeTEAKey(String title, String artist, int lrcid) {
        int[] teaKey = {0, 0, 0, 0};
        for (int i = 0; i < title.length(); i++) {
            teaKey[0] = i ^ teaKey[0] + title.toCharArray()[i];
        }
        teaKey[1] = teaKey[0];
        for (int i = artist.length() - 1; i >= 0; i--) {
            teaKey[1] = i ^ teaKey[1] + artist.toCharArray()[i];
        }

        teaKey[2] = lrcid;
        teaKey[3] = ~(teaKey[0] + teaKey[1] ^ (teaKey[2] = lrcid));
        return teaKey;
    }

    public static void main(String[] args) {
        System.out.println(ToCode(title, artist, lrcid));
    }

}
