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
    
    public static void sort(String cmd, HashMap<String, Step> indexToStepMap, List<HashMap<String, String>> result) {
    
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

    // 选择字段，从m个字段中选择n个字段
    public static List<HashMap<String, String>>
    selectFields(List<HashMap<String, String>> datas, List<String> selectedFields) {
        List<HashMap<String, String>> result = new ArrayList<>();
        for (HashMap<String, String> row : datas) {
            HashMap<String, String> newRow = new HashMap<>();
            for (String field : selectedFields) {
                if (row.containsKey(field)) {
                    newRow.put(field, row.get(field));
                }
            }
            result.add(newRow);
        }
        return result;
    }

    // 删除字段，从m个字段中删除n个字段

    //该方法会遍历输入的每条数据记录，删除其中指定的字段，最后返回处理后的结果列表。
    public static List<HashMap<String, String>>
    deleteFields(List<HashMap<String, String>> datas, List<String> fieldsToDelete) {
        List<HashMap<String, String>> result = new ArrayList<>();
        for (HashMap<String, String> row : datas) {
            HashMap<String, String> newRow = new HashMap<>(row);
            for (String field : fieldsToDelete) {
                newRow.remove(field);
            }
            result.add(newRow);
        }
        return result;
    }

    // 重命名字段
    public static List<HashMap<String, String>>
    renameField(List<HashMap<String, String>> datas, Map<String, String> fieldRenames) {
        List<HashMap<String, String>> result = new ArrayList<>();
        for (HashMap<String, String> row : datas) {
            HashMap<String, String> newRow = new HashMap<>();
            for (Map.Entry<String, String> entry : row.entrySet()) {
                String oldField = entry.getKey();
                String value = entry.getValue();
                if (fieldRenames.containsKey(oldField)) {
                    String newField = fieldRenames.get(oldField);
                    newRow.put(newField, value);
                } else {
                    newRow.put(oldField, value);
                }
            }
            result.add(newRow);
        }
        return result;
    }

    public static List<HashMap<String, Object>>
    modifyFieldType(List<HashMap<String, String>> datas, Map<String, Class<?>> fieldTypeChanges) {
        List<HashMap<String, Object>> result = new ArrayList<>();
        for (HashMap<String, String> row : datas) {
            HashMap<String, Object> newRow = new HashMap<>();
            for (Map.Entry<String, String> entry : row.entrySet()) {
                String field = entry.getKey();
                String value = entry.getValue();
                if (fieldTypeChanges.containsKey(field)) {
                    Class<?> newType = fieldTypeChanges.get(field);
                    try {
                        if (newType == Integer.class) {
                            newRow.put(field, Integer.parseInt(value));
                        } else if (newType == Double.class) {
                            newRow.put(field, Double.parseDouble(value));
                        } else if (newType == Boolean.class) {
                            newRow.put(field, Boolean.parseBoolean(value));
                        } else {
                            newRow.put(field, value);
                        }
                    } catch (NumberFormatException e) {
                        newRow.put(field, value);
                    }
                } else {
                    newRow.put(field, value);
                }
            }
            result.add(newRow);
        }
        return result;
    }

}
