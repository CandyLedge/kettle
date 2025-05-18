package RunTask.pojo.OPS;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Input {
    // 这里封装了输入的操作 都是静态方法，因为要打包出去重复使用
    // 方法名以该类的名称小写开头，下划线分割，后面写具体的事，其他类同理

    /**
     * csv 文本解析： 以逗号作为分隔符
     * 例子：cmd的格式 -> csv /home/xxx/input_text.csv true
     * 第二个代表读取的文件路径
     * 第三个代表csv文件的第一行是否为列名
     * ---------------------
     * 读取CSV文件并处理数据
     *
     * @param cmd    命令字符串，包含CSV文件的路径和相关参数
     * @param result 存储处理结果的列表
     */

    public static void input_csv(String cmd, List<HashMap<String, String>> result) {
        String[] tokens = cmd.trim().split("\\s+");

        // 至少要有文件路径
        if (tokens.length < 2) {
            throw new IllegalArgumentException("cmd format: csv <filePath> [hasHeader]");
        }

        String filePath = tokens[1];
        boolean hasHeader = tokens.length >= 3 && Boolean.parseBoolean(tokens[2]);
        List<String> headers = new ArrayList<>();

        try (BufferedReader reader = FileUtil.getReader(filePath, Charset.defaultCharset())) {
            String line;
            // 如果有表头，读取表头
            if (hasHeader && (line = reader.readLine()) != null) {
                headers = Arrays.asList(line.trim().split(","));
            }
            // 读取每一行数据并映射
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] values = line.split(",");
                HashMap<String, String> row = new HashMap<>();
                for (int i = 0; i < values.length; i++) {
                    String key = hasHeader && i < headers.size() ? headers.get(i) : "col" + i;
                    String val = values[i].trim();
                    row.put(key, val);
                }
                result.add(row);
            }
        } catch (IOException e) {
            throw new RuntimeException("read file fail: " + filePath, e);
        }
    }

    /**
     * json 文本解析
     * 例子：cmd的格式 -> json /home/xxx/input_text.json
     * 第二个参数代表读取的文件路径
     * ---------------------
     * 读取JSON文件并处理数据
     *
     * @param cmd    命令字符串，包含JSON文件的路径
     * @param result 存储处理结果的列表
     */

    //json /home/xxx/input_text.json
    public static void input_json(String cmd, List<HashMap<String, String>> result) {
        String[] tokens = cmd.trim().split("\\s+");

        if (tokens.length < 2) {
            throw new IllegalArgumentException("cmd format: json <filePath>");
        }

        String filePath = tokens[1];
        String jsonStr = FileUtil.readString(filePath, Charset.defaultCharset());

        if (JSONUtil.isTypeJSONArray(jsonStr)) {
            JSONArray jsonArray = JSONUtil.parseArray(jsonStr);
            for (Object obj : jsonArray) {
                if (obj instanceof JSONObject) {
                    HashMap<String, String> map = new HashMap<>();
                    flattenJson((JSONObject) obj, "", map);
                    result.add(map);
                }
            }
        } else if (JSONUtil.isTypeJSONObject(jsonStr)) {
            JSONObject jsonObj = JSONUtil.parseObj(jsonStr);
            HashMap<String, String> map = new HashMap<>();
            flattenJson(jsonObj, "", map);
            result.add(map);
        } else {
            throw new RuntimeException("invalid json format: " + filePath);
        }
    }

    /**
     * 扁平化 JSON 对象，将嵌套结构展开为路径形式。
     * @param json  输入的 JSON 对象
     * @param path  当前路径前缀（初始传 ""）
     * @param map   最终输出的扁平化 map
     */
    private static void flattenJson(Object json, String path, Map<String, String> map) {
        if (json instanceof JSONObject) {
            JSONObject obj = (JSONObject) json;
            for (Map.Entry<String, Object> entry : obj.entrySet()) {
                String newPath = path.isEmpty() ? entry.getKey() : path + "." + entry.getKey();
                flattenJson(entry.getValue(), newPath, map);
            }
        } else if (json instanceof JSONArray) {
            JSONArray arr = (JSONArray) json;
            for (int i = 0; i < arr.size(); i++) {
                String newPath = path + "[" + i + "]";
                flattenJson(arr.get(i), newPath, map);
            }
        } else if (json != null) {
            map.put(path, json.toString());
        }
    }


    //xml /home/xxx/input_text.xml
    public static void input_xml(String cmd, List<HashMap<String, String>> result) {
        String[] tokens = cmd.trim().split("\\s+");

        if (tokens.length < 2) {
            throw new IllegalArgumentException("cmd format: xml <filePath>");
        }

        String filePath = tokens[1];

        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(filePath));
            Element root = document.getRootElement();

            Stack<Element> elementStack = new Stack<>();
            Stack<String> pathStack = new Stack<>();
            Stack<HashMap<String, String>> mapStack = new Stack<>();

            elementStack.push(root);
            pathStack.push(root.getName());
            mapStack.push(new HashMap<>());

            while (!elementStack.isEmpty()) {
                Element element = elementStack.pop();
                String path = pathStack.pop();
                HashMap<String, String> map = mapStack.pop();

                // 属性使用 @ 连接
                for (Iterator<Attribute> attrIt = element.attributeIterator(); attrIt.hasNext(); ) {
                    Attribute attr = attrIt.next();
                    map.put(path + "@" + attr.getName(), attr.getValue());
                }

                List<Element> children = element.elements();

                if (children.isEmpty()) {
                    String text = element.getTextTrim();
                    if (!text.isEmpty()) {
                        map.put(path, text);
                    }
                    result.add(map);
                } else {
                    for (int i = children.size() - 1; i >= 0; i--) {
                        Element child = children.get(i);
                        elementStack.push(child);
                        pathStack.push(path + "." + child.getName());  // 用点连接
                        mapStack.push(new HashMap<>(map));  // 拷贝 map
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML file: " + filePath, e);
        }
    }
}
