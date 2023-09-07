package cn.com.tzy.spingbootstartermybatis.core.mapper.handler;

import cn.com.tzy.springbootcomm.common.bean.Base;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;
import java.util.Objects;

/**
 * 通用参数填充实现类
 *
 * 如果没有显式的对通用参数进行赋值，这里会对通用参数进行填充、赋值
 *
 * @author hexiaowu
 */
public class DefaultDBFieldHandler implements MetaObjectHandler {



    @Override
    public void insertFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof Base) {
            Base baseDO = (Base) metaObject.getOriginalObject();

            Date current = new Date();
            // 创建时间为空，则以当前时间为插入时间
            if (Objects.isNull(baseDO.getCreateTime())) {
                baseDO.setCreateTime(current);
            }
            // 更新时间为空，则以当前时间为更新时间
            if (Objects.isNull(baseDO.getUpdateTime())) {
                baseDO.setUpdateTime(current);
            }

            Long userId = JwtUtils.getUserId();
            // 当前登录用户不为空，创建人为空，则当前登录用户为创建人
            if (Objects.nonNull(userId) && Objects.isNull(baseDO.getCreateUserId())) {
                baseDO.setCreateUserId(userId);
            }
            // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
            if (Objects.nonNull(userId) && Objects.isNull(baseDO.getUpdateUserId())) {
                baseDO.setUpdateUserId(userId);
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时间为空，则以当前时间为更新时间
        Object modifyTime = getFieldValByName("updateTime", metaObject);
        if (Objects.isNull(modifyTime)) {
            setFieldValByName("updateTime", new Date(), metaObject);
        }

        // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
        Object modifier = getFieldValByName("updateUserId", metaObject);
        Long userId = JwtUtils.getUserId();
        if (Objects.nonNull(userId) && Objects.isNull(modifier)) {
            setFieldValByName("updateUserId", userId, metaObject);
        }
    }
}
