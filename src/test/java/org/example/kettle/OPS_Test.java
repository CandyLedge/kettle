package org.example.kettle;

import RunTask.pojo.OPS.Middle;
import RunTask.pojo.OPS.Step;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OPS_Test {
    @Test
    public void test01(){
        List<Step> outputs = new ArrayList<>();
        outputs.add(new Step(2,"","output-database","database woman man01"));
        outputs.add(new Step(3,"","output-database","database woman woman01"));
        List<HashMap<String,String>> result = new ArrayList<>();
        HashMap<String,String> man1 = new HashMap<>();
        man1.put("id","1");
        man1.put("age","18");
        man1.put("sex","男");
        HashMap<String,String> woman1 = new HashMap<>();
        woman1.put("id","2");
        woman1.put("age","18");
        woman1.put("sex","女");
        result.add(man1);
        result.add(woman1);
        Middle.middle_whereDataByColumn("switch(sex)\ncase 男:step(2)\ncase 女:step(3)",outputs,result);
    }
}
