package cn.ldsnb.faceshow.down;


import com.google.common.io.Files;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

public class GetUrlUtil {
    public static void main(String[] args) {
        List<File> fileList = new ArrayList<>();
        readDir("D:/faceshow",fileList);
        System.out.println(fileList.size());


    }

    public static List<String> getUrlList() {
        File file = new File("d:/file/index.m3u8");
        List<String> lines = null;
        try {
            lines = Files.readLines(file, Charset.defaultCharset());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void readDir(String path,List<File> fileList) {

        File file = new File(path);

        File[] files = file.listFiles();
        if (files == null) {
            files = new File[] {new File("")};
        }
        System.out.println(files.length);
        for (int i = 0; i < files.length; i++) {

            if (files[i].isDirectory()) {
                readDir(files[i].getPath(),fileList);

            } else {
                System.out.println("文件长度"+files[i].length());
                if (files[i].length() > 0) {

                    fileList.add(files[i]);

                }
            }
        }


    }

    /**
     * 根据url获取保存path路径
     *
     * @param url
     * @return
     */
    public static String getPath(String url) {
        String path = "d:/faceshow/";
        String temp = url.substring(21, url.length());
        //String substring = temp.substring(0, temp.lastIndexOf("/"));
        return path + temp;
    }


    public static boolean down(String strUrl, String savePath) throws IOException {
        System.out.println("当前下载地址：" + strUrl);
        FileOutputStream op = null;
        InputStream inStream = null;
        ByteArrayOutputStream outStream = null;
        URLConnection conn = null;
        try {
            URL url = new URL(strUrl);
            if (strUrl.startsWith("https")) {
                ImageDownLoad.HTTPSTrustManager.retrieveResponseFromServer(strUrl);
                //构造连接
                conn = (HttpsURLConnection) url.openConnection();
            } else {
                conn = (URLConnection) url.openConnection();

            }


            //这个网站要模拟浏览器才行
            conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");

            //打开连接
            conn.connect();
            //打开这个网站的输入流
            inStream = conn.getInputStream();

            //用这个做中转站 ，把图片数据都放在了这里，再调用toByteArray()即可获得数据的byte数组
            outStream = new ByteArrayOutputStream();

            //用这个是很好的，不用一次就把图片读到了文件中
            //要是需要把图片用作其他用途呢？所以直接把图片的数据弄成一个变量，十分有用
            //相当于操作这个变量就能操作图片了

            byte[] buf = new byte[402400];
            //为什么是1024？
            //1024Byte=1KB，分配1KB的缓存
            //这个就是循环读取，是一个临时空间，多大都没关系
            //这没有什么大的关系，你就是用999这样的数字也没有问题，就是每次读取的最大字节数。
            //byte[]的大小，说明你一次操作最大字节是多少
            //虽然读的是9M的文件，其实你的内存只用1M来处理，节省了很多空间．
            //当然，设得小，说明I/O操作会比较频繁，I/O操作耗时比较长，
            //这多少会有点性能上的影响．这看你是想用空间换时间，还是想用时间换空间了．
            //时间慢总比内存溢出程序崩溃强．如果内存足够的话，我会考虑设大点．
            int len = 0;
            //读取图片数据
            while ((len = inStream.read(buf)) != -1) {
                outStream.write(buf, 0, len);
            }

            //把图片数据填入文件中

            System.out.println("保存路径：" + savePath);
            Path path = Paths.get(savePath.substring(0, savePath.lastIndexOf("/")));
            Path pathCreate = java.nio.file.Files.createDirectories(path);


            File file = new File(savePath);
            System.out.println(savePath);
            op = new FileOutputStream(file);

            op.write(outStream.toByteArray());

            op.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (op != null) {
                op.close();
            }
            if (inStream != null) {
                inStream.close();
            }
            if (outStream != null) {
                outStream.close();
            }

        }


    }


}


