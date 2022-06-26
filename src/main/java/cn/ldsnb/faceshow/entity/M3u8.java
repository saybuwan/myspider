package cn.ldsnb.faceshow.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("m3u8")
public class M3u8 {
    @TableId
    private  int id;
    @TableField("tsurl")
    private  String tsurl;
    @TableField("createdate")
    private Date createdate;
    private  String status;
    private  String iv;
    @TableField("m3u8uri")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String m3u8uri;
}
