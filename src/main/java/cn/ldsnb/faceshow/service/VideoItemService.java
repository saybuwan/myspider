package cn.ldsnb.faceshow.service;

import cn.ldsnb.faceshow.entity.VideoItem;
import java.util.List;
import org.springframework.stereotype.Service;


public interface VideoItemService {
      void save(VideoItem videoItem);
      List<VideoItem> getItemList(String url);
      void downM3u8();
}
