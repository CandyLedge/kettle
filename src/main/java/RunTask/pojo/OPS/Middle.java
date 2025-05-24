package RunTask.pojo.OPS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    
    public static void fieldMerge(String cmd, HashMap<String, Step> indexToStepMap, List<HashMap<String, String>> results) {
        String[] split = cmd.split(" ");
        if (split.length != 4) {
            throw new RuntimeException("字段合并的步骤出错！原因：privateField 字符串 传入错误！");
        }
        String field_1 = split[0];
        String field_2 = split[1];
        String mergedField = split[2];
        for (HashMap<String, String> data : results) {
            String v_1 = data.get(field_1);
            String v_2 = data.get(field_2);
            data.remove(field_1);
            data.remove(field_2);
            data.put(mergedField, v_1 + v_2);
        }
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
    
    /**
     * 辅助方法：提取括号内容，供多方法使用
     *
     * @param text 包含括号的字符串
     * @return 括号内的内容或 null
     */
    private static String extractBracketContent(String text) {
        int start = text.indexOf('(');
        if (start < 0) return null;
        int balance = 1;
        for (int i = start + 1; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') balance++;
            else if (c == ')') {
                balance--;
                if (balance == 0) return text.substring(start + 1, i);
            }
        }
        return null;
    }
    
    /**
     * 辅助方法：检测正则模式中的括号是否平衡
     *
     * @param pattern 正则模式字符串
     * @return 括号平衡返回 true
     */
    private static boolean areParenthesesBalanced(String pattern) {
        int balance = 0;
        boolean inClass = false;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '\\') {
                i++;
                continue;
            }
            if (c == '[' && !inClass) { inClass = true; } else if (c == ']' && inClass) {
                inClass = false;
            } else if (!inClass) {
                if (c == '(') balance++;
                else if (c == ')') {
                    balance--;
                    if (balance < 0) return false;
                }
            }
        }
        return true;
    }
    
    /**
     * 辅助类：数值范围映射的表示类
     */
    private static class Range {
        private final double low;
        private final double high;
        
        Range(double low, double high) {
            this.low = low;
            this.high = high;
        }
        
        boolean contains(double v) { return v >= low && v <= high; }
    }
}