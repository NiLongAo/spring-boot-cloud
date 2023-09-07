package cn.com.tzy.springbootstarternetty.msg.model;

import cn.com.tzy.springbootstarternetty.msg.Message;
import cn.com.tzy.springbootstarternetty.msg.MsgCode;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class Msg100000039 extends Message {
    @Override
    public int getMsgCode() {
        return MsgCode.MSG_100000039.getCode();
    }

    @Override
    public boolean checkCRC() {return MsgCode.MSG_100000039.getOnCrc();}


    private String id;
    private String name;
    private Integer age;


    @Override
    public void readData(ByteBuf buffer){
        id = readString(buffer);
        name = readString(buffer);
        age = buffer.readInt();
    }

    @Override
    public void writeData(ByteBuf buffer){
        writeString(buffer,id);
        writeString(buffer,name);
        buffer.writeInt(age);
    }
}
