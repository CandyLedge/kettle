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
     * 中间处理：排序数据（Sort Rows）
     * 示例命令：sort(score, desc)
     * @param cmd 包含字段名与可选排序方向的命令
     * @param dataRows 待排序的数据行列表
     */
    public static void sortRows(String cmd, List<HashMap<String, String>> dataRows) {
        String args = extractBracketContent(cmd);
        if (args == null || args.isEmpty()) return;

        String[] parts = args.split("\\s*,\\s*");
        String field = parts[0];
        boolean descending = parts.length > 1 && "desc".equalsIgnoreCase(parts[1]);

        dataRows.sort((row1, row2) -> {
            String val1 = row1.get(field);
            String val2 = row2.get(field);
            if (val1 == null) return 1;
            if (val2 == null) return -1;

            try {
                double num1 = Double.parseDouble(val1);
                double num2 = Double.parseDouble(val2);
                return descending ? Double.compare(num2, num1) : Double.compare(num1, num2);
            } catch (NumberFormatException e) {
                return descending ? val2.compareTo(val1) : val1.compareTo(val2);
            }
        });
    }

    /**
     * 中间处理：行转列（Pivot）
     * 示例命令：pivot(department, name)
     * @param cmd 包含主字段和目标字段的命令
     * @param dataRows 原始数据行列表，方法内部会构造新结构
     */
    public static void pivotRows(String cmd, List<HashMap<String, String>> dataRows) {
        String args = extractBracketContent(cmd);
        if (args == null || args.isEmpty()) return;

        String[] parts = args.split("\\s*,\\s*");
        if (parts.length < 2) return;
        String groupField = parts[0];
        String valueField = parts[1];

        // 用 Map 聚合
        Map<String, List<String>> pivotMap = new LinkedHashMap<>();
        for (HashMap<String, String> row : dataRows) {
            String key = row.get(groupField);
            String val = row.get(valueField);
            if (key == null || val == null) continue;
            pivotMap.computeIfAbsent(key, k -> new ArrayList<>()).add(val);
        }

        // 重建行
        dataRows.clear();
        for (Map.Entry<String, List<String>> entry : pivotMap.entrySet()) {
            HashMap<String, String> newRow = new HashMap<>();
            newRow.put(groupField, entry.getKey());
            int i = 1;
            for (String val : entry.getValue()) {
                newRow.put(valueField + "_" + i++, val);
            }
            dataRows.add(newRow);
        }
    }

    /**
     * 中间处理：字段计算（Field Calculation）
     * 示例命令：calc(total = score1 + score2)
     * @param cmd 包含目标字段与表达式
     * @param dataRows 原始数据行，将添加计算结果
     */
    public static void calculateField(String cmd, List<HashMap<String, String>> dataRows) {
        String expression = extractBracketContent(cmd);
        if (expression == null || !expression.contains("=")) return;

        String[] parts = expression.split("=");
        String targetField = parts[0].trim();
        String formula = parts[1].trim();

        Pattern p = Pattern.compile("([a-zA-Z0-9_]+|\\d+(\\.\\d+)?)(\\s*[+\\-*/]\\s*)([a-zA-Z0-9_]+|\\d+(\\.\\d+)?)");
        Matcher m = p.matcher(formula);
        if (!m.matches()) return;

        String left = m.group(1).trim();
        String op = m.group(3).trim();
        String right = m.group(4).trim();

        for (HashMap<String, String> row : dataRows) {
            try {
                double l = isNumber(left) ? Double.parseDouble(left) : Double.parseDouble(row.getOrDefault(left, "0"));
                double r = isNumber(right) ? Double.parseDouble(right) : Double.parseDouble(row.getOrDefault(right, "0"));
                double result;
                switch (op) {
                    case "+": result = l + r; break;
                    case "-": result = l - r; break;
                    case "*": result = l * r; break;
                    case "/": result = r != 0 ? l / r : 0; break;
                    default: continue;
                }
                row.put(targetField, String.valueOf(result));
            } catch (Exception e) {
                row.put(targetField, "0");
            }
        }
    }

    private static boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }



}
