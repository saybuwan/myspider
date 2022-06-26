package cn.ldsnb;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import sun.security.krb5.internal.crypto.Aes128;

public class Test {
    public static void main(String[] args) {
        List<File> list = new ArrayList<>();
        readDir("D:/faceshowvip/hg/01",list);
        System.out.println(list);
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

    }


    public static byte[] getKey(String path){
        byte [] bytes = new byte[16];
        try {
            //D:/faceshow/20210603/724013b037ade9007de7c8c85fd227a3/enc.key
            FileInputStream fileInputStream = new FileInputStream(path);

            int read = fileInputStream.read(bytes);
            System.out.println(bytes);
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


}
