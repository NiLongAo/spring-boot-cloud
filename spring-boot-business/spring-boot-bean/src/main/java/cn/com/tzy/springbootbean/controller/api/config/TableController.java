package cn.com.tzy.springbootbean.controller.api.config;

import cn.com.tzy.springbootbean.service.api.TableService;
import cn.com.tzy.springbootcomm.common.vo.RestResult;
import cn.com.tzy.springbootstartercloud.api.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController("ApiConfigTableController")
@RequestMapping(value = "/api/config/table")
public class TableController extends ApiController {

    @Autowired
    private TableService tableService;

    /**
     * 租户信息下拉展示(动态搜索数据源)
     * @return
     */
    @GetMapping("select_dice_data")
    @ResponseBody
    public RestResult<?> selectDiceData(
            @RequestParam(value = "table") String table,
            @RequestParam(value = "text")String text,
            @RequestParam("code") Integer code
    ){
        return tableService.selectDiceData(table,text,code);
    }

}
