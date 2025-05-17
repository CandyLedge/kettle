package RunTask.pojo;

import RunTask.pojo.OPS.Middle;
import RunTask.pojo.OPS.Step;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiddleStepDealwith {
    private Step start;
    private HashMap<String, Step> indexToStepMap;
    private List<HashMap<String, String>> results;
    
    public MiddleStepDealwith(Step start, HashMap<String, Step> indexToStepMap, List<HashMap<String, String>> results) {
        this.start = start;
        this.indexToStepMap = indexToStepMap;
        this.results = results;
    }
    
    public void exec() {
        String stepFunction = start.getType().split("-")[1];
        List<Step> nexts = new ArrayList<>();
        extractSteps(start.getPrivateField()).forEach(i -> {
            Step step = indexToStepMap.get(i.toString());
            nexts.add(step);
        });
        if (start.getType().startsWith("output")) {
            try {
                Method method = Class.forName("RunTask.pojo.OPS.Output")
                                     .getMethod(stepFunction, String.class, HashMap.class);
                for (HashMap<String, String> result : results) {
                    method.invoke(null, start.getPrivateField(), result);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage() + this);
            }
            
        } else if (start.getType().startsWith("middle")) {
            if (nexts.size() <= 1) {
                try {
                    Method method = Class.forName("RunTask.pojo.OPS.Middle")
                                         .getMethod(stepFunction, String.class, HashMap.class, List.class);
                    method.invoke(null, start.getPrivateField(), indexToStepMap, results);
                    new MiddleStepDealwith(nexts.get(0), indexToStepMap, results).exec();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage() + this);
                }
            } else {
                try {
                    Method method = Class.forName("RunTask.pojo.OPS.Middle")
                                         .getMethod(stepFunction, String.class, HashMap.class, List.class);
                    //noinspection unchecked
                    HashMap<String, List<HashMap<String, String>>> stepToDatas = (HashMap<String, List<HashMap<String, String>>>) method.invoke(null, start.getPrivateField(), indexToStepMap, results);
                    for (String step : stepToDatas.keySet()) {
                        List<HashMap<String, String>> datas = stepToDatas.get(step);
                        new MiddleStepDealwith(indexToStepMap.get(step), indexToStepMap, datas).exec();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage() + this.start.toString());
                }
            }
        } else {
            throw new RuntimeException("传入非法的步骤!");
        }
        
    }
    
    private static List<Integer> extractSteps(String input) {
        List<Integer> steps = new ArrayList<>();
        // 定义正则表达式模式，用于匹配step(n)格式
        Pattern pattern = Pattern.compile("step\\((\\d+)\\)");
        Matcher matcher = pattern.matcher(input);
        
        // 查找所有匹配项并提取数字
        while (matcher.find()) {
            steps.add(Integer.parseInt(matcher.group(1)));
        }
        
        return steps;
    }
    
    // getter and setter ...
    
    public List<HashMap<String, String>> getResults() {
        return results;
    }
    
    public void setResults(List<HashMap<String, String>> results) {
        this.results = results;
    }
    
    public Step getStart() {
        return start;
    }
    
    public void setStart(Step start) {
        this.start = start;
    }
    
    public HashMap<String, Step> getIndexToStepMap() {
        return indexToStepMap;
    }
    
    public void setIndexToStepMap(HashMap<String, Step> indexToStepMap) {
        this.indexToStepMap = indexToStepMap;
    }
    
    @Override
    public String toString() {
        return "MiddleStepDealwith{" +
                "start=" + start +
                ", indexToStepMap=" + indexToStepMap +
                ", results=" + results +
                '}';
    }
}
