package RunTask;

import RunTask.pojo.OPS.Input;
import RunTask.pojo.OPS.Middle;
import RunTask.pojo.OPS.Step;
import RunTask.pojo.Process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// 传入process开始执行任务
public class Running {
    public static void runTask(Process process) {
        List<Step> steps = process.getSteps();
        List<Step> input = new ArrayList<>();
        List<Step> middle = new ArrayList<>();
        List<Step> output = new ArrayList<>();
        // HashMap -> 对象
        // List<HashMap> -> 多个对象
        List<HashMap<String, String>> result = new ArrayList<>();
        // 将步骤分为3大类
        steps.forEach(step -> {
            if (step.getType()
                    .startsWith("input")) {
                input.add(step);
            }
            if (step.getType()
                    .startsWith("middle")) {
                middle.add(step);
            }
            if (step.getType()
                    .startsWith("output")) {
                output.add(step);
            }
        });
        // 处理input类
        
        for (Step step : input) {
            String inputMode = step.getType()
                                   .split("-")[1];
            switch (inputMode) {
                case "csv":
                    Input.input_csv(step.getPrivateField(), result);
                    break;
                case "json":
                    Input.input_json(step.getPrivateField(), result);
                    break;
                default:
                    throw new RuntimeException("有没处理的input！");
            }
        }
        
        // 处理middle类
        for (Step step : middle) {
            String mode = step.getType()
                                   .split("-")[1];
            switch (mode){
                case "where_data_by_column":
                    Middle.middle_whereDataByColumn(step.getPrivateField(), output, result);
                    break;
                default:
                    throw new RuntimeException("有没处理的middle！");
            }
        }
    }
}
