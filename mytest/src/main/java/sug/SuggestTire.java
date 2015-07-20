package sug;

import java.util.List;

/**
 * 搜索建议服务接口
 * <p/>
 * date: 12-8-15 上午11:17
 *
 * @author: yangyang.cong@ttpod.com
 */
public interface SuggestTire {


    /**
     * 增加热搜词
     *
     * @param word
     * @param hot
     */
//    void addHotWord(String word, int hot,int results);


    /**
     * 前缀匹配 返回词数量
     *
     * @param prefix
     * @param limit
     * @return
     */
    List<Suggest> suggestBy(String prefix, int limit);


}
