package cn.ldsnb.faceshow.web;


import cn.ldsnb.RetResponse.AResponse;
import cn.ldsnb.faceshow.entity.VideoItem;
import cn.ldsnb.faceshow.service.VideoItemService;
import cn.ldsnb.faceshow.service.impl.VideoItemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("item")
public class VideoItemController {

    @Autowired
    private VideoItemServiceImpl videoItemService;

    @GetMapping("/save")
    public void save(){
        videoItemService.save(new VideoItem());
    }

    /**
     * 传入接口路径，保存电影信息
     * @param url
     * @return
     */
    @PostMapping("getItemList")
    public AResponse getItemList(@RequestParam String url){
        videoItemService.getItemList(url);
        return null;
    }
    @PostMapping("downM3u8")
    public AResponse downM3u8(){
        videoItemService.downM3u8();
        return  null;
    }
}
