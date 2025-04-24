package RunTask.pojo;

import RunTask.pojo.OPS.Step;

import java.util.ArrayList;
import java.util.List;

public class Process {
    private List<Step> steps = new ArrayList<>();
    
    public List<Step> getSteps() {
        return steps;
    }
    public void setSteps(List<Step> steps) {
        this.steps = steps;
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
