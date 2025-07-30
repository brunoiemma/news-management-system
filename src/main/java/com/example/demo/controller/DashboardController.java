package com.example.demo.controller;

import com.example.demo.service.NewsService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String getDashboard(Authentication authentication, Model model) {
        // Get user's role and redirect to appropriate dashboard
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PUBLISHER"))) {
            return "redirect:/publisher/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_REDACTOR"))) {
            return "redirect:/redactor/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUBSCRIBER"))) {
            return "redirect:/subscriber/dashboard";
        }
        return "redirect:/";
    }

    @GetMapping("/publisher/dashboard")
    public String getPublisherDashboard(Model model) {
        // Add statistics for publisher dashboard
        model.addAttribute("pendingReview", newsService.countPendingReview());
        model.addAttribute("publishedToday", newsService.countPublishedToday());
        model.addAttribute("totalPublished", newsService.countTotalPublished());
        model.addAttribute("pendingArticles", newsService.getPendingArticles());
        model.addAttribute("recentPublications", newsService.getRecentPublications());
        return "dashboard/publisher";
    }

    @GetMapping("/redactor/dashboard")
    public String getRedactorDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        // Add statistics for redactor dashboard
        model.addAttribute("totalArticles", newsService.countArticlesByAuthor(username));
        model.addAttribute("publishedArticles", newsService.countPublishedArticlesByAuthor(username));
        model.addAttribute("reviewArticles", newsService.countArticlesUnderReviewByAuthor(username));
        model.addAttribute("draftArticles", newsService.countDraftArticlesByAuthor(username));
        
        // Add article lists
        model.addAttribute("draftArticlesList", newsService.getDraftArticlesByAuthor(username));
        model.addAttribute("reviewArticlesList", newsService.getArticlesUnderReviewByAuthor(username));
        model.addAttribute("publishedArticlesList", newsService.getPublishedArticlesByAuthor(username));
        return "dashboard/redactor";
    }

    @GetMapping("/subscriber/dashboard")
    public String getSubscriberDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        // Add content for subscriber dashboard
        model.addAttribute("featuredArticles", newsService.getFeaturedArticles());
        model.addAttribute("recentlyRead", newsService.getRecentlyReadArticles(username));
        model.addAttribute("recommendedArticles", newsService.getRecommendedArticles(username));
        model.addAttribute("categories", newsService.getAllCategoriesWithCount());
        return "dashboard/subscriber";
    }
}
