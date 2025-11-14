package iuh.service;

import iuh.model.Customer;
import iuh.model.Order;
import iuh.model.OrderLine;
import iuh.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShoppingAssistantService {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    /**
     * Lấy context về sản phẩm từ database
     */
    public String getProductsContext() {
        List<Product> products = productService.findAll();
        if (products.isEmpty()) {
            return "Hiện tại cửa hàng chưa có sản phẩm nào.";
        }

        StringBuilder context = new StringBuilder("DANH SÁCH SẢN PHẨM:\n");
        for (Product product : products) {
            context.append(String.format("- ID: %d, Tên: %s, Giá: %s VNĐ, Tình trạng: %s",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.isInStock() ? "Còn hàng" : "Hết hàng"));
            
            if (product.getCategory() != null) {
                context.append(String.format(", Danh mục: %s", product.getCategory().getName()));
            }
            
            if (product.getComments() != null && !product.getComments().isEmpty()) {
                context.append(String.format(", Số bình luận: %d", product.getComments().size()));
            }
            
            context.append("\n");
        }
        return context.toString();
    }

    /**
     * Lấy context về đơn hàng của customer hiện tại
     */
    public String getCustomerOrdersContext() {
        Customer customer = customerService.getCurrentCustomer();
        if (customer == null) {
            return "Khách hàng chưa đăng nhập. Không có thông tin đơn hàng.";
        }

        List<Order> orders = orderService.findAll();
        List<Order> customerOrders = orders.stream()
                .filter(order -> order.getCustomer() != null && 
                        order.getCustomer().getId().equals(customer.getId()))
                .collect(Collectors.toList());

        if (customerOrders.isEmpty()) {
            return String.format("Khách hàng %s chưa có đơn hàng nào.", customer.getName());
        }

        StringBuilder context = new StringBuilder(String.format("ĐƠN HÀNG CỦA KHÁCH HÀNG %s:\n", customer.getName()));
        for (Order order : customerOrders) {
            context.append(String.format("\nĐơn hàng ID: %d, Ngày đặt: %s\n",
                    order.getId(),
                    order.getDate() != null ? order.getDate().getTime().toString() : "N/A"));
            
            if (order.getOrderLines() != null && !order.getOrderLines().isEmpty()) {
                context.append("Chi tiết sản phẩm:\n");
                for (OrderLine orderLine : order.getOrderLines()) {
                    Product product = orderLine.getProduct();
                    context.append(String.format("  - %s: Số lượng %d, Giá mua %s VNĐ\n",
                            product != null ? product.getName() : "N/A",
                            orderLine.getAmount(),
                            orderLine.getPurchasePrice()));
                }
            }
        }
        return context.toString();
    }

    /**
     * Đọc nội dung từ file shopping-info.txt
     */
    public String getShoppingInfoContext() {
        try {
            ClassPathResource resource = new ClassPathResource("documents/shopping-info.txt");
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "Không thể đọc thông tin về cửa hàng.";
        }
    }

    /**
     * Tạo context tổng hợp cho AI
     */
    public String buildContext(String userMessage) {
        StringBuilder context = new StringBuilder();
        
        // Thêm thông tin về cửa hàng từ documents
        context.append("=== THÔNG TIN VỀ CỬA HÀNG ===\n");
        context.append(getShoppingInfoContext());
        context.append("\n\n");
        
        // Thêm thông tin sản phẩm
        context.append("=== THÔNG TIN SẢN PHẨM ===\n");
        context.append(getProductsContext());
        context.append("\n\n");
        
        // Thêm thông tin đơn hàng nếu user đã đăng nhập
        Customer customer = customerService.getCurrentCustomer();
        if (customer != null) {
            context.append("=== THÔNG TIN ĐƠN HÀNG ===\n");
            context.append(getCustomerOrdersContext());
            context.append("\n\n");
        }
        
        return context.toString();
    }
}






