package RunTask.pojo.OPS;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.List;

public class Output {
    // 这里封装了输出的操作 例如：数据库，文件···
    public static void output_database(String cmd, HashMap<String, String> result){
//        System.out.println("cmd = " + cmd);
//        StringBuffer sb = new StringBuffer();
//        sb.append("result -> { \n");
//        for (String key : result.keySet()) {
//            String value = result.get(key);
//            sb.append("\t").append("key").append(" -> ").append(value).append("\n");
//        }
//        sb.append("}\n");
//        System.out.print(sb);
        String dbName = cmd.split(" ")[1];
        String tbName = cmd.split(" ")[2];
        StringBuffer sql = new StringBuffer("insert into ");
        sql.append(tbName).append("(");
        String column = String.join(", ",result.keySet());
        sql.append(column).append(") values (");
        String values = String.join(", ", result.values());
        sql.append(values).append(");");
        SqlJson json = new SqlJson();
        json.setSql(sql.toString());
        String jsonStr = JSONUtil.toJsonStr(json);
        System.out.println("jsonStr = " + jsonStr);
    }
    private static class SqlJson{
        private String sql;
        private List<Object> params;
        
        public SqlJson() {
        }
        
        public SqlJson(String sql, List<Object> params) {
            this.sql = sql;
            this.params = params;
        }
        
        public String getSql() {
            return sql;
        }
        
        public void setSql(String sql) {
            this.sql = sql;
        }
        
        public List<Object> getParams() {
            return params;
        }
        
        public void setParams(List<Object> params) {
            this.params = params;
        }
    }
    
}
