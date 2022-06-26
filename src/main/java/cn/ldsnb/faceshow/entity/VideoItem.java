package cn.ldsnb.faceshow.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("videoitem")
public class VideoItem {


    private  int id;
    private  String title;
    private  String url;
    private  String cid;
    @TableField("encryptUrl")
    private  String encryptUrl;
    private  String sl ;
    @TableField("tagNames")
    private  String tagNames;
    @TableField("tagIds")
    private  String tagIds;
    @TableField("playedCount")
    private  String playedCount;
    private  String duration;
    private  String vip;
    private  String m3u8;
    private  String key;
    @TableField("encryptimage")
    private  String encryptImage;
    @TableField("urlimage")
    private  String urlImage;
    @TableField(value = "createdate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createdate;
}
