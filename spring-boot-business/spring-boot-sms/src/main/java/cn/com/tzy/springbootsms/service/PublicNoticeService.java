package cn.com.tzy.springbootsms.service;

import cn.com.tzy.springbootcomm.common.vo.PageResult;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootentity.dome.sms.PublicNotice;
import cn.com.tzy.springbootentity.param.sms.PublicNoticeParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

public interface PublicNoticeService extends IService<PublicNotice>{

    /**
     * 获取分页信息
     * @param param
     * @return PageResult
     */
    PageResult findPage(PublicNoticeParam param);

    /**
     * 获取用户分页信息
     * @param param
     * @return PageResult
     */
    PageResult findUserPage(PublicNoticeParam param);

    /**
     * 获取公告详情
     * @param id
     * @return RestResult
     */
    RestResult<?> detail(Long id);

    /**
     * 新增平台公告
     * @param param
     * @return RestResult
     */
    RestResult<?> insert(PublicNoticeParam param);

    /**
     * 修改平台公告
     * @param param
     * @return RestResult
     */
    RestResult<?> update(PublicNoticeParam param);

    /**
     * 删除平台公告
     * @param id
     * @return RestResult
     */
    RestResult<?> remove(Long id);

    /**
     * 用户查看公告并标记已读
     * @param userId
     * @param publicNoticeId
     * @return RestResult
     */
    RestResult<?> userReadNoticeDetail(Long userId,Long publicNoticeId);

    /**
     * 获取时间范围内所有平台公告
     * @return RestResult
     */
    List<PublicNotice> findDateRange(Date date);
}
