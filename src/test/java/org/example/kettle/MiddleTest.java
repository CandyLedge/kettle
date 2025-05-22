package org.example.kettle;

import RunTask.pojo.OPS.Middle;
import org.junit.jupiter.api.Test;

import java.util.*;


public class MiddleTest {

    @Test
    public void testSortRowsAsc() {
        List<HashMap<String, String>> result = new ArrayList<>();
        result.add(new HashMap<>(Map.of("name", "Alice", "score", "88")));
        result.add(new HashMap<>(Map.of("name", "Bob", "score", "75")));
        result.add(new HashMap<>(Map.of("name", "Charlie", "score", "92")));

        String cmd = "sort(score)";
        Middle.sortRows(cmd, result);

        System.out.println("排序后（升序）：");
        result.forEach(System.out::println);

    }

    @Test
    public void testSortRowsDesc() {
        List<HashMap<String, String>> result = new ArrayList<>();
        result.add(new HashMap<>(Map.of("name", "Alice", "score", "88")));
        result.add(new HashMap<>(Map.of("name", "Bob", "score", "75")));
        result.add(new HashMap<>(Map.of("name", "Charlie", "score", "92")));

        String cmd = "sort(score, desc)";
        Middle.sortRows(cmd, result);

        System.out.println("排序后（降序）：");
        result.forEach(System.out::println);

    }

    @Test
    public void testPivotRows() {
        List<HashMap<String, String>> result = new ArrayList<>();
        result.add(new HashMap<>(Map.of("department", "HR", "name", "Alice")));
        result.add(new HashMap<>(Map.of("department", "HR", "name", "Bob")));
        result.add(new HashMap<>(Map.of("department", "IT", "name", "Charlie")));

        String cmd = "pivot(department, name)";
        Middle.pivotRows(cmd, result);

        System.out.println("行转列结果：");
        result.forEach(System.out::println);

    }

    @Test
    public void testCalculateField() {
        List<HashMap<String, String>> result = new ArrayList<>();
        result.add(new HashMap<>(Map.of("score1", "80", "score2", "20")));
        result.add(new HashMap<>(Map.of("score1", "60", "score2", "35")));

        String cmd = "calc(total = score1 + score2)";
        Middle.calculateField(cmd, result);

        System.out.println("字段计算结果：");
        result.forEach(System.out::println);

    }

    @Test
    public void testCalculateFieldWithLiteral() {
        List<HashMap<String, String>> result = new ArrayList<>();
        result.add(new HashMap<>(Map.of("score1", "50")));

        String cmd = "calc(result = score1 + 30)";
        Middle.calculateField(cmd, result);

        System.out.println("字段计算（包含常数）结果：");
        result.forEach(System.out::println);

    }



}