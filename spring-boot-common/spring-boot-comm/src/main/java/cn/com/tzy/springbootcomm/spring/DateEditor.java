package cn.com.tzy.springbootcomm.spring;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;

public class DateEditor extends PropertyEditorSupport {
	public void setAsText(String text) {
		if(StringUtils.isNotEmpty(text)) {

			try {
				setValue(DateUtils.parseDate(text, new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"}));
			} catch (ParseException e) {
				throw new IllegalArgumentException("日期格式错误(" + text + ")");
			}
		} else {
			setValue(null);
		}


    }
}
