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

    /**
     * 中间处理：列转行（Unpivot）
     * 将多列数据转换为多行记录
     * 示例命令：unpivot(department, sales_2020, sales_2021)
     * @param cmd 包含主字段和需要转行的字段列表
     * @param dataRows 原始数据行列表，方法内部会构造新结构
     */
    public static void unpivotRows(String cmd, List<HashMap<String, String>> dataRows) {
        String args = extractBracketContent(cmd);
        if (args == null || args.isEmpty()) return;

        String[] parts = args.split("\\s*,\\s*");
        if (parts.length < 2) return;
        String groupField = parts[0];

        // 提取需要转行的字段
        List<String> valueFields = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            valueFields.add(parts[i]);
        }

        List<HashMap<String, String>> newRows = new ArrayList<>();

        for (HashMap<String, String> row : dataRows) {
            String groupValue = row.get(groupField);
            if (groupValue == null) continue;

            // 为每个值字段创建一个新行
            for (String valueField : valueFields) {
                String value = row.get(valueField);
                if (value != null) {
                    HashMap<String, String> newRow = new HashMap<>();
                    newRow.put(groupField, groupValue);
                    newRow.put("field", valueField);  // 列名转为field列
                    newRow.put("value", value);      // 值转为value列
                    newRows.add(newRow);
                }
            }
        }

        dataRows.clear();
        dataRows.addAll(newRows);
    }

    /**
     * 中间处理：数据合并（Merge Rows）
     * 示例命令：merge(department, sum(sales), avg(employees))
     * @param cmd 包含分组字段和聚合表达式的命令
     * @param dataRows 原始数据行列表，方法内部会构造合并后的数据
     */
    public static void mergeRows(String cmd, List<HashMap<String, String>> dataRows) {
        String args = extractBracketContent(cmd);
        if (args == null || args.isEmpty()) return;

        String[] parts = args.split("\\s*,\\s*");
        if (parts.length < 2) return;
        String groupField = parts[0];

        // 解析聚合表达式
        List<Aggregation> aggregations = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            String expr = parts[i].trim();
            Matcher matcher = AGGREGATION_PATTERN.matcher(expr);
            if (matcher.matches()) {
                String function = matcher.group(1).toLowerCase();
                String field = matcher.group(2);
                aggregations.add(new Aggregation(function, field));
            }
        }

        if (aggregations.isEmpty()) return;

        // 使用Map进行分组聚合
        Map<String, GroupData> groupMap = new LinkedHashMap<>();

        for (HashMap<String, String> row : dataRows) {
            String groupValue = row.get(groupField);
            if (groupValue == null) continue;

            GroupData groupData = groupMap.computeIfAbsent(groupValue, k -> new GroupData());

            // 处理每个聚合字段
            for (Aggregation agg : aggregations) {
                String fieldValue = row.get(agg.getField());
                if (fieldValue != null) {
                    try {
                        double value = Double.parseDouble(fieldValue);
                        groupData.accumulate(agg, value);
                    } catch (NumberFormatException e) {
                        // 非数字值处理
                        if ("count".equals(agg.getFunction())) {
                            groupData.accumulate(agg, 1);
                        }
                    }
                }
            }
        }

        // 构建合并后的结果
        dataRows.clear();
        for (Map.Entry<String, GroupData> entry : groupMap.entrySet()) {
            HashMap<String, String> mergedRow = new HashMap<>();
            mergedRow.put(groupField, entry.getKey());

            // 设置聚合结果
            for (Aggregation agg : aggregations) {
                String result = entry.getValue().getResult(agg);
                mergedRow.put(agg.getField() + "_" + agg.getFunction(), result);
            }

            dataRows.add(mergedRow);
        }
    }

    // 聚合表达式的正则模式
    private static final Pattern AGGREGATION_PATTERN = Pattern.compile("(sum|avg|count|min|max)\\(([^)]+)\\)");

    // 辅助类：表示一个聚合操作
    private static class Aggregation {
        private final String function;
        private final String field;

        public Aggregation(String function, String field) {
            this.function = function;
            this.field = field;
        }

        public String getFunction() {
            return function;
        }

        public String getField() {
            return field;
        }
    }

    // 辅助类：表示一组数据的聚合结果
    private static class GroupData {
        private final Map<Aggregation, AggregateResult> results = new HashMap<>();

        public void accumulate(Aggregation agg, double value) {
            AggregateResult result = results.computeIfAbsent(agg, k -> new AggregateResult());
            result.accumulate(value, agg.getFunction());
        }

        public String getResult(Aggregation agg) {
            AggregateResult result = results.get(agg);
            return result != null ? result.getResult(agg.getFunction()) : "0";
        }
    }

    // 辅助类：计算聚合结果
    private static class AggregateResult {
        private int count = 0;
        private double sum = 0;
        private double min = Double.MAX_VALUE;
        private double max = Double.MIN_VALUE;

        public void accumulate(double value, String function) {
            count++;
            sum += value;
            if (value < min) min = value;
            if (value > max) max = value;
        }

        public String getResult(String function) {
            switch (function) {
                case "sum": return String.valueOf(sum);
                case "avg": return count > 0 ? String.valueOf(sum / count) : "0";
                case "count": return String.valueOf(count);
                case "min": return min != Double.MAX_VALUE ? String.valueOf(min) : "0";
                case "max": return max != Double.MIN_VALUE ? String.valueOf(max) : "0";
                default: return "0";
            }
        }
    }
}