package iuh.controller;

import iuh.model.Product;
import iuh.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    @GetMapping
    public String showAllProducts(@RequestParam(required = false) String keyword, Model model) {
        List<Product> productlist;
        if (keyword != null && !keyword.trim().isEmpty()) {
            productlist = productService.search(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            productlist = productService.findAll();
        }
        model.addAttribute("products", productlist);
        return "product/list";
    }

    @GetMapping("/{id}")
    public String showProduct(@PathVariable int id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "product/productdetail";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editProduct(@PathVariable int id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/product";
        }
        model.addAttribute("product", product);
        return "product/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateProduct(@PathVariable int id, @Valid @ModelAttribute Product product, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        Product existingProduct = productService.findById(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", product);
            return "product/edit";
        }
        
        existingProduct.setName(product.getName().trim());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setInStock(product.isInStock());
        
        productService.save(existingProduct);
        redirectAttributes.addFlashAttribute("success", "Product updated successfully");
        return "redirect:/product/" + id;
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteProduct(@PathVariable int id, RedirectAttributes redirectAttributes) {
        if (productService.existsById(id)) {
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Product not found");
        }
        return "redirect:/product";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addProduct(Model model) {
        model.addAttribute("product", new Product());
        return "product/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveProduct(@Valid @ModelAttribute Product product, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", product);
            return "product/add";
        }
        product.setName(product.getName().trim());
        Product savedProduct = productService.save(product);
        redirectAttributes.addFlashAttribute("success", "Product added successfully");
        return "redirect:/product/" + savedProduct.getId();
    }
}
