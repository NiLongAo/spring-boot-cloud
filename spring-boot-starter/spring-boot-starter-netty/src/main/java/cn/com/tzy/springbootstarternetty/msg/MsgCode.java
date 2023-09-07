package cn.com.tzy.springbootstarternetty.msg;


import cn.com.tzy.springbootstarternetty.msg.model.Msg100000039;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum MsgCode {
    MSG_100000039(100000039, true,Msg100000039.class),

    ;

    private static final Map<Integer, Class> map = new HashMap<Integer, Class>();
    private static final List<Integer> inCodeCrcList = new ArrayList<>();

    static {
        for (MsgCode e : MsgCode.values()) {
            if(e.getOnCrc()){
                inCodeCrcList.add(e.getCode());
            }
            map.put(e.code, e.clazz);
        }
    }

    private final int code;

    private final boolean onCrc;
    private final Class clazz;

    private MsgCode(int code,boolean onCrc, Class clazz) {
        this.code = code;
        this.onCrc = onCrc;
        this.clazz = clazz;
    }
    public final int getCode() {
        return code;
    }


    public final boolean getOnCrc() {
        return onCrc;
    }

    public static Class get(int code) {
        return map.get(code);
    }

    public static List<Integer> getInCodeCrcList() {
        return inCodeCrcList;
    }
}