package org.example.kettle;

import RunTask.pojo.OPS.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wtest {

    public static void main(String[] args) {
        // 创建一个包含一些数据的 List<HashMap<String, String>>
        List<HashMap<String, String>> result = new ArrayList<>();

        // 添加一些测试数据
        HashMap<String, String> row1 = new HashMap<>();
        row1.put("oldField1", "value1");
        row1.put("oldField2", "value2");
        result.add(row1);

        HashMap<String, String> row2 = new HashMap<>();
        row2.put("oldField1", "value3");
        row2.put("oldField2", "value4");
        result.add(row2);

        // 打印原始数据
        System.out.println("原始数据:");
        for (HashMap<String, String> row : result) {
            System.out.println(row);
        }

        // 重命名字段
        String cmd = "oldField1,newField1,oldField2,newField2";
        HashMap<String, Step> indexToStepMap = new HashMap<>(); // 这个参数在当前方法中没有使用
        renameFields(cmd, indexToStepMap, result);

        // 打印重命名后的数据
        System.out.println("重命名后的数据:");
        for (HashMap<String, String> row : result) {
            System.out.println(row);
        }
    }

    public static void renameFields(String cmd, HashMap<String, Step> indexToStepMap,
                                    List<HashMap<String, String>> result) {
        if (cmd == null || result == null) {
            return;
        }
        String[] renamePairs = cmd.split(",");
        Map<String, String> renameMap = new HashMap<>();
        for (int i = 0; i < renamePairs.length; i += 2) {
            if (i + 1 < renamePairs.length) {
                String oldField = renamePairs[i].trim();
                String newField = renamePairs[i + 1].trim();
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
}
