package org.gradle;
 
/**  
 * date: 2014-5-5 下午2:09:50 
 *
 * @author chaoyang.wang@ttpod.com 
 */
public class OnlineBean {
	
	String song_name;
	String singer_name;
	String song_id;
	String pick_count;
	
	public String getSong_name() {
		return song_name;
	}
	public void setSong_name(String song_name) {
		this.song_name = song_name;
	}
	public String getSinger_name() {
		return singer_name;
	}
	public void setSinger_name(String singer_name) {
		this.singer_name = singer_name;
	}
	public String getSong_id() {
		return song_id;
	}
	public void setSong_id(String song_id) {
		this.song_id = song_id;
	}
	public String getPick_count() {
		return pick_count;
	}
	public void setPick_count(String pick_count) {
		this.pick_count = pick_count;
	}
}
