package iuh.service;

import iuh.model.Product;
import iuh.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public List<Product> findAll() {
        return  repository.findAll();
    }

    public Product findById(Integer id) {
        Optional<Product> product = repository.findById(id);
        return product.orElse(null);
    }

    public Product save(Product product) {
        return repository.save(product);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    public List<Product> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return repository.search(keyword.trim());
    }

}
