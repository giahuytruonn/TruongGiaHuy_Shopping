package iuh.service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeminiAiService {

    private final Client client;
    private final SimpleRetriever retriever;

    @Autowired
    public GeminiAiService(SimpleRetriever retriever, 
                          @Value("${gemini.api.key:}") String apiKey) {
        this.retriever = retriever;
        
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("gemini.api.key không được để trống trong application.properties. " +
                    "Vui lòng thêm: gemini.api.key=your-api-key");
        }
        
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    public String processUserInput(String userInput) {
        try {
            // Lấy context từ documents và database
            List<String> docs = retriever.retrieve(userInput);
            String context = String.join("\n", docs);
            
            // Tạo prompt với system instruction
            String systemPrompt = """
                    Bạn là một trợ lý AI thông minh và thân thiện của cửa hàng ShopNow.
                    Nhiệm vụ của bạn là hỗ trợ khách hàng với các câu hỏi về:
                    - Thông tin sản phẩm (tên, giá, tình trạng tồn kho, danh mục)
                    - Đơn hàng và lịch sử mua hàng
                    - Chính sách mua hàng và hướng dẫn sử dụng
                    - Tư vấn và gợi ý sản phẩm
                    
                    Hãy trả lời một cách thân thiện, chuyên nghiệp và hữu ích.
                    Sử dụng thông tin từ context được cung cấp để trả lời chính xác.
                    Nếu không có thông tin trong context, hãy nói rõ và đề xuất cách khác để hỗ trợ khách hàng.
                    Luôn trả lời bằng tiếng Việt.
                    """;
            
            String fullPrompt = String.format("""
                    %s
                    
                    === CONTEXT VỀ CỬA HÀNG ===
                    %s
                    
                    === CÂU HỎI CỦA KHÁCH HÀNG ===
                    %s
                    
                    Hãy trả lời câu hỏi của khách hàng dựa trên context trên.
                    """, systemPrompt, context, userInput);
            
            // Cấu hình với system instruction
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .systemInstruction(Content.fromParts(Part.fromText(fullPrompt)))
                    .build();
            
            // Gọi Gemini API
            GenerateContentResponse response = 
                    client.models.generateContent("gemini-2.0-flash-exp", userInput, config);
            
            return response.text().trim();
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, đã có lỗi xảy ra khi xử lý câu hỏi của bạn. Vui lòng thử lại sau. Lỗi: " + e.getMessage();
        }
    }
}

