package sug;

import heap.TopN;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/12/29.
 */
public class Song  implements Comparable<Suggest>,Serializable {
    private String name;
    private String singer_name;
    private Integer _id;
    private Integer hot;

    private TopN<Song> songTopN;

    public Song(String name, String singer_name, Integer _id, int hot) {
        this.name = name;
        this.singer_name=singer_name;
        this._id = _id;
        this.hot = hot;
        songTopN.insert(this);
    }

    public TopN<Song> getSongTopN() {
        return songTopN;
    }

    public void setSongTopN(TopN<Song> songTopN) {
        this.songTopN = songTopN;
    }

    @Override
    public int compareTo(Suggest that) {
        return this.hot > that.hot ? 1 :
                (this.hot == that.hot ? 0 : -1);
    }

    public String[] generateKeys() {
        char[] charOfCN = name.toCharArray();
        if (!isChinese(charOfCN[0])) {

            String lower = name.toLowerCase();
            // 大小写显示敏感处理
            if(!lower.equals(name)){
                return new String[]{lower, name};
            }

            return new String[]{name};
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
            return new String[]{name,
                    //firstSpell.toString(),
                    spell.toString()};
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
            return new String[]{name};
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger_name() {
        return singer_name;
    }

    public void setSinger_name(String singer_name) {
        this.singer_name = singer_name;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }

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
}
