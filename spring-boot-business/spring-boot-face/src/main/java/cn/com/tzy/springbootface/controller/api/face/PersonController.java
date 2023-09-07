package cn.com.tzy.springbootface.controller.api.face;
import cn.com.tzy.springbootcomm.common.vo.RespCode;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootface.service.PersonService;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
* (face_person)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/face_person")
public class PersonController extends ApiController {
    /**
    * 服务对象
    */
    @Resource
    private PersonService personService;

    /**
    * 通过主键查询单条数据
    *
    * @param id 主键
    * @return 单条数据
    */
    @GetMapping("selectOne")
    public RestResult<?> selectOne(Integer id) {
        return RestResult.result(RespCode.CODE_0.getValue(),null,personService.getById(id));
    }

}
