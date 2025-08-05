package com.example.demo.service;

import com.example.demo.model.Article;
import com.example.demo.model.ArticleStatus;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.util.WebScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class NewsService {

    @Autowired
    private ArticleRepository articleRepository;

    // Count methods
    public long countAllArticles() {
        return articleRepository.count();
    }

    public long countPendingArticles() {
        return articleRepository.countByStatus(ArticleStatus.IN_REVIEW);
    }

    public long countPendingReview() {
        return articleRepository.countByStatus(ArticleStatus.IN_REVIEW);
    }

    public long countPublishedToday() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return articleRepository.countByStatusAndPublishDateAfter(ArticleStatus.PUBLISHED, startOfDay);
    }

    public long countTotalPublished() {
        return articleRepository.countByStatus(ArticleStatus.PUBLISHED);
    }

    public long countArticlesByAuthor(String username) {
        return articleRepository.countByAuthor(username);
    }

    public long countPublishedArticlesByAuthor(String username) {
        return articleRepository.countByAuthorAndStatus(username, ArticleStatus.PUBLISHED);
    }

    public long countArticlesUnderReviewByAuthor(String username) {
        return articleRepository.countByAuthorAndStatus(username, ArticleStatus.IN_REVIEW);
    }

    public long countDraftArticlesByAuthor(String username) {
        return articleRepository.countByAuthorAndStatus(username, ArticleStatus.DRAFT);
    }

    // Article retrieval methods
    public List<Article> getPendingArticles() {
        return articleRepository.findByStatus(ArticleStatus.IN_REVIEW);
    }

    public List<Article> getRecentPublications() {
        return articleRepository.findTop10ByStatusOrderByPublishDateDesc(ArticleStatus.PUBLISHED);
    }

    public List<Article> getDraftArticlesByAuthor(String username) {
        return articleRepository.findByAuthorAndStatus(username, ArticleStatus.DRAFT);
    }

    public List<Article> getArticlesUnderReviewByAuthor(String username) {
        return articleRepository.findByAuthorAndStatus(username, ArticleStatus.IN_REVIEW);
    }

    public List<Article> getPublishedArticlesByAuthor(String username) {
        return articleRepository.findByAuthorAndStatus(username, ArticleStatus.PUBLISHED);
    }

    public List<Article> getFeaturedArticles() {
        return articleRepository.findTop5ByStatusOrderByViewCountDesc(ArticleStatus.PUBLISHED);
    }

    public List<Article> getRecentlyReadArticles(String username) {
        // This would typically involve a separate table tracking user reading history
        // For now, return recent published articles as a placeholder
        return articleRepository.findTop5ByStatusOrderByPublishDateDesc(ArticleStatus.PUBLISHED);
    }

    public List<Article> getRecommendedArticles(String username) {
        // This would typically involve a recommendation algorithm
        // For now, return popular articles as a placeholder
        return articleRepository.findTop5ByStatusOrderByViewCountDesc(ArticleStatus.PUBLISHED);
    }

    // Category methods
    public Map<String, Long> getAllCategoriesWithCount() {
        List<String> categories = articleRepository.findDistinctCategories();
        Map<String, Long> categoryCount = new HashMap<>();
        for (String category : categories) {
            long count = articleRepository.countByStatusAndCategory(ArticleStatus.PUBLISHED, category);
            categoryCount.put(category, count);
        }
        return categoryCount;
    }

    // Article management methods
    public Article createArticle(Article article) {
        if (article.getSourceUrl() != null && !article.getSourceUrl().isEmpty()) {
            try {
                Map<String, String> scrapedData = WebScraper.scrapeArticle(article.getSourceUrl());
                if (scrapedData.get("title") == null || scrapedData.get("title").isEmpty()) {
                    throw new RuntimeException("No se pudo extraer el título del artículo");
                }
                if (scrapedData.get("content") == null || scrapedData.get("content").isEmpty()) {
                    throw new RuntimeException("No se pudo extraer el contenido del artículo");
                }
                article.setTitle(scrapedData.get("title"));
                article.setContent(scrapedData.get("content"));
                article.setImageUrl(scrapedData.get("imageUrl"));
                article.setCategory(scrapedData.get("category"));
                article.setSource(new java.net.URL(article.getSourceUrl()).getHost());
            } catch (IOException e) {
                throw new RuntimeException("Error al acceder a la URL: " + e.getMessage(), e);
            } catch (Exception e) {
                throw new RuntimeException("Error al procesar el artículo: " + e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("La URL del artículo es requerida");
        }
        article.setStatus(ArticleStatus.DRAFT);
        return articleRepository.save(article);
    }

    public Article updateArticle(Article article) {
        return articleRepository.save(article);
    }

    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    public void publishArticle(Long id) {
        articleRepository.findById(id).ifPresent(article -> {
            article.setStatus(ArticleStatus.PUBLISHED);
            article.setPublishDate(LocalDateTime.now());
            articleRepository.save(article);
        });
    }

    public void submitForReview(Long id) {
        articleRepository.findById(id).ifPresent(article -> {
            article.setStatus(ArticleStatus.IN_REVIEW);
            articleRepository.save(article);
        });
    }

    public void rejectArticle(Long id) {
        articleRepository.findById(id).ifPresent(article -> {
            article.setStatus(ArticleStatus.DRAFT);
            articleRepository.save(article);
        });
    }

    public void incrementViewCount(Long id) {
        articleRepository.findById(id).ifPresent(article -> {
            article.setViewCount(article.getViewCount() + 1);
            articleRepository.save(article);
        });
    }
}
