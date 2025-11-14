package iuh.controller;

import iuh.model.Order;
import iuh.model.Product;
import iuh.service.OrderService;
import iuh.service.ProductService;
import iuh.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
@SessionAttributes("cart")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    /**
     * Khởi tạo cart trong session
     */
    @ModelAttribute("cart")
    public Map<Integer, Integer> initializeCart() {
        return new HashMap<>();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public String showAllOrders(@RequestParam(required = false) String keyword, Model model) {
        try {
            List<Order> orderlist;
            if (keyword != null && !keyword.trim().isEmpty()) {
                orderlist = orderService.search(keyword);
                model.addAttribute("keyword", keyword);
            } else {
                orderlist = orderService.findAll();
            }
            model.addAttribute("orders", orderlist);
            return "order/list";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading orders: " + e.getMessage());
            model.addAttribute("orders", new ArrayList<>());
            return "order/list";
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public String showOrder(@PathVariable int id, Model model) {
        Order order = orderService.findById(id);
        model.addAttribute("order", order);
        return "order/orderdetail";
    }

    @PostMapping("/buy/{productId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public String buyProduct(@PathVariable int productId, 
                           @RequestParam(defaultValue = "1") int quantity,
                           @SessionAttribute(value = "cart", required = false) Map<Integer, Integer> cart,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        if (cart == null) {
            cart = new HashMap<>();
        }
        
        Product product = productService.findById(productId);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Product not found");
            return "redirect:/product";
        }
        
        if (!product.isInStock()) {
            redirectAttributes.addFlashAttribute("error", "Product is out of stock");
            return "redirect:/product/" + productId;
        }
        
        // Thêm vào giỏ hàng thay vì tạo order ngay lập tức
        cartService.addToCart(cart, productId, quantity);
        
        // Cập nhật cart trong model để session được lưu
        model.addAttribute("cart", cart);
        
        redirectAttributes.addFlashAttribute("success", product.getName() + " added to cart!");
        return "redirect:/product/" + productId;
    }
}
