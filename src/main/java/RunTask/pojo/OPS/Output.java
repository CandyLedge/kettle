package RunTask.pojo.OPS;

import java.util.HashMap;
import java.util.List;

public class Output {
    // 这里封装了输出的操作 例如：数据库，文件···
    public static void output_database(String cmd, HashMap<String, String> result){
        System.out.println("cmd = " + cmd);
        StringBuffer sb = new StringBuffer();
        sb.append("result -> { \n");
        for (String key : result.keySet()) {
            String value = result.get(key);
            sb.append("\t").append("key").append(" -> ").append(value).append("\n");
        }
        sb.append("}\n");
        System.out.print(sb);
    }
}
