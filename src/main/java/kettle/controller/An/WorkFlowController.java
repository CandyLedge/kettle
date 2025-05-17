package kettle.controller.An;

import RunTask.Running;
import RunTask.pojo.Process;
import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.*;

//api/flow
@RestController
public class WorkFlowController {
    
    @PostMapping("/make_process_file")
    public String receiveWorkFlow(@RequestBody Process steps) {
        // 输出接收到的 steps 对象
        steps.getSteps()
             .forEach(step -> {
                 System.out.println("Step: " + step.getStep());
                 System.out.println("Description: " + step.getDescription());
                 System.out.println("Type: " + step.getType());
                 System.out.println("PrivateField: " + step.getPrivateField());
                 System.out.println("----------");
             });
        // 调用处理代码
        Running.runTask(steps);
        return JSONUtil.createObj()
                       .put("code", "200")
                       .put("message", "ok")
                       .toString();
    }
}
