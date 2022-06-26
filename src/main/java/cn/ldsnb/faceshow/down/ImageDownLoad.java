package cn.ldsnb.faceshow.down;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ImageDownLoad {


    public static void main(String[] args) {
//        Map<String,String> map = new HashMap<>();
//
//        getDownLoaded("d:/uploadfile",map);
//        System.out.println(map.size());
        try {
           // down("https://oss.fstsc.xyz/hls/20220604/0dcde6e7c4ff854a8882497529fa7ddb/film_00063.ts");
            down("https://vs4vs.com/hls/20220604/0dcde6e7c4ff854a8882497529fa7ddb/enc.key");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public  static  void getDownLoaded(String path, Map<String,String> map) {

        File file = new File(path);
        File[] files = file.listFiles();
        for(int i = 0 ;i<files.length;i++){

            if (files[i].isDirectory()){
                getDownLoaded(files[i].getPath(),map);
            }else{
                System.out.println(files[i].getName());
                map.put(files[i].getName(),null);
            }
        }

    }



    public static void down(String strUrl) throws IOException {

        //String strUrl = "https://oss.fstsc.xyz/hls/20220604/0dcde6e7c4ff854a8882497529fa7ddb/film_00000.ts";
        FileOutputStream op=null;
        InputStream inStream=null;
        ByteArrayOutputStream outStream=null;
        try {
            HTTPSTrustManager.retrieveResponseFromServer(strUrl);
            URL url = new URL(strUrl);

            //构造连接
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            //这个网站要模拟浏览器才行
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");

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
            String savePath = getSavePath(strUrl);
            File file = new File("D:/file/a.ts");
            System.out.println(savePath);
             op = new FileOutputStream(file);

            op.write(outStream.toByteArray());

            op.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            op.close();
            System.out.println("关闭资源");
            inStream.close();
            outStream.close();
        }






    }

    public static  String  getSavePath(String strUrl){
        //String strUrl = "https://p.xiurenb.net/uploadfile/202203/8/64164452591.jpg";
        String substring = strUrl.substring(21, strUrl.length());
        String substring1 = substring.substring(0, substring.lastIndexOf("/"));
        String strPath = "d:/"+substring1+"/";

        Path path = Paths.get(strPath);
        File file = new File(strPath);
        try {

            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(substring1);
        return "d:/"+substring;
    }
    public static class HTTPSTrustManager implements X509TrustManager {
        static HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                        + session.getPeerHost());
                return true;
            }
        };

        public static  String retrieveResponseFromServer(final String url) {
            HttpURLConnection connection = null;

            try {
                URL validationUrl = new URL(url);
                trustAllHttpsCertificates();
                HttpsURLConnection.setDefaultHostnameVerifier(hv);

                connection = (HttpURLConnection) validationUrl.openConnection();
                final BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));

                String line;
                final StringBuffer stringBuffer = new StringBuffer(255);

                synchronized (stringBuffer) {
                    while ((line = in.readLine()) != null) {
                        stringBuffer.append(line);
                        stringBuffer.append("\n");
                    }
                    return stringBuffer.toString();
                }

            } catch (final IOException e) {
                System.out.println(e.getMessage());
                return null;
            } catch (final Exception e1) {
                System.out.println(e1.getMessage());
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        public  static  void trustAllHttpsCertificates() throws Exception {
            TrustManager[] trustAllCerts = new TrustManager[1];
            TrustManager tm = new miTM();
            trustAllCerts[0] = tm;
            SSLContext sc = SSLContext
                    .getInstance("SSL");
            sc.init(null, trustAllCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc
                    .getSocketFactory());
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        static class miTM implements TrustManager,
                X509TrustManager {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public boolean isServerTrusted(
                    X509Certificate[] certs) {
                return true;
            }

            public boolean isClientTrusted(
                    X509Certificate[] certs) {
                return true;
            }

            public void checkServerTrusted(
                    X509Certificate[] certs, String authType)
                    throws CertificateException {
                return;
            }

            public void checkClientTrusted(
                    X509Certificate[] certs, String authType)
                    throws CertificateException {
                return;
            }
        }
    }

}
