//package cn.ldsnb.spider;
//
//import java.util.ArrayList;
//import java.util.List;
//import us.codecraft.webmagic.Page;
//import us.codecraft.webmagic.Site;
//import us.codecraft.webmagic.Spider;
//import us.codecraft.webmagic.processor.PageProcessor;
//import us.codecraft.webmagic.selector.Selectable;
//
//public class FaceShowProcessor implements PageProcessor {
//    @Override
//    public void process(Page page) {
//        //从页面发现后续的url地址来抓取
//        page.addTargetRequests(
//            page.getHtml().xpath("//div[@class='page-content']/div[@class='multi-page']/a/@href").all());
//
//
//    }
//
//    @Override
//    public Site getSite() {
//        return null;
//    }
//    public static void main(String[] args) {
//        Spider.create(new FaceShowProcessor())
//            .addUrl("https://qd.anjuke.com/community/") //从https://qd.anjuke.com/community/开始爬取
//            .addPipeline(new FaceShowPricePipeline())  //使用自定义的Pipeline
//            .thread(5)
//            .run();
//        System.out.println("----------抓取了"+"条记录");
//    }
//}
