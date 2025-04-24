package kettle.controller.BaiNaiChang;

import cn.hutool.json.JSONUtil;
import kettle.pojo.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StepMapping {
    // 接受步骤json文件
    @PostMapping("/make_process_file")
    @ResponseBody
    public String makeProcessFile(@RequestBody Data json) {
        System.out.println(json);
        return JSONUtil.createObj()
                       .set("status", "200")
                       .set("msg", "ok")
                       .toString();
    }
}
