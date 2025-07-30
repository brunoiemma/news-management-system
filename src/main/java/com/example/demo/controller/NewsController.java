package com.example.demo.controller;

import com.example.demo.model.Article;
import com.example.demo.model.ArticleStatus;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.service.NewsService;
import com.example.demo.service.ResourceNotFoundException;
import com.example.demo.service.VisitorStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private VisitorStatsService visitorStatsService;

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        List<Article> topArticles = newsService.getFeaturedArticles();
        model.addAttribute("topArticles", topArticles);
        visitorStatsService.recordVisit(request, "home");
        return "index";
    }

    @GetMapping("/category/{category}")
    public String categoryNews(@PathVariable String category, Model model, HttpServletRequest request) {
        List<Article> articles = articleRepository.findByCategoryAndStatus(category, ArticleStatus.PUBLISHED);
        List<Article> topArticles = newsService.getFeaturedArticles();
        model.addAttribute("articles", articles);
        model.addAttribute("topArticles", topArticles);
        model.addAttribute("category", category);
        visitorStatsService.recordVisit(request, "category/" + category);
        return "category";
    }

    @GetMapping("/news/{id}")
    public String newsDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        model.addAttribute("article", article);
        newsService.incrementViewCount(id);
        visitorStatsService.recordVisit(request, "news/" + id);
        return "news-detail";
    }
}
