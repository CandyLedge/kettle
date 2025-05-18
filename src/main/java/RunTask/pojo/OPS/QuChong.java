package RunTask.pojo.OPS;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class QuChong<K, V> extends HashMap<K, V> {
    private final Set<V> values = new HashSet<>();

    @Override
    public V put(K key, V value) {
        if (value == null) {
            return null; // 直接忽略 null
        }

        // 如果value存在，不插入
        if (values.contains(value)) {
            return null;
        }

        // 否则就记录value，调用正常的 put 方法
        values.add(value);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 判断某个 value 是否已存在
     */
    public boolean containsValue(Object value) {
        return values.contains(value);
    }

    /**
     * 获取当前存储的所有 value 集合
     */
    public Set<V> getValues() {
        return new HashSet<>(values);

    }
}

