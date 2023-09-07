package cn.com.tzy.springbootentity.utils;

import org.apache.commons.lang3.StringUtils;

public class HtmlUtils {

    public static String  createHtml(String content){
        String head = "<head><meta charset='utf-8'><meta charset='utf-8'name='viewport' content='width=device-width, initial-scale=1.0'><style>img{ width:100%; }</style><title></title></head>";
        StringBuilder contents = new StringBuilder();
        contents.append("<!doctype html>" + head + "<html><body>");
        contents.append(content);
        contents.append("</body></html>");
        return contents.toString();
    }


    public static String getBody(String content){
        if(StringUtils.isEmpty(content)){
            return "";
        }
        String[] split = content.split("<body>");
        content = split[split.length - 1];
        split = content.split("</body");
        content =split[0];
        return content;
    }
}
