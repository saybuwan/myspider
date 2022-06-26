package cn.ldsnb.faceshow.service.impl;

import cn.hutool.http.HttpUtil;
import cn.ldsnb.faceshow.down.GetUrlUtil;
import cn.ldsnb.faceshow.down.MyFileUtil;
import cn.ldsnb.faceshow.entity.M3u8;
import cn.ldsnb.faceshow.entity.VideoItem;
import cn.ldsnb.faceshow.mapper.M3u8Mapper;
import cn.ldsnb.utils.ThreadPoolMonitor;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class M3u8ServiceImpl {
    @Autowired
    private M3u8Mapper m3u8Mapper;



    public void deal(String path){

        List<File> m3u8List = this.readM3u8FromDir(path);
        BlockingQueue blockingQueue= new LinkedBlockingDeque();

        ThreadPoolMonitor threadPoolMonitor = new ThreadPoolMonitor(10, 12, 30, TimeUnit.MILLISECONDS, blockingQueue, "图片下载");

        for (int i=m3u8List.size()-1;i>0;i--){
            File file = m3u8List.get(i);
            threadPoolMonitor.execute(()->parseM3u8(file.getPath()));

        }
//        m3u8List.forEach(item -> {
//            threadPoolMonitor.execute(()->parseM3u8(item.getPath()));
////            System.out.println(item.getPath());
////            parseM3u8(item.getPath());
//        });
        System.out.println(m3u8List);
    }
    /**
     * 获取m3u8类型的文件
     * @param path
     */
    public  List<File> readM3u8FromDir(String path) {
        List<File> fileList = new ArrayList<>();
        GetUrlUtil.readDir(path, fileList);
        return  fileList.stream().filter(i -> i.getName().endsWith("m3u8")).collect(
            Collectors.toList());
    }

    public  List<String> parseM3u8(String path) {
        List<String> tsList = new ArrayList<>();

        try {
            List<String> lineList = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            String iv = "";
            M3u8 m3u8 = new M3u8();
            for(int i = 0 ;i<lineList.size();i++){

                String line = lineList.get(i);
                if(line.startsWith("#EXT-X-KEY")){
                    String[] split = line.split(",");
                    String s = split[2];
                    iv=s.substring(3,s.length());
                    m3u8.setIv(iv);
                    //获取m3u8中的iv值
                }
                if(line.endsWith(".ts")){
                    //获取视频片段路径
                    m3u8.setTsurl(line);
                    m3u8.setM3u8uri(path);
                    m3u8.setCreatedate(new Date());
                    m3u8.setStatus("0");
                    EntityWrapper<M3u8> ew = new EntityWrapper<M3u8>();
                    ew.eq("tsurl",line);
                    List<M3u8> m3u8s = m3u8Mapper.selectList(ew);
                    if(m3u8s.size()>0){
                        log.info("已存在");
                       // log.error("已存在");
                    }else{
                        m3u8Mapper.insert(m3u8);
                    }

                }


            }

            System.out.println(lineList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void downByM3U8Url(String url,String pathStr){

        //如果该保存路径还未创建，就先创建
        Path path = Paths.get(pathStr);
        try {
            path = Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取该路径下已下载文件
        File file = new File(pathStr);
        File[] files = file.listFiles();

        String keyPrex = url.substring(0,url.lastIndexOf("/"));
        String keyUrl = keyPrex+"/enc.key";
        HttpUtil.downloadFile(url,pathStr);
        HttpUtil.downloadFile(keyUrl,pathStr);
        try {
            List<String> tsList = Files.readAllLines(Paths.get(pathStr+"/index.m3u8"));


            List<String> collect =
                tsList.stream().filter(i -> i.endsWith(".ts")).collect(Collectors.toList());
            String tsUrl = collect.get(0);
            for(File downed : files){
                String name = downed.getName();
               String downedName = tsUrl.substring(0,tsUrl.lastIndexOf("/"))+"/"+name;
                boolean remove = collect.remove(downedName);
                if(remove){
                    log.info(downedName+"已下载");
                }
            }



            BlockingQueue blockingQueue= new LinkedBlockingDeque();
            ThreadPoolMonitor threadPoolMonitor = new ThreadPoolMonitor(5, 8, 30, TimeUnit.MILLISECONDS, blockingQueue, "图片下载");
            collect.forEach(item->{
                threadPoolMonitor.execute(()->{
                    String substring = item.substring(item.lastIndexOf("/"), item.length());
                    HttpUtil.downloadFile(item,pathStr+substring);
                });
            });



           while (threadPoolMonitor.getQueue().size()!=0){


           }
            log.info("下载完毕，开始解密");
            MyFileUtil.decode(pathStr);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }





}
