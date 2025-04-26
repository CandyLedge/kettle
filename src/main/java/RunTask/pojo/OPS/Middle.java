package RunTask.pojo.OPS;

import java.util.HashMap;
import java.util.List;

public class Middle {
    // 这里封装了中间流程的操作 例如：过滤，数据处理···
    public static void middle_whereDataByColumn(String cmd, List<Step> outputStep, List<HashMap<String, String>> result) {
        String[] lings = cmd.split("\n");
        String field = extractContentInBrackets(lings[0]);
        HashMap<String, String> switchCase = new HashMap<>();
        // 解析cmd字符串，获取switch case情况
        for (int i = 1; i < lings.length; i++) {
            String line = lings[i];
            String key;
            String value;
            key = line.split(":")[0].split(" ")[1];
            value = extractContentInBrackets(line.split(":")[1]);
            switchCase.put(key, value);
        }
        // 建立一个 步骤下标 -> 步骤对象的HashMap
        HashMap<String, Step> indexToStepMap = new HashMap<>();
        for (Step step : outputStep) {
            String stepIndex = step.getStep()
                                   .toString();
            indexToStepMap.put(stepIndex, step);
        }
        // 进行数据分类
        for (HashMap<String, String> row : result) {
            String value = row.get(field);
            String goToStep = switchCase.get(value);
            Step step = indexToStepMap.get(goToStep);
            Output.output_database(step.getPrivateField(), row);
        }
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
}
