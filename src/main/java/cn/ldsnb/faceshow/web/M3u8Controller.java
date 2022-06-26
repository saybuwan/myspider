package cn.ldsnb.faceshow.web;

import cn.ldsnb.RetResponse.AResponse;
import cn.ldsnb.faceshow.service.impl.M3u8ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("m3u8")
public class M3u8Controller {
    @Autowired
    private M3u8ServiceImpl m3u8Service;

    @PostMapping("deal")
    public AResponse getItemList(@RequestParam String url){
        m3u8Service.deal("d:/faceshowvip");
        return null;
    }

    @PostMapping("downByM3U8Url")
    public AResponse downByM3U8Url(@RequestParam String url,
                                   @RequestParam String path){
        m3u8Service.downByM3U8Url(url,path);
        return null;
    }
}
