package kettle.pojo;

import RunTask.pojo.OPS.Step;

import java.util.ArrayList;
import java.util.List;

@lombok.Data
public class Data {
    private List<Step> main = new ArrayList<>();
}
