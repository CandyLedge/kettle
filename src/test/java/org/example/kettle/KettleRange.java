package org.example.kettle;

import RunTask.pojo.OPS.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KettleRange {

    /**
     * 对列表中每个 HashMap 里的指定字段进行数值分段
     * @param cmd 包含字段名、分段规则和结果字段名的命令字符串，格式为 "fieldName,min1-max1:label1,min2-max2:label2,resultField"
     * @param indexToStepMap 存储步骤索引和 Step 对象映射的 HashMap
     * @param result 包含多个 HashMap 的列表，每个 HashMap 代表一行数据
     */
    public static void range(String cmd, HashMap<String, Step> indexToStepMap,
                             List<HashMap<String, String>> result) {
        if (cmd == null || result == null) {
            return;
        }

        // 解析命令字符串
        String[] parts = cmd.split(",");
        String fieldName = parts[0];
        String resultField = parts[parts.length - 1];

        // 构建分段规则映射
        Map<String, String> rangeMap = new HashMap<>();
        for (int i = 1; i < parts.length - 1; i++) {
            String[] rangeLabel = parts[i].split(":");
            String range = rangeLabel[0];
            String label = rangeLabel[1];
            rangeMap.put(range, label);
        }

        // 遍历每一行数据，进行数值分段
        for (HashMap<String, String> row : result) {
            if (row.containsKey(fieldName)) {
                String valueStr = row.get(fieldName);
                try {
                    double value = Double.parseDouble(valueStr);
                    String label = getRangeLabel(value, rangeMap);
                    row.put(resultField, label);
                } catch (NumberFormatException e) {
                    // 处理非数值的情况
                    row.put(resultField, "Invalid");
                }
            } else {
                row.put(resultField, "Missing");
            }
        }
    }

    /**
     * 根据数值和分段规则映射获取对应的标签
     * @param value 要进行分段的数值
     * @param rangeMap 分段规则映射
     * @return 对应的标签
     */
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

    public static void main(String[] args) {
        // 创建一个包含一些数据的 List<HashMap<String, String>>
        List<HashMap<String, String>> result = new ArrayList<>();

        // 添加一些测试数据
        HashMap<String, String> row1 = new HashMap<>();
        row1.put("age", "25");
        result.add(row1);

        HashMap<String, String> row2 = new HashMap<>();
        row2.put("age", "35");
        result.add(row2);

        HashMap<String, String> row3 = new HashMap<>();
        row3.put("age", "45");
        result.add(row3);

        // 打印原始数据
        System.out.println("原始数据:");
        for (HashMap<String, String> row : result) {
            System.out.println(row);
        }

        // 进行数值分段
        String cmd = "age,0-30:Young,30-40:Middle-aged,40-50:Senior,age_range";
        HashMap<String, Step> indexToStepMap = new HashMap<>();
        range(cmd, indexToStepMap, result);

        // 打印分段后的数据
        System.out.println("分段后的数据:");
        for (HashMap<String, String> row : result) {
            System.out.println(row);
        }
    }
}