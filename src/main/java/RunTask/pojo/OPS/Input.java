package RunTask.pojo.OPS;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;



public class Input {
    // 这里封装了输入的操作 都是静态方法，因为要打包出去重复使用
    // 方法名以该类的名称小写开头，下划线分割，后面写具体的事，其他类同理
    
    /**
     * csv 文本解析： 以空格作为分隔符
     * 例子：cmd的格式 -> csv /home/xxx/input_text.txt true
     * 第二个代表读取的文件路径
     * 第三个代表csv文件的第一行是否为列名
     * ---------------------
     * 读取CSV文件并处理数据
     *
     * @param cmd 命令字符串，包含CSV文件的路径和相关参数
     * @param result 存储处理结果的列表
     */

    public static void input_csv(String cmd, List<HashMap<String, String>> result) {
//        String cmd = "csv /data/users.txt true";
        String[] tokens = cmd.trim().split("\\s+");

        //至少得有类型和路径
        if (tokens.length < 2) {
            throw new IllegalArgumentException("命令格式: csv <filePath> [hasHeader]");
        }

        String filePath = tokens[1];
        //parseBoolean为"true"时，返回true
        boolean hasHeader = tokens.length >= 3 && Boolean.parseBoolean(tokens[2]);
        List<String> headers = new ArrayList<>();

        try (BufferedReader reader = FileUtil.getReader(filePath, Charset.defaultCharset())) {
            String line;
            // 读取表头
            if (hasHeader && (line = reader.readLine()) != null) {
                headers = Arrays.asList(line.trim().split("\\s+"));
            }
            // 读取、映射每行
            while ((line = reader.readLine()) != null) {
                String[] values = line.trim().split("\\s+");
                HashMap<String, String> row = new HashMap<>();
                for (int i = 0; i < values.length; i++) {
                    String key = hasHeader && i < headers.size() ? headers.get(i) : "col" + i;
                    row.put(key, values[i]);
                }
                result.add(row);
            }
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败: " + filePath, e);
        }
    }
}
