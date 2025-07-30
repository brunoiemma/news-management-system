package com.example.demo.repository;

import com.example.demo.model.Article;
import com.example.demo.model.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // Count queries
    long countByStatus(ArticleStatus status);
    long countByStatusAndPublishDateAfter(ArticleStatus status, LocalDateTime date);
    long countByAuthor(String author);
    long countByAuthorAndStatus(String author, ArticleStatus status);
    long countByStatusAndCategory(ArticleStatus status, String category);

    // Find by status
    List<Article> findByStatus(ArticleStatus status);
    List<Article> findByStatusOrderByPublishDateDesc(ArticleStatus status);
    List<Article> findTop10ByStatusOrderByPublishDateDesc(ArticleStatus status);
    List<Article> findTop5ByStatusOrderByPublishDateDesc(ArticleStatus status);
    List<Article> findTop5ByStatusOrderByViewCountDesc(ArticleStatus status);

    // Find by author
    List<Article> findByAuthor(String author);
    List<Article> findByAuthorAndStatus(String author, ArticleStatus status);
    List<Article> findByAuthorOrderByPublishDateDesc(String author);

    // Category queries
    @Query("SELECT DISTINCT a.category FROM Article a")
    List<String> findDistinctCategories();

    List<Article> findByCategory(String category);
    List<Article> findByCategoryAndStatus(String category, ArticleStatus status);

    // Combined queries
    List<Article> findByStatusAndCategoryOrderByPublishDateDesc(ArticleStatus status, String category);
    List<Article> findByStatusAndAuthorOrderByPublishDateDesc(ArticleStatus status, String author);

    // View count queries
    List<Article> findTop10ByStatusOrderByViewCountDesc(ArticleStatus status);
    List<Article> findByViewCountGreaterThanAndStatusOrderByPublishDateDesc(int viewCount, ArticleStatus status);

    // Date-based queries
    List<Article> findByPublishDateBetweenAndStatus(LocalDateTime start, LocalDateTime end, ArticleStatus status);
    List<Article> findByPublishDateBeforeAndStatus(LocalDateTime date, ArticleStatus status);
    List<Article> findByPublishDateAfterAndStatus(LocalDateTime date, ArticleStatus status);

    // Search queries
    List<Article> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String titleKeyword, String contentKeyword);
    List<Article> findByTitleContainingIgnoreCaseAndStatus(String keyword, ArticleStatus status);
    
    // Status transition queries
    @Query("SELECT a FROM Article a WHERE a.status = ?1 AND a.lastModified < ?2")
    List<Article> findStaleArticles(ArticleStatus status, LocalDateTime threshold);
}
