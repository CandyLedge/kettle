package org.example.kettle.controller.WWZ;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class FilePreviewController {
    @GetMapping("/preview")
    public ResponseEntity<Map<String, Object>> previewFile(@RequestParam String path) {
        Map<String, Object> response = new HashMap<>();
        Path filePath = Paths.get(path);
        try {
            if (!Files.exists(filePath)) {
                System.out.println("路径不存在：" + filePath);
                response.put("status", "error");
                response.put("message", "文件路径不存在");
                return ResponseEntity.badRequest().body(response);
            }
            if (!Files.isRegularFile(filePath)) {
                System.out.println("路径不是文件：" + filePath);
                response.put("status", "error");
                response.put("message", "路径不是文件");
                return ResponseEntity.badRequest().body(response);
            }
            List<String> lines = Files.readAllLines(filePath);
            List<String> previewLines = lines.subList(0, Math.min(3, lines.size()));

            response.put("status", "success");
            response.put("previewData", previewLines);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            System.out.println("读取文件失败：" + e.getMessage());
            response.put("status", "error");
            response.put("message", "读取文件失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
