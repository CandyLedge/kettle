package RunTask.pojo.OPS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Middle {
    // 这里封装了中间流程的操作 例如：过滤，数据处理···
    
    // 这个方法返回一个键值对，key代表这一组数据应该去哪一步，value代表一组数据
    
    /// 步骤对应一组数据
    public static HashMap<String, List<HashMap<String, String>>> where_data_by_column(String cmd, HashMap<String, Step> indexToStepMap, List<HashMap<String, String>> datas) {
        /// -----------------------------------------------------------
        HashMap<String, List<HashMap<String, String>>> stepToData = new HashMap<>();
        String[] lings = cmd.split("\n");
        // field代表根据哪个列来分组数据
        String field = extractContentInBrackets(lings[0]);
        // 列值对应步骤
        HashMap<String, String> columnToStep = new HashMap<>();
        // 解析cmd字符串，获取switch case情况
        // 下面这个循环是在填充switch case hashmap
        for (int i = 1; i < lings.length; i++) {
            String line = lings[i];
            String key;
            String value;
            key = line.split(":")[0].split(" ")[1];// 列值
            value = extractContentInBrackets(line.split(":")[1]);// 步骤
            columnToStep.put(key, value);
        }
        // 开始分组
        for (String step : columnToStep.values()) {
            stepToData.put(step, new ArrayList<>());
        }
        for (HashMap<String, String> row : datas) {
            /// 根据列名获取列值，根据列值获取步骤，根据步骤创建组
            String step = columnToStep.get(row.get(field));
            stepToData.get(step).add(row);
        }
        return stepToData;
    }
    
    public static void sort (String cmd, HashMap<String, Step> indexToStepMap, List<HashMap<String, String>> result) {
    
    }
    // 传入一个字符串，返回括号里的内容
    private static String extractContentInBrackets(String input) {
        // 使用正则表达式匹配括号内的内容
        String regex = "\\((.*?)\\)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(input);
        
        if (matcher.find()) {
            return matcher.group(1); // 返回第一个匹配组的内容
        }
        return null; // 如果没有找到匹配的内容，返回 null
    }


    public static void selectFields(String cmd, HashMap<String, Step> indexToStepMap,
                                    List<HashMap<String, String>> result) {
        String[] fieldArray = cmd.split(",");
        List<String> selectedFields = new ArrayList<>();
        for (String field : fieldArray) {
            selectedFields.add(field.trim());
        }
        List<HashMap<String, String>> filteredResult = new ArrayList<>();
        for (HashMap<String, String> row : result) {
            HashMap<String, String> newRow = new HashMap<>();
            for (String field : selectedFields) {
                if (row.containsKey(field)) {
                    newRow.put(field, row.get(field));
                }
            }
            filteredResult.add(newRow);
        }
        result.clear();
        result.addAll(filteredResult);
    }


    public static void deleteFields(String cmd, HashMap<String, Step> indexToStepMap, List<HashMap<String, String>> result) {
        //cmd是指定删除的字段，result是指定删除的字段所在的数据
        String[] fieldArray = cmd.split(",");
        List<String> fieldsToDelete = new ArrayList<>();
        for (String field : fieldArray) {
            fieldsToDelete.add(field.trim());
        }
        for (HashMap<String, String> row : result) {
            for (String field : fieldsToDelete) {
                row.remove(field);
            }
        }
    }





}
