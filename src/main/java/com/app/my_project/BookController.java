package com.app.my_project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.models.BookModel;

// This class is a REST controller for managing books
@RestController
@RequestMapping("/api/books") // Base URL for all endpoints in this controller
public class BookController {
    // In-memory list of books to simulate a database
    private final List<BookModel> books = new ArrayList<>(
            Arrays.asList(
                    new BookModel("1", "Book 1", 100), // Predefined book 1
                    new BookModel("2", "Book 2", 200), // Predefined book 2
                    new BookModel("3", "Book 3", 300))); // Predefined book 3

    // Endpoint to retrieve all books
    @GetMapping
    public List<BookModel> getAllBooks() {
        return books;
    }

    // Endpoint to retrieve a book by its ID
    @GetMapping("/{id}")
    public BookModel getBookById(@PathVariable String id) {
        return books.stream()
                .filter(book -> book.getId().equals(id)) // Find the book with the matching ID
                .findFirst()
                .orElse(null); // Return null if no book is found
    }

    // Endpoint to add a new book
    @PostMapping
    public BookModel addBook(@RequestBody BookModel book) {
        books.add(book); // Add the new book to the list
        return book; // Return the added book
    }

    // Endpoint to update an existing book by its ID
    @PutMapping("/{id}")
    public BookModel updateBook(@PathVariable String id, @RequestBody BookModel updateBook) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId().equals(id)) { // Find the book with the matching ID
                books.set(i, updateBook); // Update the book details
                return updateBook; // Return the updated book
            }
        }
        return null; // Return null if no book is found
    }

    // Endpoint to delete a book by its ID
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable String id) {
        // ใช้ Lambda Function กับ removeIf เพื่อลบหนังสือที่มี ID ตรงกับพารามิเตอร์
        // removeIf จะวนลูปผ่านรายการ books และลบรายการที่ตรงกับเงื่อนไขใน Lambda
        // Lambda Function: book -> book.getId().equals(id)
        // - book คือแต่ละรายการใน books
        // - book.getId() เรียก ID ของหนังสือเล่มนั้น
        // - .equals(id) เปรียบเทียบว่า ID ของหนังสือตรงกับ ID ที่ส่งมาหรือไม่
        // ถ้าเงื่อนไขเป็นจริง (true) หนังสือเล่มนั้นจะถูกลบออกจากรายการ books
        books.removeIf(book -> book.getId().equals(id)); // Remove the book with the matching ID
    }
}