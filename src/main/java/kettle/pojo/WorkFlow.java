package kettle.pojo;
import lombok.Data;
import java.util.List;
//{
//        "main":[
//        {
//        "step":0,
//        "description":"表输入",
//        "type":"input-csv",
//        "privateField":"csv /home/xxx/input_text.txt true"
//        },
//        {
//        "step":1,
//        "description":"数据过滤,根据对象的属性分类",
//        "type":"middle-where_data_by_column",
//        "privateField":"switch(sex)\ncase 男:step(2)\ncase 女:step(3)"
//        },
//        {
//        "step":2,
//        "description":"表输出，输出到数据库里",
//        "type":"output-database",
//        "privateField":"database databaseName tableName"
//        },
//        {
//        "step":3,
//        "description":"表输出，输出到数据库里",
//        "type":"output-database",
//        "privateField":"database Woman table_01"
//        }
//        ]
//        }

@Data
public class WorkFlow {
    private List<Step> main;

    @Data
    public static class Step {
        private int step;
        private String description;
        private String type;
        private String privateField;
    }
}

