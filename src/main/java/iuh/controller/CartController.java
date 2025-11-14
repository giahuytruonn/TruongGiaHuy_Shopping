package iuh.controller;

import iuh.model.Customer;
import iuh.model.Order;
import iuh.model.OrderLine;
import iuh.model.OrderLineId;
import iuh.model.Product;
import iuh.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Controller
@RequestMapping("/cart")
@SessionAttributes("cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmailService emailService;

    @ModelAttribute("cart")
    public Map<Integer, Integer> initializeCart() {
        return new HashMap<>();
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public String viewCart(Model model, @SessionAttribute(value = "cart", required = false) Map<Integer, Integer> cart) {
        if (cart == null) {
            cart = new HashMap<>();
        }

        List<CartService.CartItem> cartItems = cartService.getCartItems(cart);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartService.getCartTotal(cart));
        model.addAttribute("cartSize", cart.size());

        return "cart/view";
    }

    @PostMapping("/add/{productId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public String addToCart(@PathVariable int productId, @RequestParam(defaultValue = "1") int quantity, @SessionAttribute(value = "cart", required = false) Map<Integer, Integer> cart, Model model, RedirectAttributes redirectAttributes) {

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

        cartService.addToCart(cart, productId, quantity);
        model.addAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("success", product.getName() + " added to cart!");

        return "redirect:/product/" + productId;
    }


    @PostMapping("/update/{productId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public String updateCartItem(@PathVariable int productId, @RequestParam int quantity, @SessionAttribute(value = "cart", required = false) Map<Integer, Integer> cart, Model model, RedirectAttributes redirectAttributes) {

        if (cart == null) {
            cart = new HashMap<>();
        }

        cartService.updateCartItem(cart, productId, quantity);
        model.addAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("success", "Cart updated successfully!");

        return "redirect:/cart";
    }


    @PostMapping("/remove/{productId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public String removeFromCart(@PathVariable int productId, @SessionAttribute(value = "cart", required = false) Map<Integer, Integer> cart, Model model, RedirectAttributes redirectAttributes) {

        if (cart == null) {
            cart = new HashMap<>();
        }

        Product product = productService.findById(productId);
        String productName = product != null ? product.getName() : "Product";

        cartService.removeFromCart(cart, productId);
        model.addAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("success", productName + " removed from cart!");

        return "redirect:/cart";
    }


    @PostMapping("/checkout")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @Transactional
    public String checkout(@SessionAttribute(value = "cart", required = false) Map<Integer, Integer> cart, Model model, RedirectAttributes redirectAttributes) throws Exception {

        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cart is empty!");
            return "redirect:/cart";
        }

        Customer currentCustomer = customerService.getCurrentCustomer();
        if (currentCustomer == null) {
            redirectAttributes.addFlashAttribute("error", "Customer not found. Please login again.");
            return "redirect:/login";
        }

        Order order = new Order();
        Calendar currentDate = Calendar.getInstance();
        order.setDate(currentDate);
        order.setCustomer(currentCustomer);
        
        Set<OrderLine> orderLines = new HashSet<>();

        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Product product = productService.findById(entry.getKey());
            if (product != null && product.isInStock()) {
                OrderLine orderLine = new OrderLine();
                orderLine.setId(new OrderLineId());
                orderLine.setOrder(order);
                orderLine.setProduct(product);
                orderLine.setAmount(entry.getValue());
                orderLine.setPurchasePrice(product.getPrice());
                orderLines.add(orderLine);
            }
        }

        order.setOrderLines(orderLines);


        String subject = "Confirming Order #" + currentCustomer.getName();

        StringBuilder content = new StringBuilder();

        content.append("<h2>Hi ").append(currentCustomer.getUsername()).append("!</h2>");
        content.append("<p>Cảm ơn bạn đã đặt hàng tại cửa hàng của chúng tôi!</p>");
        content.append("<p><strong>Thông tin đơn hàng:</strong></p>");

        double total = 0;

        content.append("<ul>");
        for (OrderLine line : orderLines) {
            double lineTotal = line.getAmount() * line.getPurchasePrice();
            total += lineTotal;

            content.append("<li>")
                    .append(line.getProduct().getName())
                    .append(" — SL: ").append(line.getAmount())
                    .append(" — Giá: ").append(line.getPurchasePrice())
                    .append(" — Thành tiền: ").append(lineTotal)
                    .append("</li>");
        }
        content.append("</ul>");

        content.append("<p><strong>Tổng cộng: </strong>").append(total).append(" VND</p>");

        content.append("""
        <p>Vui lòng thanh toán qua mã QR:</p>
        <img src="https://img.vietqr.io/image/MB-0862769500-compact.png"
             alt="QR Code" style="width:250px;" />
        """);

        content.append("<p>Chúng tôi sẽ liên hệ khi đơn hàng được xử lý.</p>");
        content.append("<p>Trân trọng,</p>");

        emailService.sendSimpleMail(subject, content.toString());

        orderService.save(order);

        cartService.clearCart(cart);
        model.addAttribute("cart", cart);

        return "redirect:/order";
    }

    @GetMapping("/payment-success")
    public String paymentSuccess(@RequestParam("orderId") Integer orderId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("success","Payment successful for order #" + orderId );
        return "redirect:/home";
    }

    @GetMapping("/payment-cancel")
    public String paymentCancel(@RequestParam("orderId") Integer orderId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error","Payment canceled for order #" + orderId);
        return "redirect:/order";
    }



    @PostMapping("/clear")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public String clearCart(@SessionAttribute(value = "cart", required = false) Map<Integer, Integer> cart, Model model, RedirectAttributes redirectAttributes) {

        if (cart == null) {
            cart = new HashMap<>();
        }

        cartService.clearCart(cart);
        model.addAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("success", "Cart cleared successfully!");

        return "redirect:/cart";
    }
}
