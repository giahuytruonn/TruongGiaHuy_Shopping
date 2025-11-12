package iuh.controller;

import iuh.model.Customer;
import iuh.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String list(Model model) {
        List<Customer> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        return "customer/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer/add";
    }

    @PostMapping("/add")
    public String save(@ModelAttribute("customer") Customer customer,
                       RedirectAttributes redirectAttributes) {
        if (customer.getPassword() != null && !customer.getPassword().isBlank()) {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        }
        customerService.save(customer);
        redirectAttributes.addFlashAttribute("success", "Customer created");
        return "redirect:/admin/customers";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Customer customer = customerService.findById(id);
        if (customer == null) return "redirect:/admin/customers";
        model.addAttribute("customer", customer);
        return "customer/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Integer id,
                         @RequestParam(required = false) String password,
                         RedirectAttributes redirectAttributes) {
        Customer existing = customerService.findById(id);
        if (existing == null) {
            return "redirect:/admin/customers";
        }
        if (password != null && !password.trim().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(password.trim()));
            customerService.save(existing);
            redirectAttributes.addFlashAttribute("success", "Password updated successfully");
        }
        return "redirect:/admin/customers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        customerService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Deleted (if existed)");
        return "redirect:/admin/customers";
    }
}


