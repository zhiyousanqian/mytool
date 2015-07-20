package com.ttpod

import com.mongodb.BasicDBObject
import com.mongodb.Bytes
import com.mongodb.Mongo

import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicInteger

/**
 * User: weijie.song@ttpod.com
 * Date: 2015/1/28 19:01
 */
//@Grapes([
//        @Grab('org.mongodb:mongo-java-driver:2.9.2'),
//        @GrabConfig(systemClassLoader = true)
//])
class RsyncSearchSongs {
    static def mongo = new Mongo("10.0.5.45", 27017)
//    static def mongo = new Mongo("58.241.28.215", 57017)
    static def songsColl = mongo.getDB("ttpod_songs").getCollection("songs")
    static def albumColl = mongo.getDB("ttpod_album").getCollection("albums")
    static def albumItemColl = mongo.getDB("ttpod_album").getCollection("albumItems")
    static def songFileColl = mongo.getDB("ttpod_songs").getCollection("songsFile")
    static def singerColl = mongo.getDB("ttpod_singer").getCollection("singer")
    static def videoColl = mongo.getDB("ttpod_video").getCollection("videos")
    static def searchViewColl = mongo.getDB("ttpod_view").getCollection("search_songs_view_tmp")
    static def index = mongo.getDB("ttpod_view").getCollection("index")
    static BlockingQueue queue = new LinkedBlockingQueue();
    static BlockingQueue songQueue = new LinkedBlockingQueue();
    static def threadSize = 20
    static ThreadPoolExecutor EXE = Executors.newFixedThreadPool(threadSize) as ThreadPoolExecutor

    public static void main(String[] args) {
        println("sync song view start!")
        AtomicInteger production = new AtomicInteger(0)
        AtomicInteger consumption = new AtomicInteger(0)
//        def id = 0

        Thread.start {
            while (true) {
                println("生成：" + production.get())
                println("消费：" + consumption.get())
                println("队列：" + queue.size())
                println("线程池：" + EXE.activeCount)
                Thread.sleep(10000)
            }
        }

        def indexData = index.findOne([_id: "search_view_updateBy"] as BasicDBObject)
        def timestamps = 1424400000;
        if (indexData) {
            timestamps = indexData.modifiedAt
        }
        index.save([_id: "search_view_updateBy", modifiedAt: System.currentTimeMillis()/1000 as int] as BasicDBObject)
        def taskSearchFlag = false;

        Thread.start {
            try {
//                songsColl.find([_id: [$gte: id]] as BasicDBObject).sort([_id: 1] as BasicDBObject).batchSize(10000).addOption(Bytes.QUERYOPTION_NOTIMEOUT).each {
//                List songView = searchViewColl.find([] as BasicDBObject, [modified_at: 1] as BasicDBObject).sort([modified_at: -1] as BasicDBObject).limit(1).toArray()
//                int timestamps = songView.size() > 0 ? songView.get(0).modified_at : 0

                songsColl.find([modifyAt: [$gte: timestamps]] as BasicDBObject).sort([_id: 1] as BasicDBObject).batchSize(10000).addOption(Bytes.QUERYOPTION_NOTIMEOUT).each {
                    def song ->
                        while (true) {
                            if (queue.size() < 10000) {
                                queue.put(song)
                                production.incrementAndGet()
                                break
                            }
                        }
                }
                taskSearchFlag = true;
            } catch (Exception e) {
                e.printStackTrace()
            }
        }

        Thread.start {
            while (true) {
                if (EXE.activeCount < threadSize) {
                    EXE.execute {
                        def song = queue.take()
                        def data = renderSong(song)
                        songQueue.put(data)
                        consumption.incrementAndGet()
                    }
                } else {
                    Thread.sleep(1)
                }
            }
        }

//        List list = new CopyOnWriteArrayList()

//        int i = 0
        Thread.start {
            while (true) {
                if (taskSearchFlag && songQueue.size() == 0) {
                    println("sync song view over! date: ${new Date()}")
                    Thread.sleep(1000)
                    System.exit(0)
                } else {
                    Thread.sleep(10000)
                }
            }
        }
        while (true) {
            def song = songQueue.take()
            searchViewColl.save(song as BasicDBObject)
//            list.add(song)
//            if (list.size() > 1000) {
//                i++
//                println("finish:" + i * 1000)
//                searchViewColl.insert(list)
//                list = new CopyOnWriteArrayList()
//            }
//            if (songQueue.size() == 0) {
//                searchViewColl.insert(list)
//                break
//            }
        }
        println("sync song view over! date: ${new Date()}")
        System.exit(0)
    }

    static def renderSong(def o) {
        def albumId = 0
        def albumName = null
        def releaseAt = 0
        def albumItem = albumItemColl.find([songId: o._id] as BasicDBObject, [albumId: 1] as BasicDBObject).sort([_id: -1] as BasicDBObject).limit(1).toArray()
        if (albumItem.size() > 0) {
            albumId = albumItem.get(0).albumId
            def album = albumColl.findOne([_id: albumId] as BasicDBObject, [name: 1, publishDate: 1] as BasicDBObject)
            if (album) {
                albumName = album.name
                releaseAt = album.publishDate
            }
        }

        List songsFile = []
        songFileColl.find([songId: o._id] as BasicDBObject, [duration: 1, path: 1, bitRate: 1, size: 1, suffix: 1] as BasicDBObject).each {
            songsFile.add(["duration": it.duration,
                           "path"    : it.path,
                           "bit_rate": it.bitRate,
                           "size"    : it.size,
                           "suffix"  : it.suffix])
        }

        def singerName = null
        def singer = singerColl.findOne([_id: o.singerId] as BasicDBObject, [name: 1] as BasicDBObject)
        if (singer) {
            singerName = singer.name
        }

        def videoId = 0
        def video = videoColl.findOne([songId: o._id] as BasicDBObject, [_id: 1] as BasicDBObject)
        if (video) {
            videoId = o._id
        }

        return ["_id"         : o._id,
                "album_id"    : albumId,
                "album_name"  : albumName,
                "company_id"  : o.companyId,
                "down_list"   : songsFile,
                "lyric_id"    : o.lyricId,
                "modified_at" : o.modifyAt,
                "name"        : o.name,
                "remarks"     : o.remarks,
                "pick_count"  : o.favorites==null?0:o.favorites,
                "published_at": o.createAt,
                "singer_id"   : o.singerId,
                "singer_name" : singerName,
                "status"      : o.status,
                "video_id"    : videoId,
                "release_at"  : releaseAt] as BasicDBObject
    }
}


