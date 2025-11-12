package iuh.controller;

import iuh.model.Customer;
import iuh.model.Order;
import iuh.model.OrderLine;
import iuh.model.OrderLineId;
import iuh.model.Product;
import iuh.service.CartService;
import iuh.service.CustomerService;
import iuh.service.OrderLineService;
import iuh.service.OrderService;
import iuh.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

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
    private OrderLineService orderLineService;

    @Autowired
    private CustomerService customerService;

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
    public String checkout(@SessionAttribute(value = "cart", required = false) Map<Integer, Integer> cart, Model model, RedirectAttributes redirectAttributes) {

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
        order = orderService.save(order);

        int totalItems = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Product product = productService.findById(entry.getKey());
            if (product != null && product.isInStock()) {
                OrderLineId orderLineId = new OrderLineId();
                orderLineId.setOrderId(order.getId());
                orderLineId.setProductId(entry.getKey());

                OrderLine orderLine = new OrderLine();
                orderLine.setId(orderLineId);
                orderLine.setOrder(order);
                orderLine.setProduct(product);
                orderLine.setAmount(entry.getValue());
                orderLine.setPurchasePrice(product.getPrice());

                orderLineService.save(orderLine);

                totalItems += entry.getValue();
                totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
            }
        }

        cartService.clearCart(cart);
        model.addAttribute("cart", cart);

        String successMessage = String.format("Order placed successfully! Order #%d - %d items - Total: $%.2f", order.getId(), totalItems, totalAmount);
        redirectAttributes.addFlashAttribute("success", successMessage);

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
