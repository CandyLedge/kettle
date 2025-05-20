package org.example.kettle;

import RunTask.pojo.OPS.Middle;
import org.junit.jupiter.api.Test;

import java.util.*;


public class MiddleTest {

    @Test
    public void testSortRowsAsc() {
        List<HashMap<String, String>> data = new ArrayList<>();
        data.add(new HashMap<>(Map.of("name", "Alice", "score", "88")));
        data.add(new HashMap<>(Map.of("name", "Bob", "score", "75")));
        data.add(new HashMap<>(Map.of("name", "Charlie", "score", "92")));

        String cmd = "sort(score)";
        Middle.sortRows(cmd, data);

        System.out.println("排序后（升序）：");
        data.forEach(System.out::println);

    }

    @Test
    public void testSortRowsDesc() {
        List<HashMap<String, String>> data = new ArrayList<>();
        data.add(new HashMap<>(Map.of("name", "Alice", "score", "88")));
        data.add(new HashMap<>(Map.of("name", "Bob", "score", "75")));
        data.add(new HashMap<>(Map.of("name", "Charlie", "score", "92")));

        String cmd = "sort(score, desc)";
        Middle.sortRows(cmd, data);

        System.out.println("排序后（降序）：");
        data.forEach(System.out::println);

    }


}