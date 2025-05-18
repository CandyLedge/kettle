package org.example.kettle;

import RunTask.pojo.OPS.Input;
import cn.hutool.core.io.resource.ResourceUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InputTest {

    @Test
    public void testInputCsv() {
        String filePath = ResourceUtil.getResource("gugugu.csv").getPath();
        String cmd = "csv " + filePath + " true";

        List<HashMap<String, String>> result = new ArrayList<>();
        Input.input_csv(cmd, result);

        System.out.println("CSV 文件内容：");
        for (HashMap<String, String> row : result) {
            System.out.println(row);
        }

        assertFalse(result.isEmpty(), "CSV 文件应包含数据！");
    }

    @Test
    public void testInputJson() {
        String filePath = ResourceUtil.getResource("gugugu.json").getPath();
        String cmd = "json " + filePath;

        List<HashMap<String, String>> result = new ArrayList<>();
        Input.input_json(cmd, result);

        System.out.println("JSON 文件内容：");
        result.forEach(System.out::println);

        assertFalse(result.isEmpty(), "JSON 文件应包含数据！");
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

