package org.example.kettle;

import RunTask.pojo.OPS.Input;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InputTest {

    @Test
    public void testInputCsv() {
        // 测试的命令字符串
        String cmd = "csv /home/sa/project/java/kettle/src/test/java/org/example/kettle/gugugu.csv true";//记得修改路径

        // 存储结果的列表
        List<HashMap<String, String>> result = new ArrayList<>();

        // 调用 input_csv 方法
        Input.input_csv(cmd, result);

        // 输出处理结果
        System.out.println("CSV 文件内容：");
        for (HashMap<String, String> row : result) {
            System.out.println(row);
        }

        // 验证输出
        if (result.isEmpty()) {
            System.out.println("没有读取到数据！");
        } else {
            System.out.println("成功读取 " + result.size() + " 条数据！");
        }
    }

    @Test
    public void testInputJson() {
        String cmd = "json /home/sa/project/java/kettle/src/test/java/org/example/kettle/gugugu.json";
        List<HashMap<String, String>> result = new ArrayList<>();
        Input.input_json(cmd, result);
        System.out.println("JSON 文件内容：");
        result.forEach(System.out::println);
        if (result.isEmpty()) {
            System.out.println("没有读取到数据！");
        }
    }

    @Test
    public void testInputXml(){
        String cmd="xml /home/sa/project/java/kettle/pom.xml";
        List<HashMap<String,String>> result=new ArrayList<>();
        Input.input_xml(cmd,result);
        System.out.println("XML 文件内容：");
        result.forEach(System.out::println);
        if(result.isEmpty()){
            System.out.println();
        }
    }
}
