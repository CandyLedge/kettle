package kettle.controller.WWZ;

import kettle.dao.NumberRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NumberRangeController {

    @Autowired
    private NumberRangeClassifier classifier;

    @GetMapping("/classify")
    public String classify(@RequestParam double value) {
        return classifier.classify(value);
    }

    @PostMapping("/setRanges")
    public String setRanges(@RequestBody List<NumberRange> ranges) {
        classifier.setRanges(ranges);
        return "范围设置成功";
    }
}