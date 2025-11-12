package iuh.service;

import iuh.model.Comment;
import iuh.model.Product;
import iuh.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository repository;

    public List<Comment> findAll() {
        return repository.findAll();
    }

    public Comment findById(Integer id) {
        Optional<Comment> comment = repository.findById(id);
        return comment.orElse(null);
    }

    public Comment save(Comment comment) {
        return repository.save(comment);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }

    public List<Comment> findByProduct(Product product) {
        return repository.findByProduct(product);
    }
}
