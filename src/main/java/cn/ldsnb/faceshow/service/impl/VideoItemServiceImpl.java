package cn.ldsnb.faceshow.service.impl;

import cn.hutool.http.HttpUtil;
import cn.ldsnb.faceshow.down.GetUrlUtil;
import cn.ldsnb.faceshow.entity.VideoItem;
import cn.ldsnb.faceshow.mapper.VideoItemMapper;
import cn.ldsnb.faceshow.service.VideoItemService;
import cn.ldsnb.utils.ThreadPoolMonitor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import javax.lang.model.element.VariableElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class VideoItemServiceImpl implements VideoItemService {

    @Autowired
    private VideoItemMapper videoItemMapper;
    @Override
    public void save(VideoItem videoItem) {
        videoItem.setCid("1");
        videoItem.setId(1);
        videoItem.setDuration("1");
        videoItem.setEncryptUrl("1");
        videoItem.setCreatedate(new Date());
        videoItemMapper.insert(videoItem);
    }

    @Override
    public List<VideoItem> getItemList(String url) {
        String post = HttpUtil.post(
            url,
            "");
        JSONObject jsonObject = JSONObject.parseObject(post);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray list = data.getJSONArray("list");
        list.stream().forEach(item->{
            VideoItem videoItem =
                JSONObject.parseObject(JSONObject.toJSONString(item), VideoItem.class);
            videoItem.setCreatedate(new Date());
            videoItemMapper.insert(videoItem);
        });
        return null;
    }

    @Override
    public void downM3u8() {
        EntityWrapper<VideoItem> ew = new EntityWrapper<VideoItem>();
        ew.eq("m3u8","0");
        ew.notLike("sl","hbl");
        List<VideoItem> videoItems = videoItemMapper.selectList(ew);
        log.info("加载到{}条未下载的m3u8",videoItems.size());
        BlockingQueue blockingQueue= new LinkedBlockingDeque();

        ThreadPoolMonitor threadPoolMonitor = new ThreadPoolMonitor(30, 40, 30, TimeUnit.MILLISECONDS, blockingQueue, "图片下载");

        videoItems.forEach(item->{
//            DownVideoItem downVideoItem = new DownVideoItem(item);
//            downVideoItem.run();
          threadPoolMonitor.execute(new DownVideoItem(item));
        });

    }

    @Data
    @AllArgsConstructor
    class DownVideoItem implements Runnable{
        private VideoItem videoItem;

        @Override
        public void run() {
            EntityWrapper<VideoItem> saveEw = new EntityWrapper<VideoItem>();
            saveEw.eq("id",videoItem.getId());
            String path = GetUrlUtil.getPath(videoItem.getSl());
            try {
                //下载m3u8文件
                boolean down = GetUrlUtil.down(videoItem.getSl(),path);
                videoItem.setM3u8(down?"1":"2");
                videoItemMapper.update(videoItem,saveEw);
            } catch (IOException e) {
                //下载失败
                videoItem.setM3u8("2");
                videoItemMapper.update(videoItem,saveEw);
                e.printStackTrace();
            }
            try {
                //下载图片
                String substring = path.substring(0, path.lastIndexOf("/"));
                String encryptUrl = videoItem.getEncryptUrl();
                String substring1 = encryptUrl.substring(encryptUrl.lastIndexOf("/") , encryptUrl.length());
                boolean down = GetUrlUtil.down(videoItem.getEncryptUrl(),substring+substring1);
                videoItem.setEncryptImage(down?"1":"2");
                videoItemMapper.update(videoItem,saveEw);
            } catch (IOException e) {
                //下载失败
                videoItem.setEncryptImage("2");
                videoItemMapper.update(videoItem,saveEw);
                e.printStackTrace();
            }

            try {
                //下载m3u8文件
                String sl = videoItem.getSl();
                String substring = sl.substring(0, sl.lastIndexOf("/"));
                String pathPix = path.substring(0, path.lastIndexOf("/"));

                boolean down = GetUrlUtil.down(substring+"/enc.key",pathPix+"/enc.key");
                videoItem.setKey(down?"1":"2");
                videoItemMapper.update(videoItem,saveEw);
            } catch (IOException e) {
                //下载失败
                videoItem.setKey("2");
                videoItemMapper.update(videoItem,saveEw);
                e.printStackTrace();
            }
        }
    }


}
