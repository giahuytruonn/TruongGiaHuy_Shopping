package iuh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class SimpleRetriever {

    @Autowired
    private ShoppingAssistantService shoppingAssistantService;

    /**
     * Retrieve relevant documents/context based on user input
     */
    public List<String> retrieve(String userInput) {
        List<String> docs = new ArrayList<>();
        
        try {
            // Đọc shopping info document
            ClassPathResource resource = new ClassPathResource("documents/shopping-info.txt");
            String shoppingInfo = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            docs.add(shoppingInfo);
        } catch (IOException e) {
            // Ignore if file not found
        }
        
        // Thêm context từ database
        String dbContext = shoppingAssistantService.buildContext(userInput);
        if (dbContext != null && !dbContext.isEmpty()) {
            docs.add(dbContext);
        }
        
        return docs;
    }
}

