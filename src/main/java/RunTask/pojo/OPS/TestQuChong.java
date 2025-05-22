package RunTask.pojo.OPS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestQuChong {
    public static void main(String[] args) {
        List<HashMap<String, String>> dataList = new ArrayList<>();

        HashMap<String, String> row1 = new HashMap<>();
        row1.put("name", "abc");
        row1.put("age", "20");

        HashMap<String, String> row2 = new HashMap<>();
        row2.put("name", "def");
        row2.put("age", "25");

        HashMap<String, String> row3 = new HashMap<>();
        row3.put("name", "abc");  // 重复
        row3.put("age", "20");

        dataList.add(row1);
        dataList.add(row2);
        dataList.add(row3);

        //System.out.println("原始数据: " + dataList);

        // 调用静态去重方法
        QuChong.quChong("", dataList);  // cmd参数可传空字符串

        System.out.println("去重后数据: " + dataList);
    }
}
