package kettle.pojo;

import java.util.HashMap;
import java.util.Map;

public class DataModel {
    private Map<String, Object> data = new HashMap<>();

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    // 添加字段
    public void addField(String key, Object value) {
        data.put(key, value);
    }

    // 删除字段
    public void removeField(String key) {
        data.remove(key);
    }

    // 重命名字段
    public void renameField(String oldKey, String newKey) {
        if (data.containsKey(oldKey)) {
            Object value = data.get(oldKey);
            data.remove(oldKey);
            data.put(newKey, value);
        }
    }

    // 修改字段类型
    public void changeFieldType(String key, Class<?> newType) {
        if (data.containsKey(key)) {
            Object value = data.get(key);
            if (newType == Integer.class && value instanceof String) {
                data.put(key, Integer.parseInt((String) value));
            }else if (newType == Long.class && value instanceof String) {
                data.put(key, Long.parseLong((String) value));
            } else if (newType == Double.class && value instanceof String) {
                data.put(key, Double.parseDouble((String) value));
            } else if (newType == Boolean.class && value instanceof String) {
                data.put(key, Boolean.parseBoolean((String) value));
            }
        }
    }
}
