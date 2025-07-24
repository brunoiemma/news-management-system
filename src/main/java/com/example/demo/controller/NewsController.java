package com.example.demo.controller;

import com.example.demo.model.Article;
import com.example.demo.service.NewsService;
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

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        List<Article> topArticles = newsService.getTopArticles();
        model.addAttribute("topArticles", topArticles);
        visitorStatsService.recordVisit(request, "home");
        return "index";
    }

    @GetMapping("/category/{category}")
    public String categoryNews(@PathVariable String category, Model model, HttpServletRequest request) {
        List<Article> articles = newsService.getArticlesByCategory(category);
        List<Article> topArticles = newsService.getTopArticles();
        model.addAttribute("articles", articles);
        model.addAttribute("topArticles", topArticles);
        model.addAttribute("category", category);
        visitorStatsService.recordVisit(request, "category/" + category);
        return "category";
    }

    @GetMapping("/news/{id}")
    public String newsDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        // Incrementar vistas
        newsService.incrementViews(id);
        visitorStatsService.recordVisit(request, "news/" + id);
        return "news-detail";
    }
}
