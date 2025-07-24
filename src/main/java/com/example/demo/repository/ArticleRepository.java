package com.example.demo.repository;

import com.example.demo.model.Article;
import com.example.demo.model.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByStatusOrderByPublishDateDesc(ArticleStatus status);

    List<Article> findAllByOrderByPublishDateDesc();

    @Query("SELECT a FROM Article a WHERE a.category = :category AND a.status = 'PUBLISHED' ORDER BY a.publishDate DESC")
    List<Article> findPublishedByCategory(@Param("category") String category);
}