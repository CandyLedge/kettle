package kettle.controller.WWZ;

import java.util.ArrayList;
import java.util.List;

import kettle.dao.NumberRange;
import org.springframework.stereotype.Service;

@Service
public class NumberRangeClassifier {
    private List<NumberRange> ranges;
    private String unknownValue;

    public NumberRangeClassifier() {
        this.ranges = new ArrayList<>();
        this.unknownValue = "未知";
    }

    public void setRanges(List<NumberRange> ranges) {
        this.ranges = ranges;
    }

    public String classify(double value) {
        for (NumberRange range : ranges) {
            if (range.isInRange(value)) {
                return range.getLabel();
            }
        }
        return unknownValue;
    }
}