package RunTask.pojo;

import RunTask.pojo.OPS.Step;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Process {
    // 在springboot外面使用该包需要把下面的注解删除
    @JsonProperty("main")
    private List<Step> steps = new ArrayList<>();
    
    public List<Step> getSteps() {
        return steps;
    }
    
    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
    
    public Process() {
    }
    
    public Process(List<Step> steps) {
        this.steps = steps;
    }
    
    @Override
    public String toString() {
        return "Process{" +
                "steps=" + steps +
                '}';
    }
    
}
