package sug;

import heap.TopN;
import tree.RadixTreeImpl;
import tree.RadixTreeNode;

import java.util.*;

/**
 * 基于 RadixTrie 进行前缀检索
 * <p/>
 * date: 12-8-15 上午11:24
 *
 * @author: yangyang.cong@ttpod.com
 */
public class RadixSuggest extends RadixTreeImpl implements SuggestTire {


    Set<String> uniqCheck = new HashSet<String>(5000);


    public void addHotWord(String word, int hot, int results) {
        Suggest hw = new Suggest(word, hot, results);

        for (String key : hw.generateKeys()) {

            key = key.intern();

            if (uniqCheck.contains(key)) {
                delete(key);
            } else {
                uniqCheck.add(key);
            }

            insert(key, hw);
        }

    }

    @Override
    public List<Suggest> suggestBy(String prefix, int limit) {


        RadixTreeNode<Suggest> node = searchPefix(prefix, root);


        List<Suggest> result = Collections.emptyList();

        if (node != null) {


            TopN<Suggest> topN = new TopN<Suggest>(limit);

            Set<Suggest> allHits = new HashSet<Suggest>(40);

            selectNode(node, allHits, topN);


            List<Suggest> hits = topN.getAll();
            result = new ArrayList<Suggest>(hits.size());

            for (int j = hits.size() - 1; j >= 0; j--) {
                result.add(hits.get(j));//.getVal()
            }


        }


        return result;
    }


    private void selectNode(RadixTreeNode<Suggest> node, Set<Suggest> allHits, TopN<Suggest> topN) {
        if (node.isReal() && allHits.add(node.getValue())) {
            topN.insert(node.getValue());
        }

        for (RadixTreeNode<Suggest> n : node.getChildern()) {
            selectNode(n, allHits, topN);
        }
    }

    public static void main(String[] args) {
        RadixSuggest tire = new RadixSuggest();

        tire.addHotWord("周杰伦", 123, 0);
        tire.addHotWord("周杰伦", 123, 0);
        tire.addHotWord("周润发", 100, 0);
        tire.addHotWord("周星驰", 110, 0);
        tire.addHotWord("zzz~", 1010, 0);
//        tire.addHotWord("zhoujielun", 123, 0);
//        tire.addHotWord("zhoujie", 123, 0);
//        tire.addHotWord("zhoujielunde", 123, 0);

        System.out.println(
                tire.suggestBy("z", 3)
        );
    }
}
