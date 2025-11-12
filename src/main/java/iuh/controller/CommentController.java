package iuh.controller;

import iuh.model.Comment;
import iuh.model.Product;
import iuh.service.CommentService;
import iuh.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public String addComment(@RequestParam("productId") Integer productId, 
                           @RequestParam("text") String text,
                           RedirectAttributes redirectAttributes) {
        Product product = productService.findById(productId);

        Comment comment = new Comment();
        comment.setText(text.trim());
        comment.setProduct(product);
        
        commentService.save(comment);
        redirectAttributes.addFlashAttribute("success", "Comment added successfully");
        return "redirect:/product/" + productId;
    }

    @GetMapping("/edit/{id}")
    public String editComment(@PathVariable Integer id, @RequestParam("productId") Integer productId,
                            org.springframework.ui.Model model, RedirectAttributes redirectAttributes) {
        Comment comment = commentService.findById(id);
        
        model.addAttribute("comment", comment);
        model.addAttribute("productId", productId);
        return "comment/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateComment(@PathVariable Integer id, 
                              @RequestParam("text") String text,
                              @RequestParam("productId") Integer productId,
                              RedirectAttributes redirectAttributes) {
        Comment comment = commentService.findById(id);
        
        comment.setText(text.trim());
        commentService.save(comment);
        redirectAttributes.addFlashAttribute("success", "Comment updated successfully");
        return "redirect:/product/" + productId;
    }

    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable Integer id, 
                              @RequestParam("productId") Integer productId,
                              RedirectAttributes redirectAttributes) {
        if (commentService.existsById(id)) {
            commentService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Comment deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Comment not found");
        }
        return "redirect:/product/" + productId;
    }
}
