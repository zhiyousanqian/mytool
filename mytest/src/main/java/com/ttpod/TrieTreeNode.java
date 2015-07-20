package com.ttpod;

import java.util.HashMap;
import java.util.Map;

public class TrieTreeNode {
	public TrieTreeNode parent;
	public Map<Character, TrieTreeNode> childs = new HashMap<Character, TrieTreeNode>();
	public char value = 0;
	public int state = 0;
	public int count = 0;

	public boolean isDelFlg() {
		if (this.count > 0) {
			return false;
		}
		return true;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("<node value = " + value + " state = " + state + " count = "
				+ count + " />");
		return buf.toString();
	}
}
