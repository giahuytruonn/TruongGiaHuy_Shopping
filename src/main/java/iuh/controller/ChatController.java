package iuh.controller;

import iuh.service.GeminiAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final GeminiAiService geminiAiService;

    @Autowired
    public ChatController(GeminiAiService geminiAiService) {
        this.geminiAiService = geminiAiService;
    }

    @GetMapping
    public String chatPage(Model model) {
        return "chat/chat";
    }

    @PostMapping(value = "/message", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        
        if (userMessage == null || userMessage.trim().isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("response", "Vui lòng nhập câu hỏi của bạn.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            String aiResponse = geminiAiService.processUserInput(userMessage);
            Map<String, String> response = new HashMap<>();
            response.put("response", aiResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("response", "Đã có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}

