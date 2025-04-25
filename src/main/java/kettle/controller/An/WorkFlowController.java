package kettle.controller.An;

import kettle.pojo.WorkFlow;
import org.springframework.web.bind.annotation.*;
//api/flow
@RestController
@RequestMapping("/api")
public class WorkFlowController {

    @PostMapping("/flow")
    public String receiveWorkFlow(@RequestBody WorkFlow workFlow) {
        // 处理接收到的 WorkFlow 对象
        workFlow.getMain().forEach(step -> {
            System.out.println("Step: " + step.getStep());
            System.out.println("Description: " + step.getDescription());
            System.out.println("Type: " + step.getType());
            System.out.println("PrivateField: " + step.getPrivateField());
            System.out.println("----------");
        });
        return "Workflow received ok";
    }
}
