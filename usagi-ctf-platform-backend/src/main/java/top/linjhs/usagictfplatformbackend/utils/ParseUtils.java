package top.linjhs.usagictfplatformbackend.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用于数据类型转换
 *
 * @author qst137
 * @version 1.0
 */
@Slf4j
@Component
public class ParseUtils {
    /**
     * 将逗号分隔字符串转为列表
     *
     * @param str 待处理字符串
     * @return java.util.List
     * @author qst137
     */
    public List<String> stringToList(String str){
        List<String> list=new ArrayList<>();
        if (!StringUtils.hasLength(str)){
            return list;
        }
        String[] strings=str.split(",");
        Collections.addAll(list,strings);
        return list;
    }
    /**
     * 将列表转为逗号分隔字符串
     *
     * @param list 待处理字符串列表
     * @return java.lang.String
     * @author qst137
     */
    public String listToString(List<String> list){
        if(list.isEmpty()){
            return "";
        }
        StringBuilder code = new StringBuilder();
        for(String s:list){
            code.append(s).append(',');
        }
        code.deleteCharAt(code.length()-1);
        return code.toString();
    }
}
