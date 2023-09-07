package cn.com.tzy.spingbootstartermybatis.handler;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Range;
import lombok.extern.log4j.Log4j2;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


@Log4j2
public class ShardingDatabaseAlgorithm implements PreciseShardingAlgorithm<Date>, RangeShardingAlgorithm<Date> {

    //精准查询
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Date> preciseShardingValue) {
        String tableName=preciseShardingValue.getLogicTableName();
        try {
            String format = DateUtil.format(preciseShardingValue.getValue()==null?new Date():preciseShardingValue.getValue(), Constant.MONTH_FORMAT);
            tableName=String.format("%s_%s",tableName,format);
            log.info("db_name:{}",tableName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (String each : collection) {
            log.info("db:" + each);
            if (each.equals(tableName)) {
                return each;
            }
        }
        throw new IllegalArgumentException();
    }

    //范围查询
    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Date> rangeShardingValue) {
        Range<Date> valueRange = rangeShardingValue.getValueRange();//获得输入的查询条件范围
        Date date = valueRange.hasLowerBound() ? valueRange.lowerEndpoint() : new Date();//查询条件下限
        Date date1 = valueRange.hasUpperBound() ? valueRange.upperEndpoint() : new Date();//查询条件上限
        List<DateTime> dateTimeList = DateUtil.rangeToList(date, date1, DateField.MONTH);
        Collection<String> collect = new ArrayList<>();
        for (DateTime dateTime : dateTimeList) {
            String format = DateUtil.format(dateTime, Constant.MONTH_FORMAT);
            for (String tableName : collection) {
                if(tableName.endsWith(String.format("_%s",format))){
                    if(!collect.contains(tableName)){
                        collect.add(tableName);
                    }
                }
            }
        }
        return collect;
    }
}
