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
        return null; // 如果没有找到匹配的内容返回 null
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
 /**
 * 删除 result 中每条记录指定的字段
 *
 * @param cmd 命令字符串，逗号分隔多个字段名
 * @param indexToStepMap 映射索引到 Step 的 Map（当前方法未使用）
 * @param result 数据集合，每个元素是一个字段-值映射
 */
public static void deleteFields(String cmd, HashMap<String, Step> indexToStepMap, List<HashMap<String, String>> result) {
    if (cmd == null || result == null) {
        throw new IllegalArgumentException("参数 cmd 或 result 不能为 null");
    }
    String[] fieldArray = cmd.split(",");
    List<String> fieldsToDelete = new ArrayList<>();
    for (String field : fieldArray) {
        fieldsToDelete.add(field.trim());
    }
    for (HashMap<String, String> row : result) {
        for (String field : fieldsToDelete) {
            if (!row.containsKey(field)) {
                 System.out.println("字段不存在: " + field);
            }
            row.remove(field);
        }
    }
}

    public static void renameFields(String cmd, HashMap<String, Step> indexToStepMap,
                                    List<HashMap<String, String>> result) {//重命名
        if (cmd == null || result == null) {
            return;
        }
        String[] renamePairs = cmd.split(",");
        Map<String, String> renameMap = new HashMap<>();
        for (String pair : renamePairs) {
            String[] fields = pair.split("=");
            if (fields.length == 2) {
                String oldField = fields[0].trim();
                String newField = fields[1].trim();
                renameMap.put(oldField, newField);
            }
        }
        for (HashMap<String, String> row : result) {
            for (Map.Entry<String, String> entry : renameMap.entrySet()) {
                String oldField = entry.getKey();
                String newField = entry.getValue();
                if (row.containsKey(oldField)) {
                    String value = row.get(oldField);
                    row.remove(oldField);
                    row.put(newField, value);
                }
            }
        }
    }

    public static void range(String cmd, HashMap<String, Step> indexToStepMap,
                             List<HashMap<String, String>> result) {//数值分段
        if (cmd == null || result == null) {
            return;
        }
        String[] parts = cmd.split(",");
        String fieldName = parts[0];
        String resultField = parts[parts.length - 1];
        Map<String, String> rangeMap = new HashMap<>();
        for (int i = 1; i < parts.length - 1; i++) {
            String[] rangeLabel = parts[i].split(":");
            String range = rangeLabel[0];
            String label = rangeLabel[1];
            rangeMap.put(range, label);
        }
        for (HashMap<String, String> row : result) {
            if (row.containsKey(fieldName)) {
                String valueStr = row.get(fieldName);
                try {
                    double value = Double.parseDouble(valueStr);
                    String label = getRangeLabel(value, rangeMap);
                    row.put(resultField, label);
                } catch (NumberFormatException e) {
                    row.put(resultField, "Invalid");
                }
            } else {
                row.put(resultField, "Missing");
            }
        }
    }
    private static String getRangeLabel(double value, Map<String, String> rangeMap) {
        for (Map.Entry<String, String> entry : rangeMap.entrySet()) {
            String range = entry.getKey();
            String[] minMax = range.split("-");
            double min = Double.parseDouble(minMax[0]);
            double max = Double.parseDouble(minMax[1]);
            if (value >= min && value < max) {
                return entry.getValue();
            }
        }
        return "Out of range";
    }
}
