package cn.ldsnb.faceshow.down;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class MyFileUtil {


    public static byte[] getKey(String path){
        byte [] bytes = new byte[16];
        try {
            //D:/faceshow/20210603/724013b037ade9007de7c8c85fd227a3/enc.key
            FileInputStream fileInputStream = new FileInputStream(path);

            int read = fileInputStream.read(bytes);
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static void decode(String targetPathStr,String savePathStr,String keyPathStr){
        byte [] bytes = new byte[16];
        bytes = "1102030405060708".getBytes();
        AES aes = new AES(Mode.CTS, Padding.PKCS5Padding, getKey(keyPathStr), bytes);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(targetPathStr);

        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] decrypt = aes.decrypt(fileInputStream);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(savePathStr);
            fileOutputStream.write(decrypt);
            fileInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void readDir(String path, List<File> fileList) {

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
     * 解密ts文件
     * @param path
     */
    public static void decode(String path){
        List<File> list = new ArrayList<>();
        readDir(path,list);
        String keyPathStr="";
        for(int i=0;i<list.size();i++){
            File file = list.get(i);
            if(file.getName().endsWith("enc.key")){
                keyPathStr = file.getPath();
            }
            if(file.getName().endsWith("new.ts")){
                return;
            }
        }
        for(int i=0;i<list.size();i++){
            File file = list.get(i);
            if(file.getName().endsWith(".ts")){
                decode(file.getPath(),file.getPath(),keyPathStr);
            }
        }
        log.info("解密完成，开始合并");
        //获取合并文件命令
        String mergeOrder = getMergeOrder(path);
        try {
            //执行合并文件命令
            executeCmd(mergeOrder);
            log.info("合并完成");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String executeCmd(String command) throws IOException {
        System.out.println("Execute command : " + command);
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("cmd /c " + command);
        BufferedReader
            br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
        String line = null;
        StringBuilder build = new StringBuilder();
        while ((line = br.readLine()) != null) {
            System.out.println(line);
            build.append(line);
        }
        return build.toString();
    }

    public static String getMergeOrder(String path){
        //d:/faceshowvip/hg/01
        String s1 = path.replaceAll("/", "\\\\");
        //copy /b D:\faceshowvip\hg\01\*.ts D:\faceshowvip\hg\01\new.ts
        StringBuilder stringBuilder = new StringBuilder("copy /b ");
        stringBuilder.append(s1+"\\*.ts ");
        stringBuilder.append(s1+"\\new.ts");
        return  stringBuilder.toString();
    }

    public static void main(String[] args) {
        String mergeOrder = getMergeOrder("d:/faceshowvip/hg/01");
        System.out.println(mergeOrder);
    }
}
