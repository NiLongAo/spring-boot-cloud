
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.com.tzy.springbootstarterautopoi.style.ExcelExportStylerBorderImpl;
import cn.com.tzy.springbootstarterautopoi.utils.ChangeExcelExportUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootTest
public class TestUtils {

    List<ExcelExportEntity> colList = new ArrayList<ExcelExportEntity>();
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    @Test
    public void test111() throws IOException {
        ExportParams exportParams = new ExportParams("问卷", "数据");
        exportParams.setStyle(ExcelExportStylerBorderImpl.class);
        Workbook workbook = ChangeExcelExportUtil.exportExcel(exportParams, colList, list);
        FileOutputStream fos = new FileOutputStream("D:/home/excel/ExcelExportForMap.tt.xls");
        workbook.write(fos);
        fos.close();
    }

    @Before
    public void testBefore() {
        //示例1
        colList1();
        list1();
        //示例2
        //colList2();
        //list2();
    }


    private void colList1(){
        List<ColumnList> maps = JSONUtil.toList(columnList, ColumnList.class);
        createColList(colList,maps);
    }

    private void createColList(List<ExcelExportEntity> colList,List<ColumnList> maps){
        for (ColumnList map : maps) {
            ExcelExportEntity entity = new ExcelExportEntity(map.title, map.dataIndex,20);
            if(map.getChildren() != null && map.getChildren().size() > 0){
                List<ExcelExportEntity> colList1 = new ArrayList<>();
                createColList(colList1,map.getChildren());
                entity.setList(colList1);
            }else {
                entity.setNeedMerge(true);
            }
            colList.add(entity);
        }
    }


    private void list1(){
        List<Map> maps = JSONUtil.toList(lineList, Map.class);
        createList(list,colList,maps);

    }
    private void createList(List<Map<String, Object>> list,List<ExcelExportEntity> colList,List<Map> maps){
        for (Map map : maps) {
            Map<String, Object> map1 = new HashMap<>();
            for (ExcelExportEntity entity : colList) {
                String key = String.valueOf(entity.getKey());
                Object val = map.get(key);
                if(entity.getList() != null && entity.getList().size() > 0){
                    List<Map<String, Object>> list11 = new ArrayList<Map<String, Object>>();
                    createList(list11,entity.getList(),maps);
                    map1.put(key,list11);
                }else {
                    map1.put(key,val);
                }
            }
            list.add(map1);
        }



    }



    @Data
    private class ColumnList{
        private String title;
        private String dataIndex;
        private List<ColumnList> children;
    }

    private final String columnList ="[\n" +
            "            {\n" +
            "                \"title\": \"机关名称\",\n" +
            "                \"dataIndex\": \"policeAgencyName\",\n" +
            "                \"bizId\": null,\n" +
            "                \"industryExamineId\": null,\n" +
            "                \"children\": null\n" +
            "            },\n" +
            "            {\n" +
            "                \"title\": \"旅馆业\",\n" +
            "                \"dataIndex\": \"1551772550130827265\",\n" +
            "                \"bizId\": null,\n" +
            "                \"industryExamineId\": null,\n" +
            "                \"children\": [\n" +
            "                    {\n" +
            "                        \"title\": \"分类1\",\n" +
            "                        \"dataIndex\": \"15517725501308272651\",\n" +
            "                        \"bizId\": null,\n" +
            "                        \"industryExamineId\": null,\n" +
                    "                \"children\": [\n" +
                    "                    {\n" +
                    "                        \"title\": \"总数\",\n" +
                    "                        \"dataIndex\": \"1551772550130827265_total\",\n" +
                    "                        \"bizId\": null,\n" +
                    "                        \"industryExamineId\": null,\n" +
                    "                        \"children\": null\n" +
                    "                    },\n" +
                    "                    {\n" +
                    "                        \"title\": \"未列管\",\n" +
                    "                        \"dataIndex\": \"1551772550130827265_not\",\n" +
                    "                        \"bizId\": null,\n" +
                    "                        \"industryExamineId\": null,\n" +
                    "                        \"children\": null\n" +
                    "                    },\n" +
                    "                    {\n" +
                    "                        \"title\": \"已列管\",\n" +
                    "                        \"dataIndex\": \"1551772550130827265_on\",\n" +
                    "                        \"bizId\": null,\n" +
                    "                        \"industryExamineId\": null,\n" +
                    "                        \"children\": null\n" +
                    "                    }\n" +
                    "                ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"title\": \"分类2\",\n" +
            "                        \"dataIndex\": \"15517725501308272643\",\n" +
            "                        \"bizId\": null,\n" +
            "                        \"industryExamineId\": null,\n" +
                    "                \"children\": [\n" +
                    "                    {\n" +
                    "                        \"title\": \"总数\",\n" +
                    "                        \"dataIndex\": \"1551772550130827265_total\",\n" +
                    "                        \"bizId\": null,\n" +
                    "                        \"industryExamineId\": null,\n" +
                    "                        \"children\": null\n" +
                    "                    },\n" +
                    "                    {\n" +
                    "                        \"title\": \"未列管\",\n" +
                    "                        \"dataIndex\": \"1551772550130827265_not\",\n" +
                    "                        \"bizId\": null,\n" +
                    "                        \"industryExamineId\": null,\n" +
                    "                        \"children\": null\n" +
                    "                    },\n" +
                    "                    {\n" +
                    "                        \"title\": \"已列管\",\n" +
                    "                        \"dataIndex\": \"1551772550130827265_on\",\n" +
                    "                        \"bizId\": null,\n" +
                    "                        \"industryExamineId\": null,\n" +
                    "                        \"children\": null\n" +
                    "                    }\n" +
                    "                ]\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"title\": \"分类三\",\n" +
            "                        \"dataIndex\": \"155177255013082726512\",\n" +
            "                        \"bizId\": null,\n" +
            "                        \"industryExamineId\": null,\n" +
                    "                \"children\": [\n" +
                    "                    {\n" +
                    "                        \"title\": \"总数\",\n" +
                    "                        \"dataIndex\": \"1551772550130827265_total\",\n" +
                    "                        \"bizId\": null,\n" +
                    "                        \"industryExamineId\": null,\n" +
                    "                        \"children\": null\n" +
                    "                    },\n" +
                    "                    {\n" +
                    "                        \"title\": \"未列管\",\n" +
                    "                        \"dataIndex\": \"1551772550130827265_not\",\n" +
                    "                        \"bizId\": null,\n" +
                    "                        \"industryExamineId\": null,\n" +
                    "                        \"children\": null\n" +
                    "                    },\n" +
                    "                    {\n" +
                    "                        \"title\": \"已列管\",\n" +
                    "                        \"dataIndex\": \"1551772550130827265_on\",\n" +
                    "                        \"bizId\": null,\n" +
                    "                        \"industryExamineId\": null,\n" +
                    "                        \"children\": null\n" +
                    "                    }\n" +
                    "                ]\n" +
            "                    }\n" +
            "                ]\n" +
            "            }\n" +
            "        ]";


//    private final String columnList ="[\n" +
//            "            {\n" +
//            "                \"title\": \"机关名称\",\n" +
//            "                \"dataIndex\": \"policeAgencyName\",\n" +
//            "                \"bizId\": null,\n" +
//            "                \"industryExamineId\": null,\n" +
//            "                \"children\": null\n" +
//            "            },\n" +
//            "            {\n" +
//            "                \"title\": \"旅馆业\",\n" +
//            "                \"dataIndex\": \"1551772550130827265\",\n" +
//            "                \"bizId\": null,\n" +
//            "                \"industryExamineId\": null,\n" +
//            "                \"children\": [\n" +
//            "                    {\n" +
//            "                        \"title\": \"总数\",\n" +
//            "                        \"dataIndex\": \"1551772550130827265_total\",\n" +
//            "                        \"bizId\": null,\n" +
//            "                        \"industryExamineId\": null,\n" +
//            "                        \"children\": null\n" +
//            "                    },\n" +
//            "                    {\n" +
//            "                        \"title\": \"未列管\",\n" +
//            "                        \"dataIndex\": \"1551772550130827265_not\",\n" +
//            "                        \"bizId\": null,\n" +
//            "                        \"industryExamineId\": null,\n" +
//            "                        \"children\": null\n" +
//            "                    },\n" +
//            "                    {\n" +
//            "                        \"title\": \"已列管\",\n" +
//            "                        \"dataIndex\": \"1551772550130827265_on\",\n" +
//            "                        \"bizId\": null,\n" +
//            "                        \"industryExamineId\": null,\n" +
//            "                        \"children\": null\n" +
//            "                    }\n" +
//            "                ]\n" +
//            "            },\n" +
//            "            {\n" +
//            "                \"title\": \"测试行业\",\n" +
//            "                \"dataIndex\": \"1583357479353421826\",\n" +
//            "                \"bizId\": null,\n" +
//            "                \"industryExamineId\": null,\n" +
//            "                \"children\": [\n" +
//            "                    {\n" +
//            "                        \"title\": \"总数\",\n" +
//            "                        \"dataIndex\": \"1583357479353421826_total\",\n" +
//            "                        \"bizId\": null,\n" +
//            "                        \"industryExamineId\": null,\n" +
//            "                        \"children\": null\n" +
//            "                    },\n" +
//            "                    {\n" +
//            "                        \"title\": \"未列管\",\n" +
//            "                        \"dataIndex\": \"1583357479353421826_not\",\n" +
//            "                        \"bizId\": null,\n" +
//            "                        \"industryExamineId\": null,\n" +
//            "                        \"children\": null\n" +
//            "                    },\n" +
//            "                    {\n" +
//            "                        \"title\": \"已列管\",\n" +
//            "                        \"dataIndex\": \"1583357479353421826_on\",\n" +
//            "                        \"bizId\": null,\n" +
//            "                        \"industryExamineId\": null,\n" +
//            "                        \"children\": null\n" +
//            "                    }\n" +
//            "                ]\n" +
//            "            },\n" +
//            "            {\n" +
//            "                \"title\": \"印刷业\",\n" +
//            "                \"dataIndex\": \"1551768930178060290\",\n" +
//            "                \"bizId\": null,\n" +
//            "                \"industryExamineId\": null,\n" +
//            "                \"children\": [\n" +
//            "                    {\n" +
//            "                        \"title\": \"总数\",\n" +
//            "                        \"dataIndex\": \"1551768930178060290_total\",\n" +
//            "                        \"bizId\": null,\n" +
//            "                        \"industryExamineId\": null,\n" +
//            "                        \"children\": null\n" +
//            "                    },\n" +
//            "                    {\n" +
//            "                        \"title\": \"未列管\",\n" +
//            "                        \"dataIndex\": \"1551768930178060290_not\",\n" +
//            "                        \"bizId\": null,\n" +
//            "                        \"industryExamineId\": null,\n" +
//            "                        \"children\": null\n" +
//            "                    },\n" +
//            "                    {\n" +
//            "                        \"title\": \"已列管\",\n" +
//            "                        \"dataIndex\": \"1551768930178060290_on\",\n" +
//            "                        \"bizId\": null,\n" +
//            "                        \"industryExamineId\": null,\n" +
//            "                        \"children\": null\n" +
//            "                    }\n" +
//            "                ]\n" +
//            "            },\n" +
//            "            {\n" +
//            "                \"title\": \"典当行\",\n" +
//            "                \"dataIndex\": \"1579004660861210626\",\n" +
//            "                \"bizId\": null,\n" +
//            "                \"industryExamineId\": null,\n" +
//            "                \"children\": [\n" +
//            "                    {\n" +
//            "                        \"title\": \"总数\",\n" +
//            "                        \"dataIndex\": \"1579004660861210626_total\",\n" +
//            "                        \"bizId\": null,\n" +
//            "                        \"industryExamineId\": null,\n" +
//            "                        \"children\": null\n" +
//            "                    },\n" +
//            "                    {\n" +
//            "                        \"title\": \"未列管\",\n" +
//            "                        \"dataIndex\": \"1579004660861210626_not\",\n" +
//            "                        \"bizId\": null,\n" +
//            "                        \"industryExamineId\": null,\n" +
//            "                        \"children\": null\n" +
//            "                    },\n" +
//            "                    {\n" +
//            "                        \"title\": \"已列管\",\n" +
//            "                        \"dataIndex\": \"1579004660861210626_on\",\n" +
//            "                        \"bizId\": null,\n" +
//            "                        \"industryExamineId\": null,\n" +
//            "                        \"children\": null\n" +
//            "                    }\n" +
//            "                ]\n" +
//            "            }\n" +
//            "        ]";

    private final String lineList ="[\n" +
            "            {\n" +
            "                \"1579004660861210626_total\": 1,\n" +
            "                \"1583357479353421826_on\": 2,\n" +
            "                \"1551768930178060290_not\": 0,\n" +
            "                \"type\": 2,\n" +
            "                \"policeAgencyName\": \"西安市公安局\",\n" +
            "                \"1551772550130827265_total\": 9,\n" +
            "                \"1579004660861210626_not\": 0,\n" +
            "                \"total\": 13,\n" +
            "                \"1551772550130827265_on\": 9,\n" +
            "                \"1551768930178060290_total\": 1,\n" +
            "                \"1583357479353421826_not\": 0,\n" +
            "                \"1551768930178060290_on\": 1,\n" +
            "                \"policeAgencyId\": \"1551465548485804034\",\n" +
            "                \"1551772550130827265_not\": 0,\n" +
            "                \"1579004660861210626_on\": 1,\n" +
            "                \"1583357479353421826_total\": 2\n" +
            "            },\n" +
            "            {\n" +
            "                \"1579004660861210626_total\": 1,\n" +
            "                \"1583357479353421826_on\": 0,\n" +
            "                \"1551768930178060290_not\": 0,\n" +
            "                \"type\": 3,\n" +
            "                \"policeAgencyName\": \"浐灞生态区分局\",\n" +
            "                \"1551772550130827265_total\": 7,\n" +
            "                \"1579004660861210626_not\": 0,\n" +
            "                \"total\": 9,\n" +
            "                \"1551772550130827265_on\": 7,\n" +
            "                \"1551768930178060290_total\": 1,\n" +
            "                \"1583357479353421826_not\": 0,\n" +
            "                \"1551768930178060290_on\": 1,\n" +
            "                \"policeAgencyId\": \"1551465715674955778\",\n" +
            "                \"1551772550130827265_not\": 0,\n" +
            "                \"1579004660861210626_on\": 1,\n" +
            "                \"1583357479353421826_total\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"1579004660861210626_total\": 0,\n" +
            "                \"1583357479353421826_on\": 2,\n" +
            "                \"1551768930178060290_not\": 0,\n" +
            "                \"type\": 3,\n" +
            "                \"policeAgencyName\": \"西咸新区分局\",\n" +
            "                \"1551772550130827265_total\": 2,\n" +
            "                \"1579004660861210626_not\": 0,\n" +
            "                \"total\": 4,\n" +
            "                \"1551772550130827265_on\": 2,\n" +
            "                \"1551768930178060290_total\": 0,\n" +
            "                \"1583357479353421826_not\": 0,\n" +
            "                \"1551768930178060290_on\": 0,\n" +
            "                \"policeAgencyId\": \"1583014970580373505\",\n" +
            "                \"1551772550130827265_not\": 0,\n" +
            "                \"1579004660861210626_on\": 0,\n" +
            "                \"1583357479353421826_total\": 2\n" +
            "            }\n" +
            "        ]";

}
