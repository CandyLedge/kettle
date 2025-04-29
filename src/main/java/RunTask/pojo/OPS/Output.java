package RunTask.pojo.OPS;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;

public class Output {
    // 这里封装了输出的操作 例如：数据库，文件···
    public static void output_database(String cmd, HashMap<String, String> result) {
        String dbConnectionId = cmd.split(" ")[1];
        String tbName = cmd.split(" ")[2];
        String jsonStr = getJsonStr(result, tbName, dbConnectionId);
/*        String url = "http://localhost:3000/api/query/preview";
        String httpResult = HttpRequest.post(url)
                                       .header("Content-Type", "application/json")
                                       .form(jsonStr)
                                       .timeout(2000)
                                       .execute()
                                       .body();
        System.out.println("httpResult = " + httpResult);*/
        System.out.println("jsonStr = " + jsonStr);
    }
    
    private static String getJsonStr(HashMap<String, String> result, String tbName, String dbConnectionId) {
        // 传入一个Map,将map转为insert into语句
        StringBuffer sql = new StringBuffer("insert into ");
        sql.append(tbName)
           .append("(");
        String column = String.join(", ", result.keySet());
        sql.append(column)
           .append(") values (");
        String values = String.join(", ", result.values());
        sql.append(values)
           .append(");");
        SqlJson json = new SqlJson();
        json.setSql(sql.toString());
        json.setDatabasename(dbConnectionId);
        String jsonStr = JSONUtil.toJsonStr(json);
        return jsonStr;
    }
    
    private static class SqlJson {
        private String databasename;
        private String sql;
        
        public SqlJson() {
        }
        
        public String getDatabasename() {
            return databasename;
        }
        
        public void setDatabasename(String databasename) {
            this.databasename = databasename;
        }
        
        public SqlJson(String databasename, String sql) {
            this.databasename = databasename;
            this.sql = sql;
        }
        
        public String getSql() {
            return sql;
        }
        
        public void setSql(String sql) {
            this.sql = sql;
        }
    }
    
}
