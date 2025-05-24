package RunTask.pojo.OPS;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Middle {

    /**
     * 中间处理：根据列值路由到不同步骤
     * 示例命令：where(field)\n case KEY1:(stepIndex1)\n case KEY2:(stepIndex2)
     * @param cmd 操作命令字符串，第一行包含字段名，后续行定义值到步骤索引映射
     * @param steps 可执行的步骤列表，每个步骤包含索引和类型
     * @param dataRows 数据行列表，将对每行根据字段值执行对应步骤操作
     */
    public static void middle_whereDataByColumn(String cmd, List<Step> steps, List<HashMap<String, String>> dataRows) {
        String[] lines = cmd.split("\\n");
        String column = extractBracketContent(lines[0]);
        Map<String, String> caseMap = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            String[] parts = lines[i].split(":");
            String key = parts[0].split(" ")[1];
            String target = extractBracketContent(parts[1]);
            caseMap.put(key, target);
        }

        Map<String, Step> stepIndexMap = new HashMap<>();
        for (Step step : steps) {
            stepIndexMap.put(step.getStep().toString(), step);
        }

        for (HashMap<String, String> row : dataRows) {
            String cellValue = row.get(column);
            Step step = stepIndexMap.get(caseMap.get(cellValue));
            if (step == null) continue;
            switch (step.getType()) {
                case "output-database":
                    Output.output_database(step.getPrivateField(), row);
                    break;
                default:
                    throw new RuntimeException("未知输出类型: " + step.getType());
            }
        }
    }

    /**
     * 中间处理：数据过滤（Filter Rows）
     * 支持条件：>, <, =, >=, <=
     * 示例命令：filter(age>=18)
     * @param cmd 包含字段名与条件表达式的命令
     * @param dataRows 待过滤的数据行列表
     */
    public static void filterRows(String cmd, List<HashMap<String, String>> dataRows) {
        String condition = extractBracketContent(cmd);
        if (condition == null || condition.isEmpty()) return;

        Pattern conditionPattern = Pattern.compile("(\\w+)\\s*(>=|<=|=|<|>)\\s*(\\S+)");
        Matcher matcher = conditionPattern.matcher(condition);
        if (!matcher.find()) return;

        String field = matcher.group(1);
        String operator = matcher.group(2);
        String valueLiteral = matcher.group(3);

        dataRows.removeIf(row -> {
            String cell = row.get(field);
            if (cell == null) return true;
            try {
                double numCell = Double.parseDouble(cell);
                double numValue = Double.parseDouble(valueLiteral);
                switch (operator) {
                    case ">":  return numCell <= numValue;
                    case "<":  return numCell >= numValue;
                    case "=":  return numCell != numValue;
                    case ">=": return numCell < numValue;
                    case "<=": return numCell > numValue;
                    default:    return true;
                }
            } catch (NumberFormatException e) {
                return "=".equals(operator) ? !cell.equals(valueLiteral) : true;
            }
        });
    }

    /**
     * 中间处理：字段拆行（Split Field to Rows）
     * 支持格式：split(field) 或 split(field, ',') 或 split(field, "|")
     * 示例命令：split(tags ,)
     * @param cmd 包含字段名和可选分隔符的命令
     * @param dataRows 待拆分的数据行列表，将在方法内重写为多行结构
     */
    public static void splitFieldToRows(String cmd, List<HashMap<String, String>> dataRows) {
        String args = extractBracketContent(cmd);
        if (args == null || args.isEmpty()) return;

        Pattern splitPattern = Pattern.compile("^([a-zA-Z0-9_]+)(?:\\s*,\\s*['\"]?(.*?)['\"]?)?$");
        Matcher matcher = splitPattern.matcher(args);
        if (!matcher.matches()) return;

        String field = matcher.group(1);
        String delimiter = matcher.group(2) != null ? matcher.group(2) : ",";

        List<HashMap<String, String>> expanded = new ArrayList<>();
        for (HashMap<String, String> row : dataRows) {
            String cell = row.get(field);
            if (cell == null) continue;
            if (delimiter.isEmpty()) {
                expanded.add(new HashMap<>(row));
                continue;
            }
            for (String part : cell.split(Pattern.quote(delimiter))) {
                HashMap<String, String> newRow = new HashMap<>(row);
                newRow.put(field, part.trim());
                expanded.add(newRow);
            }
        }

        dataRows.clear();
        dataRows.addAll(expanded);
    }


    /**
     * 中间处理：数值分段（Number Range）
     * 支持命令格式：range(field) [as targetField] 0-59:low,60-89:mid,90-100:high
     * 示例命令：range(score) 0-59:low,60-89:mid,90-100:high
     * @param cmd 包含字段名、可选目标字段及映射定义的命令
     * @param dataRows 待处理的数据行列表，将新增分段结果字段
     */
    public static void mapNumberRange(String cmd, List<HashMap<String, String>> dataRows) {
        String field = extractBracketContent(cmd);
        if (field == null || field.isEmpty()) return;

        String rest = cmd.substring(cmd.indexOf(')') + 1).trim();
        String targetField = field + "_range";
        String definitions = rest;
        if (rest.startsWith("as ")) {
            int idx = rest.indexOf(' ', 3);
            if (idx == -1) idx = rest.length();
            targetField = rest.substring(3, idx).trim();
            definitions = rest.substring(idx).trim();
        }

        Map<Range, String> ranges = new LinkedHashMap<>();
        for (String part : definitions.split(",")) {
            String[] kv = part.trim().split(":");
            if (kv.length != 2) continue;
            String[] bounds = kv[0].split("-");
            if (bounds.length != 2) continue;
            try {
                double low = Double.parseDouble(bounds[0].trim());
                double high = Double.parseDouble(bounds[1].trim());
                ranges.put(new Range(low, high), kv[1].trim());
            } catch (NumberFormatException ignored) {}
        }

        for (HashMap<String, String> row : dataRows) {
            String cell = row.get(field);
            if (cell == null) continue;
            try {
                double num = Double.parseDouble(cell);
                for (Map.Entry<Range, String> e : ranges.entrySet()) {
                    if (e.getKey().contains(num)) {
                        row.put(targetField, e.getValue());
                        break;
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    /**
     * 辅助方法：提取括号内容，供多方法使用
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
     * @param pattern 正则模式字符串
     * @return 括号平衡返回 true
     */
    private static boolean areParenthesesBalanced(String pattern) {
        int balance = 0;
        boolean inClass = false;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '\\') { i++; continue; }
            if (c == '[' && !inClass) { inClass = true; }
            else if (c == ']' && inClass) { inClass = false; }
            else if (!inClass) {
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
        Range(double low, double high) { this.low = low; this.high = high; }
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