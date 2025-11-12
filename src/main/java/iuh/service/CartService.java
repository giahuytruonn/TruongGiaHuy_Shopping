package iuh.service;

import iuh.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    @Autowired
    private ProductService productService;

    public void addToCart(Map<Integer, Integer> cart, int productId, int quantity) {
        if (cart.containsKey(productId)) {
            cart.put(productId, cart.get(productId) + quantity);
        } else {
            cart.put(productId, quantity);
        }
    }

    public void removeFromCart(Map<Integer, Integer> cart, int productId) {
        cart.remove(productId);
    }

    public void updateCartItem(Map<Integer, Integer> cart, int productId, int quantity) {
        if (quantity <= 0) {
            cart.remove(productId);
        } else {
            cart.put(productId, quantity);
        }
    }

    public List<CartItem> getCartItems(Map<Integer, Integer> cart) {
        List<CartItem> cartItems = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Product product = productService.findById(entry.getKey());
            if (product != null) {
                CartItem cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setQuantity(entry.getValue());
                cartItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
                cartItems.add(cartItem);
            }
        }
        return cartItems;
    }

    public BigDecimal getCartTotal(Map<Integer, Integer> cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Product product = productService.findById(entry.getKey());
            if (product != null) {
                total = total.add(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
            }
        }
        return total;
    }

    public boolean isCartEmpty(Map<Integer, Integer> cart) {
        return cart == null || cart.isEmpty();
    }

    public void clearCart(Map<Integer, Integer> cart) {
        cart.clear();
    }

    public static class CartItem {
        private Product product;
        private Integer quantity;
        private BigDecimal totalPrice;

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
        }
    }
}
