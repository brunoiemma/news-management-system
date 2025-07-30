package com.example.demo.controller;

import com.example.demo.model.Article;
import com.example.demo.model.ArticleStatus;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.service.NewsService;
import com.example.demo.service.ResourceNotFoundException;
import com.example.demo.service.UserService;
import com.example.demo.service.VisitorStatsService;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private VisitorStatsService visitorStatsService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserService userService;

    @GetMapping({"", "/", "/dashboard"})
    public String adminDashboard(Model model) {
        // Add visitor statistics
        model.addAttribute("totalVisits", visitorStatsService.getTotalVisits());
        
        // Add user statistics
        model.addAttribute("userCount", userService.countAllUsers());
        model.addAttribute("activeUsers", userService.countActiveUsers());
        
        // Add article statistics
        model.addAttribute("totalArticles", newsService.countAllArticles());
        model.addAttribute("pendingArticles", newsService.countPendingArticles());
        
        return "admin/dashboard";
    }

    @GetMapping("/fetch-news")
    public String fetchNewsForm() {
        return "admin/fetch-news";
    }

    @PostMapping("/fetch-news")
    public String fetchNews(@RequestParam String url, Model model) {
        try {
            Article article = new Article();
            article.setSourceUrl(url);
            newsService.createArticle(article);
            return "redirect:/admin/articles";
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching article: " + e.getMessage());
            return "admin/fetch-news";
        }
    }

    @GetMapping("/articles")
    public String listArticles(Model model, @RequestParam(required = false) String status) {
        ArticleStatus statusEnum = status != null && !status.isEmpty() ? 
            ArticleStatus.valueOf(status.toUpperCase()) : null;
        List<Article> articles = statusEnum != null ? 
            articleRepository.findByStatus(statusEnum) : 
            articleRepository.findAll();
        model.addAttribute("articles", articles);
        model.addAttribute("statuses", ArticleStatus.values());
        model.addAttribute("selectedStatus", status);
        return "admin/articles";
    }

    @GetMapping({"/articles/create", "/articles/new"})
    public String createArticleForm(Model model) {
        Article article = new Article();
        article.setStatus(ArticleStatus.DRAFT);  // Explicitly set DRAFT status for new articles
        model.addAttribute("article", article);
        return "admin/create-article";
    }

    @PostMapping("/articles/create")
    public String createArticle(@ModelAttribute Article article) {
        newsService.createArticle(article);
        return "redirect:/admin/articles";
    }

    @GetMapping("/articles/edit/{id}")
    public String editArticleForm(@PathVariable Long id, Model model) {
        Optional<Article> article = articleRepository.findById(id);
        if (article.isEmpty()) {
            return "redirect:/admin/articles";
        }
        model.addAttribute("article", article.get());
        return "admin/edit-article";
    }

    @PostMapping("/articles/edit/{id}")
    public String updateArticle(@PathVariable Long id, @ModelAttribute Article article) {
        article.setId(id); // Ensure the ID is set
        newsService.updateArticle(article);
        return "redirect:/admin/articles";
    }
}
