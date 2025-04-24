package RunTask.pojo.ABC;

public class Input {
    // 这里封装了输入的操作 都是静态方法，因为要打包出去重复使用
    // 方法名以该类的名称小写开头，下划线分割，后面写具体的事，其他类同理
    
    /**
     * csv 文本解析： 以空格作为分隔符
     * 例子：csv /home/xxx/input_text.txt true
     * 第二个代表读取的文件路径
     * 第三个代表csv文件的第一行是否为列名
     *  */
    public static void input_csv(String cmd){
    
    }
}
