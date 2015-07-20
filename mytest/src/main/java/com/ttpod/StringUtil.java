package com.ttpod;

import java.util.List;

/**
 * 
 * 编辑距离算法实现
 * 
 */
public class StringUtil {

	// ****************************
	// Get minimum of three values
	// ****************************

	private static int Minimum(int a, int b, int c) {
		int mi;

		mi = a;
		if (b < mi) {
			mi = b;
		}
		if (c < mi) {
			mi = c;
		}
		return mi;

	}

	// *****************************
	// Compute Levenshtein distance
	// *****************************

	public static int LD(String s, String t) {
		int d[][]; // matrix
		int n; // length of s
		int m; // length of t
		int i; // iterates through s
		int j; // iterates through t
		char s_i; // ith character of s
		char t_j; // jth character of t
		int cost; // cost

		// Step 1

		n = s.length();
		m = t.length();
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		d = new int[n + 1][m + 1];

		// Step 2

		for (i = 0; i <= n; i++) {
			d[i][0] = i;
		}

		for (j = 0; j <= m; j++) {
			d[0][j] = j;
		}

		// Step 3

		for (i = 1; i <= n; i++) {

			s_i = s.charAt(i - 1);

			// Step 4

			for (j = 1; j <= m; j++) {

				t_j = t.charAt(j - 1);

				// Step 5

				if (s_i == t_j) {
					cost = 0;
				} else {
					cost = 1;
				}

				// Step 6

				d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1,
						d[i - 1][j - 1] + cost);

			}

		}

		// Step 7

		return d[n][m];

	}

	public static int ListLD(List<String> s, List<String> t) {
		int d[][]; // matrix
		int n; // length of s
		int m; // length of t
		int i; // iterates through s
		int j; // iterates through t
		String s_i; // ith character of s
		String t_j; // jth character of t
		int cost; // cost

		// Step 1

		n = s.size();
		m = t.size();
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		d = new int[n + 1][m + 1];

		// Step 2

		for (i = 0; i <= n; i++) {
			d[i][0] = i;
		}

		for (j = 0; j <= m; j++) {
			d[0][j] = j;
		}

		// Step 3

		for (i = 1; i <= n; i++) {

			s_i = s.get(i - 1);

			// Step 4

			for (j = 1; j <= m; j++) {

				t_j = t.get(j - 1);

				// Step 5

				if (s_i.equals(t_j)) {
					cost = 0;
				} else {
					cost = 1;
				}

				// Step 6

				d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1,
						d[i - 1][j - 1] + cost);

			}

		}

		// Step 7

		return d[n][m];

	}

	public static String checkNull(String str) {
		if (null == str) {
			return "";
		}
		return str;
	}

	public static String toUppercase(String str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c >= 'a' && c <= 'z') {
				c -= 32;
			}
			sb.append(c);
		}
		return sb.toString();
	}

	public static int parseInt(String str) {
		int temp = 0;
		try {
			temp = Integer.parseInt(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}

	public static void main(String[] args) {
		StringUtil su = new StringUtil();
		String a = "我在那个角落伤风";
		String b = "我在那个角落患过";
		String c = "光亮";
		String d = "光的亮";
		String e = "光";
		// int len=Math.max(a.length(), b.length());
		System.out.println(su.LD(a, b));
		System.out.println(su.LD(a, c));
		System.out.println(su.LD(a, d));
		System.out.println(su.LD(a, e));
	}

}
