package cn.com.tzy.springbootwebapi;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.com.tzy.springbootentity.export.entity.UserExportModel;
import cn.com.tzy.springbootstarterautopoi.handler.AbstractExcelDictHandler;
import cn.com.tzy.springbootstarterautopoi.handler.ExcelDataHandlerImpl;
import cn.com.tzy.springbootstarterautopoi.style.ExcelExportStylerBorderImpl;
import cn.com.tzy.springbootstarterautopoi.utils.ChangeExcelExportUtil;
import cn.com.tzy.springbootstarterlogscore.utils.IPUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class SpringBootWebApiApplicationTests {

    @Test
    void contextLoads() throws IOException {
        List<UserExportModel> dataList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            UserExportModel build = UserExportModel.builder()
                    .userName("姓名"+i)
                    .nickName("昵称"+i)
                    .loginAccount("账号"+i)
                    .password("密码"+i)
                    .credentialssalt("加盐"+i)
                    .imageUrl("图片"+i)
                    .phone("手机号"+i)
                    .gender(1)
                    .idCard("61012519950823432"+i)
                    .provinceId(i)
                    .cityId(i)
                    .areaId(i)
                    .address("地址"+i)
                    .memo("备注"+i)
                    .loginLastTime(new Date())
                    .updateUserId((long) i)
                    .updateTime(new Date())
                    .createUserId((long) i)
                    .createTime(new Date())
                    .build();
            dataList.add(build);
        }
        ExportParams exportParams = new ExportParams("问卷", "数据");
        exportParams.setAddIndex(true);
        exportParams.setSecondTitle("创建人：admin");
        exportParams.setStyle(ExcelExportStylerBorderImpl.class);
        exportParams.setExclusions( new String[]{"登录时间","修改人编号","修改时间","修改人编号"});
        AbstractExcelDictHandler bean = SpringUtil.getBean(AbstractExcelDictHandler.class);
        exportParams.setDictHandler(bean);//插入字典处理类
        ExcelDataHandlerImpl<UserExportModel> excelDataHandler = new ExcelDataHandlerImpl<UserExportModel>();
        excelDataHandler.setNeedHandlerFields(new String[]{"电话","人员名称","密码","身份证号","居住地址"});
        exportParams.setDataHandler(excelDataHandler);//数据加敏处理
        Workbook workbook = ChangeExcelExportUtil.exportExcel(exportParams, UserExportModel.class, dataList);
        FileOutputStream fos = new FileOutputStream("D:/home/excel/ExcelExportForMap.tt.xls");
        workbook.write(fos);
        fos.close();
    }

    @Test
    public void test() throws IOException {
        String ipAdder = IPUtil.getIpAdder("106.14.122.20");
        System.out.println(ipAdder);
    }

}
