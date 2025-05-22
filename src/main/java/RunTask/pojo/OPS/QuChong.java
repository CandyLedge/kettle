package RunTask.pojo.OPS;
import java.util.*;

public class QuChong {

    public static void quChong(String cmd, List<HashMap<String, String>> result) {
        Set<HashMap<String, String>> uniqueSet = new HashSet<>(result);
        result.clear();
        result.addAll(uniqueSet);
    }

}