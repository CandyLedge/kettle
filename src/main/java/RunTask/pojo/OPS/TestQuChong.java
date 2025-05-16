package RunTask.pojo.OPS;

public class TestQuChong {
    public static void main(String[] args) {
        QuChong<String, String> uniqueMap = new QuChong<>();

        uniqueMap.put("a", "abc");
        uniqueMap.put("b", "def");
        uniqueMap.put("c", "abc"); // 这个不插

        System.out.println("Map 内容: " + uniqueMap); // 输出 {a=apple, b=banana}
    }
}
