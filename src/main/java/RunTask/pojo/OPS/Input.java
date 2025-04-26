package RunTask.pojo.OPS;

import java.util.HashMap;
import java.util.List;

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
    
    }
}
