package cn.com.tzy.springbootcomm.constant;

import cn.com.tzy.springbootcomm.utils.AppUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NotNullMap extends HashMap {

    public NotNullMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public NotNullMap(int initialCapacity) {
        super(initialCapacity);
    }

    public NotNullMap() {
    }

    public NotNullMap(Map m) {
        super(m);
    }

    public void putString(String k, String v) {
        putString(k, v, "");
    }

    public void putByte(String k, Byte v) {
        putByte(k, v, (byte) 0);
    }

    public void putShort(String k, Short v) {
        putShort(k, v, (short) 0);
    }

    public void putInteger(String k, Integer v) {
        putInteger(k, v, 0);
    }

    public void putLong(String k, Long v) {
        putLong(k, v, (long) 0);
    }

    public void putFloat(String k, Float v) {
        putFloat(k, v, (float) 0);
    }

    public void putDouble(String k, Double v) {
        putDouble(k, v, (double) 0);
    }

    public void putString(String k, String v, String defaultValue) {
        if(v == null) {
            v = defaultValue;
        }
        put(k, v);
    }

    public void putByte(String k, Byte v, byte defaultValue) {
        if(v == null) {
            v = defaultValue;
        }
        put(k, v);
    }

    public void putShort(String k, Short v, short defaultValue) {
        if(v == null) {
            v = defaultValue;
        }
        put(k, v);
    }

    public void putInteger(String k, Integer v, int defaultValue) {
        if(v == null) {
            v = defaultValue;
        }
        put(k, v);
    }

    public void putLong(String k, Long v, long defaultValue) {
        if(v == null) {
            v = defaultValue;
        }
        put(k, v);
    }

    public void putFloat(String k, Float v, float defaultValue) {
        if(v == null) {
            v = defaultValue;
        }
        put(k, v);
    }

    public void putDouble(String k, Double v, double defaultValue) {
        if(v == null) {
            v = defaultValue;
        }
        put(k, v);
    }

    public void putDate(String k, Date v) {
        if(v == null) {
            put(k, v);
        } else {
            put(k, DateFormatUtils.format(v, Constant.DATE_FORMAT));
        }
    }

    public void putDateTime(String k, Date v) {
        if(v == null) {
            put(k, v);
        } else {
            put(k, DateFormatUtils.format(v, Constant.DATE_TIME_FORMAT));
        }
    }

    public void putMobileMask(String k, String v) {
        if(v == null) {
            put(k, v);
        } else {
            put(k, AppUtils.getMobileMask(v));
        }

    }
}
