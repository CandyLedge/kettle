package org.example.kettle;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DataController.class)
public class DataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testAddField() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("key", "testKey");
        request.put("value", "testValue");

        mockMvc.perform(post("/data/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.testKey").value("testValue"));
    }

    @Test
    public void testDeleteField() throws Exception {
        // 先添加一个字段
        Map<String, Object> addRequest = new HashMap<>();
        addRequest.put("key", "keyToDelete");
        addRequest.put("value", "valueToDelete");
        mockMvc.perform(post("/data/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)));

        // 删除字段
        mockMvc.perform(delete("/data/delete")
                        .param("key", "keyToDelete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keyToDelete").doesNotExist());
    }

    @Test
    public void testRenameField() throws Exception {
        // 先添加一个字段
        Map<String, Object> addRequest = new HashMap<>();
        addRequest.put("key", "oldKey");
        addRequest.put("value", "oldValue");
        mockMvc.perform(post("/data/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)));

        // 重命名字段
        Map<String, String> renameRequest = new HashMap<>();
        renameRequest.put("oldKey", "oldKey");
        renameRequest.put("newKey", "newKey");
        mockMvc.perform(put("/data/rename")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(renameRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oldKey").doesNotExist())
                .andExpect(jsonPath("$.newKey").value("oldValue"));
    }

    @Test
    public void testChangeFieldType() throws Exception {
        // 先添加一个字符串类型的字段
        Map<String, Object> addRequest = new HashMap<>();
        addRequest.put("key", "stringKey");
        addRequest.put("value", "123");
        mockMvc.perform(post("/data/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)));

        // 更改字段类型为整数
        Map<String, Object> changeTypeRequest = new HashMap<>();
        changeTypeRequest.put("key", "stringKey");
        changeTypeRequest.put("type", "int");
        mockMvc.perform(put("/data/changeType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeTypeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stringKey").isNumber());
    }
}