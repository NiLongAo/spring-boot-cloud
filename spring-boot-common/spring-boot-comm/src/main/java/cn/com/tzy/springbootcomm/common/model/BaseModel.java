package cn.com.tzy.springbootcomm.common.model;

import io.swagger.annotations.ApiModel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("权限分配")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class BaseModel {
    /**
     * 参数校验分组：分页
     */
    public @interface page {
    }

    /**
     * 参数校验分组：查询所有
     */
    public @interface list {
    }

    /**
     * 参数校验分组：增加
     */
    public @interface add {
    }

    /**
     * 参数校验分组：编辑
     */
    public @interface edit {
    }

    /**
     * 参数校验分组：删除
     */
    public @interface delete {
    }

    /**
     * 参数校验分组：详情
     */
    public @interface detail {
    }

    /**
     * 参数校验分组：导出
     */
    public @interface export {
    }
    /**
     * 参数校验分组：树
     */
    public @interface tree {
    }

    /**
     * 参数校验分组：修改状态
     */
    public @interface updateStatus {
    }

    /**
     * 参数校验分组：批量删除
     */
    public @interface batchDelete {
    }
}
