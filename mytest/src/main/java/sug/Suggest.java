package sug;

import heap.TopN;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.Serializable;

/**
 * 热搜词 根据搜索次数排序
 * <p/>
 * date: 12-8-13 下午2:38
 *
 * @author: yangyang.cong@ttpod.com
 */
public class Suggest implements Comparable<Suggest>,Serializable {

    private static final long serialVersionUID = 1632905699587746701L;

    public int weight;

    public String name;

    public String singer_name;

    public Integer _id;

    public int hit;

    public String val;

    public int hot;

    public int type;//0 搜索词，1 艺人， 2 歌曲

    private TopN<Suggest> songTopN = new TopN<Suggest>(5);

    public String getSinger_name() {
        return singer_name;
    }

    public void setSinger_name(String singer_name) {
        this.singer_name = singer_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public TopN<Suggest> getSongTopN() {
        return songTopN;
    }

    public void setSongTopN(TopN<Suggest> songTopN) {
        this.songTopN = songTopN;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Suggest(String val, int weight, int hit) {
        this.val = val.intern();
        this.weight = (int) (weight * lengthNorm(val));
        this.hot = weight;
        this.hit = hit;
    }

    //风云榜只按照差值排序
    public Suggest(String val, int search_count) {
        this.val = val.intern();
        this.weight = search_count;
        this.hot = search_count;
    }

    public Suggest(String name, String singer_name, Integer _id, int weight) {
        this.val = name;
        this.name = name;
        this.singer_name=singer_name;
        this._id = _id;
        this.weight = weight;
        songTopN.insert(this);
    }

    @Override
    public int compareTo(Suggest that) {
        return this.weight > that.weight ? 1 :
                (this.weight == that.weight ? 0 : -1);
    }

    public String[] generateKeys() {
        char[] charOfCN = val.toCharArray();
        if (!isChinese(charOfCN[0])) {

            String lower = val.toLowerCase();
            // 大小写显示敏感处理
            if(!lower.equals(val)){
                return new String[]{lower, val};
            }

            return new String[]{val};
        }


        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        // remove firstSpell  2014.6.11
        //StringBuilder firstSpell = new StringBuilder();
        StringBuilder spell = new StringBuilder();

        try {
            String[] spellArray = null;
            for (char cn : charOfCN) {
                if (isChinese(cn) && (spellArray = PinyinHelper.toHanyuPinyinStringArray(cn, defaultFormat)) != null) {
                    //firstSpell.append(spellArray[0].charAt(0));
                    spell.append(spellArray[0]);
                } else {
                    //firstSpell.append(cn);
                    spell.append(cn);
                }
            }
            return new String[]{val,
                    //firstSpell.toString(),
                    spell.toString()};
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
            return new String[]{val};
        }

    }

    /**
     * 判断是否为中文字符,忽略全半角标点
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
            //|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                ) {
            return true;
        }
        return false;
    }

    /**
     * 把中文转成Unicode码
     */
    public String chineseToUnicode(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            int chr1 = (char) str.charAt(i);
            if (chr1 >= 0X4E00 && chr1 <= 0X9FA5) {//汉字范围 \u4e00-\u9fa5 (中文)
                result += "\\u" + Integer.toHexString(chr1);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }
//
//    public static void main(String[] args) {
//
//        String s  = "zsd晚上wd，！";
//        for( char c : s.toCharArray())
//            System.out.println(c+"\t"+isChinese(c));
//
//        HotWord word = new HotWord("周杰伦",23);
//        System.out.println(Arrays.toString(word.generateKeys()));
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Suggest suggest = (Suggest) o;

        if (val != null ? !val.equals(suggest.val) : suggest.val != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return val != null ? val.hashCode() : 0;
    }

    @Override
    public String toString() {
        return val + ":"+name+":"+singer_name+":"+_id+":" + weight+":"+hit;
    }

    static final float[] norms = {0,1,1,1,1.1f,1.2f,1.4f};
    public static float  lengthNorm(String val){
        int length = val.length();
        if(length>6){
            return 1.5f;
        }
        return norms[length];
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getWeight() {
        return weight;
    }

    public int getHit() {
        return hit;
    }

    public String getVal() {
        return val;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

}
