package kettle.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {
    @PostMapping("/task")
    public String receiveTask(@RequestBody Task task) {
        List<TaskStep> steps = task.getSteps();
        for (TaskStep step : steps) {
            System.out.println("Step: " + step.getStepName());
            System.out.println("Description: " + step.getDescription());
            System.out.println("Priority: " + step.getPriority());
            System.out.println("Due Date: " + step.getDueDate());
            System.out.println("----------");
        }
        return "Task received ok";
    }
}








//=======================
// 假设的Task类和TaskStep类
class Task {
    private List<TaskStep> steps;

    public List<TaskStep> getSteps() {
        return steps;
    }

}

class TaskStep {
    private String stepName;
    private String description;
    private int priority;
    private String dueDate;

    public String getStepName() {
        return stepName;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public String getDueDate() {
        return dueDate;
    }

}
