package RunTask;

import RunTask.pojo.MiddleStepDealwith;
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
        // HashMap -> 对象
        // List<HashMap> -> 多个对象
        List<HashMap<String, String>> result = new ArrayList<>();
        
        steps.forEach(step -> {
            if (step.getType()
                    .startsWith("input")) {
                input.add(step);
            }
            if (step.getType()
                    .startsWith("middle")) {
                middle.add(step);
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
        
        // 建立一个 步骤下标 -> 步骤对象的HashMap (不包含input步骤)
        HashMap<String, Step> indexToStepMap = new HashMap<>();
        for (Step step : steps) {
            if (step.getType()
                    .startsWith("input")) {
                continue;
            }
            String stepIndex = step.getStep()
                                   .toString();
            indexToStepMap.put(stepIndex, step);
        }
        new MiddleStepDealwith(middle.get(0), indexToStepMap, result).exec();
    }
}
