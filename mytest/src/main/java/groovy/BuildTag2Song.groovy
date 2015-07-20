package groovy

//@Grapes([
//        @Grab('org.mongodb:mongo-java-driver:2.12.2'),
//        @Grab('com.ttpod:ttpod-cache:1.3.0'),
//        @Grab('org.apache.curator:curator-recipes:2.4.2')
//])
import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import com.mongodb.Mongo
import com.mongodb.MongoURI
import heap.TopN

import java.util.*

/**
* Created by Administrator on 2015/1/21.
*/
public class BuildTag2Song {
//    static Mongo mongo = new Mongo(new MongoURI("mongodb://10.0.5.145:27018,10.0.5.146:27018,10.0.5.147:27018/?w=1&slaveok=true"));
    static Mongo mongo = new Mongo(new MongoURI("mongodb://113.31.130.5:27132"));

    static DBCollection hot_Coll = mongo.getDB("zookeeper").getCollection("hot_data");

    static DBCollection tag_Coll = mongo.getDB("ttpod_search").getCollection("gd_crawler");


    static DBCollection insert_tag_Coll = mongo.getDB("ttpod_index").getCollection("tag_songs");

//    static String[] dd = ["歌曲", "音乐", "流行", "红歌", "金曲", "最热的", "最火的", "最新的", "铃声", "大全", "精选", "下载"];

    static $$(Map map) { new BasicDBObject(map) };

    static Set<String> songidSet = new HashSet<>();

    static Map<String, Integer> tagId2CountMap = new HashMap<>();

    static Map<String,TopN<Tag>> tag2TopNMap= new HashMap();

    static List<String> standTagList;
    static Map<Integer, String> songidScoreMap;

    static main(a) {
        try {
            standTagList= getStandardTag();
            songidScoreMap = getSongIdScore()
            //tag -> ids
            println "start build SongTag > Count ...."

            buildSongTag2CountMap();

            for(String tagId:tagId2CountMap.keySet()){
                String tag = tagId.split("\\|")[0];
                Integer id = Integer.valueOf(tagId.split("\\|")[1]);
                TopN<Tag> topN = tag2TopNMap.get(tag);

                Tag tag1 = buildTag(tag,id);
                if(topN==null){
                    topN = new TopN<>(200);
                    tag2TopNMap.put(tag,topN);
                }
                topN.insert(tag1);
            }

            println "start build dbObject..."
            List<BasicDBObject> dbList = new ArrayList<>();
            //insert db
            for (String tag : tag2TopNMap.keySet()) {
                BasicDBObject basicDBObject = new BasicDBObject();
                basicDBObject.put("tag", tag);

                TopN<Tag> tagTopN = tag2TopNMap.get(tag);
                if (tagTopN != null) {
                    List<Tag> songList = tagTopN.getAll();

                    List<BasicDBObject> basicDBObjectList = new ArrayList<>();
                    for (int i=songList.size()-1;i>=0 ; i--) {
                        Tag song = songList.get(i);
                        BasicDBObject basicDBObject1 = new BasicDBObject();
                        basicDBObject1.put("name", song.getName());
                        basicDBObject1.put("song_id", song.getId());
                        basicDBObject1.put("listen_count", song.getListen_count());
                        basicDBObject1.put("mark_count", song.getMark_count());
                        basicDBObject1.put("score", song.getScore());
                        basicDBObject1.put("time", 2);
                        basicDBObjectList.add(basicDBObject1);
                    }
                    basicDBObject.put("songs", basicDBObjectList);
                    dbList.add(basicDBObject);
                }
            }
            println "start insert tag size = " +dbList.size();
            insert_tag_Coll.insert(dbList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        println "tagMap size =" + tag2TopNMap.size();
    }

    static Tag buildTag(String tag,Integer id){
        String songInfo = songidScoreMap.get(id);
        Integer mark_count = tagId2CountMap.get(tag+"|"+id);
        Tag tag1 = new Tag(id, songInfo.split("\\|")[1], Integer.valueOf(songInfo.split("\\|")[0]), mark_count);
        return tag1;
    }

    static void buildSongTag2CountMap(){
        println "++++++++++" + tag_Coll.find($$(listen_count: $$($gt: 50), tags: $$('$ne': []))).count();
        DBCursor tag_cur = tag_Coll.find($$(listen_count: $$($gt: 50), tags: $$('$ne': []))).batchSize(10000);
        int a=0;
        while (tag_cur.hasNext()) {
//            if(a%1000==0){
                println "a ="+ a++;
//            }
            BasicDBObject basicDBObject = tag_cur.next();
            List<String> tagList = basicDBObject.get("tags");
            Integer listenCount = basicDBObject.get("listen_count");
//            String desc = basicDBObject.get("desc");
            String title = basicDBObject.get("title");
            if (tagList != null && tagList.size() > 0) {
                for (String standTag : standTagList) {
                     for (String tag : tagList) {
                        if (tag.indexOf(standTag) > -1 || title.indexOf(standTag)> -1) {
                            List<Integer> ids = basicDBObject.get("song_list");
                            updateTagId2CountMap(standTag, ids,listenCount);
                        }
                    }
                }
            }
        }
    }

    static void updateTagId2CountMap(String tag, List<Integer> ids,Integer listen_count) {
        listen_count = listen_count/1000 as int;
        if(listen_count < 1){
            listen_count = 1;
        }

        if (ids == null) return;
        for (Integer id : ids) {
            if(songidScoreMap.containsKey(id)) {
                Integer count = tagId2CountMap.get(tag + "|" + id);
                if (count == null) {
                    tagId2CountMap.put(tag + "|" + id, listen_count);
                } else {
                    tagId2CountMap.put(tag + "|" + id, listen_count + count);
                }
            }

//            String songs = songMap.get(id);
//            if (songs != null && songidSet.add(tag+songs.split("\\|")[1])) {
//                Tag tag1 = new Tag(id, songs.split("\\|")[1], Integer.valueOf(songs.split("\\|")[0]));
//
//            }
        }
    }

    static Map<Integer, String> getSongIdScore() {
        println "-----------------" + hot_Coll.find($$(listen_count: $$($gt: 200), date: 20150121)).count();
        DBCursor hot_cur = hot_Coll.find($$(listen_count: $$($gt: 200), date: 20150121)).batchSize(10000);
        Map<Integer, String> map = new HashMap<>();
        int b = 0;
        while (hot_cur.hasNext()) {
//            if(b%1000==0){
                println "b ="+ b++;
//            }
            BasicDBObject basicDBObject = hot_cur.next();
            String name = basicDBObject.get("name") != null ? basicDBObject.get("name")+"_" +basicDBObject.get("singer_name"): "-"
            map.put(basicDBObject.get("song_id"), basicDBObject.get("listen_count") + "|" + name);
        }
        println "songidscore size = " + map.size();
        return map;
    }

    static List<String> getStandardTag() {
        List<String> tagList = [];
        new File("e://tag.log").eachLine {
            tagList.add(it.trim().toLowerCase());
        }
        println "tagList size =" + tagList.size();
        return tagList;
    }

    //*************************************************************************************
    static class Tag implements Comparable<Tag> {

        String name;
        int id;
        int listen_count;
        int mark_count

        int getMark_count() {
            return mark_count
        }

        void setMark_count(int mark_count) {
            this.mark_count = mark_count
        }

        float getScore() {
            return score
        }

        void setScore(float score) {
            this.score = score
        }
        float score

        public Tag(int id, String name, int listen_count,int mark_count) {
            this.id = id;
            this.name = name;
            this.listen_count = listen_count;
            this.mark_count = mark_count;
            this.score = Math.log(listen_count)*0.5 + Math.log(mark_count);
        }

        public int compareTo(Tag that) {
            return this.score > that.score ? 1 :
                    (this.score == that.score ? 0 : -1);
        }

        String getName() {
            return name
        }

        void setName(String name) {
            this.name = name
        }

        int getId() {
            return id
        }

        void setId(int id) {
            this.id = id
        }

        int getListen_count() {
            return listen_count
        }

        void setListen_count(int listen_count) {
            this.listen_count = listen_count
        }


    }

//   static class TopN<T extends Comparable> {
//        private int n;
//        private Heap<T> minHeap = null;
//
//        public TopN(int n) {
//            this(n, true);
//        }
//
//        public TopN(int n, final boolean originSort) {
//            this.n = n;
//
//            Comparator<T> cmp = originSort ? new Comparator<T>() {
//                public int compare(T o1, T o2) {
//                    return o1.compareTo(o2);
//                }
//            } : new Comparator<T>() {
//                public int compare(T o1, T o2) {
//                    return o2.compareTo(o1);
//                }
//            };
//            minHeap = new SimpleHeap<T>(n, cmp);
//        }
//
//        public void insert(T entry) {
//
//            if (!minHeap.isFull()) {
//                minHeap.insert(entry);
//            } else {
//                T root = minHeap.root();
//                if (entry.compareTo(root) > 0) {
//                    minHeap.removeRoot();
//                    minHeap.insert(entry);
//                }
//            }
//        }
//        public List<T> getAll() {
//            List<T> res = new ArrayList<T>();
//            while (minHeap.size() != 0) {
//                res.add(minHeap.removeRoot());
//            }
//            return res;
//        }
//    }
//
//    class SimpleHeap<T> implements Heap<T> {
//
//
//        public Object[] data;
//        protected int currSize;     //堆当前大小
//
//        Comparator<T> comparator;
//
//        public SimpleHeap(int maxElements, Comparator<T> comparator) {
//            data = new Object[maxElements + 1];
//            this.comparator = comparator;
//        }
//
//        @Override
//        public boolean insert(T elem) {
//            if (isFull()) {
//                return false;
//            }
//
//            currSize++;
//
//            data[currSize] = elem;
//
//            shiftUp(currSize);
//
//            return true;
//        }
//
//        @Override
//        public T removeRoot() {// 删除节点，把尾节点拿过来填空，然后新官上任，接受贫下中农再教育。
//
//            if (size() == 0) {
//                return null;
//            }
//
//            Object result = data[ROOT_INDEX];
//            data[ROOT_INDEX] = data[currSize];
//            data[currSize] = null;
//            --currSize;
//            shiftDown(ROOT_INDEX);//接受贫下中农再教育。
//
//            return (T) result;
//        }
//
//        public void swap(int i, int j) {
//            Object newhotsongs = data[i];
//            data[i] = data[j];
//            data[j] = newhotsongs;
//        }
//
//        @Override
//        public int shiftDown(int idx) {//相当官，接受贫下中农再教育。
//            boolean needSwap = true;
//            /* while not at bottom (hasLeftChild) and either child is smaller
//                 than current swap current with smallest of children */
//            while ( needSwap && hasLeftChild(idx)) {
//                /* compute smaller of existing chilren of current */
//                int minChild = leftChild(idx);
//                if (hasRightChild(idx) && comparator.compare((T) data[rightChild(idx)], (T) data[leftChild(idx)]) < 0)
//                    minChild = rightChild(idx);
//                /* if smallest child is smaller than current then swap */
//                if (comparator.compare((T) data[minChild], (T) data[idx]) < 0) {
//                    swap(idx, minChild);
//                    idx = minChild;
//                } else {
//                    needSwap = false; /* both children are greater - quit */
//                }
//            }
//            return idx;
//        }
//
//        @Override
//        public int shiftUp(int idx) {//比父节点小就上飘
//            /* while not in root and current is smaller than parent
//               swap parent and current */
//            while (idx != ROOT_INDEX && comparator.compare((T) data[idx], (T) data[parent(idx)]) < 0) {
//                swap(idx, parent(idx));
//                idx = parent(idx);
//            }
//            return idx;
//        }
//
//        @Override
//        public T root() {
//            return (T) data[ROOT_INDEX];
//        }
//
//        @Override
//        public int size() {
//            return currSize;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        public int leftChild(int position) {
//            return position << 1;
//        }
//
//        public int rightChild(int position) {
//            return (position << 1) + 1;
//        }
//
//        public int parent(int position) {
//            return position >> 1;
//        }
//
//        private boolean hasLeftChild(int i) {
//            return leftChild(i) <= currSize;
//        }
//
//        private boolean hasRightChild(int i) {
//            return rightChild(i) <= currSize;
//        }
//
//
//        @Override
//        public boolean isFull() {
//            return size() == data.length - 1;
//        }
//    }
//
//    interface Heap<T> {
//
//        int ROOT_INDEX = 1; // lyric.index of root
//
//        boolean insert(T elem);//堆中合适的位置插入新的元素项
//
//        //T remove(int position);//删除position位置的元素项
//        T removeRoot();            //删除堆中最大或最小元素项
//
//        void swap(int i, int j);//交换位置i和位置j处的两个元素
//
//        /**
//         * takes element in lyric.index and moves it down in the heap until the element
//         * below is greater or it hits the bottom
//         *
//         * @return the final lyric.index of the element.
//         */
//        int shiftDown(int idx);            //自顶向下堆化
//
//        /**
//         * Moves the element in lyric.index idx up until the element above is
//         * smaller or it hits the root.
//         *
//         * @return the final lyric.index of the element.
//         */
//        int shiftUp(int idx);            //自底向上堆化
//
//
//        T root();                //返回堆顶元素项
//
//        int size();                    //返回当前堆中元素项的个数
//
//        int leftChild(int position);    //获取position的左子节点位置
//
//        int rightChild(int position);    //获取position的右子节点位置
//
//        int parent(int position);        //获取position的父节点位置
//
//        boolean isFull();
//
//    }
}


