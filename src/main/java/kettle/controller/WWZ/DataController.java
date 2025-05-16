package kettle.controller.WWZ;
import kettle.pojo.DataModel;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataController {
    private final DataModel dataModel = new DataModel();
    @PostMapping("/add")
    public Map<String, Object> addField(@RequestBody Map<String, Object> request) {
        String key = (String) request.get("key");
        Object value = request.get("value");
        dataModel.addField(key, value);
        return dataModel.getData();
    }
    @DeleteMapping("/delete")
    public Map<String, Object> deleteField(@RequestParam String key) {
        dataModel.removeField(key);
        return dataModel.getData();
    }
    @PutMapping("/rename")
    public Map<String, Object> renameField(@RequestBody Map<String, String> request) {
        String oldKey = request.get("oldKey");
        String newKey = request.get("newKey");
        dataModel.renameField(oldKey, newKey);
        return dataModel.getData();
    }
    @PutMapping("/changeType")
    public Map<String, Object> changeFieldType(@RequestBody Map<String, Object> request) {
        String key = (String) request.get("key");
        String type = (String) request.get("type");
        if ("int".equals(type)) {
            dataModel.changeFieldType(key,Integer.class);
        }else if ("long".equals(type)) {
            dataModel.changeFieldType(key, Long.class);
        } else if ("double".equals(type)) {
            dataModel.changeFieldType(key, Double.class);
        } else if ("boolean".equals(type)) {
            dataModel.changeFieldType(key, Boolean.class);
        }
        return dataModel.getData();
    }
}
