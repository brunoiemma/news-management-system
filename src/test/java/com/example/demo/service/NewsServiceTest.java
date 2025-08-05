package com.example.demo.service;

import com.example.demo.model.Article;
import com.example.demo.model.ArticleStatus;
import com.example.demo.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewsServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private NewsService newsService;

    private Article createSampleArticle(Long id, String title, String category, ArticleStatus status) {
        Article article = new Article();
        article.setId(id);
        article.setTitle(title);
        article.setContent("Sample content");
        article.setCategory(category);
        article.setStatus(status);
        article.setPublishDate(LocalDateTime.now());
        article.setViewCount(0);
        return article;
    }

    @Test
    void getFeaturedArticles_ShouldReturnPublishedArticles() {
        // Arrange
        List<Article> expectedArticles = Arrays.asList(
            createSampleArticle(1L, "Article 1", "deportes", ArticleStatus.PUBLISHED),
            createSampleArticle(2L, "Article 2", "tecnologia", ArticleStatus.PUBLISHED)
        );
        
        when(articleRepository.findTop5ByStatusOrderByViewCountDesc(ArticleStatus.PUBLISHED))
            .thenReturn(expectedArticles);

        // Act
        List<Article> result = newsService.getFeaturedArticles();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Article 1", result.get(0).getTitle());
        assertEquals("Article 2", result.get(1).getTitle());
        verify(articleRepository).findTop5ByStatusOrderByViewCountDesc(ArticleStatus.PUBLISHED);
    }

    @Test
    void findByCategoryAndStatus_ShouldReturnArticlesForCategory() {
        // Arrange
        String category = "deportes";
        List<Article> expectedArticles = Arrays.asList(
            createSampleArticle(1L, "Sports Article 1", category, ArticleStatus.PUBLISHED),
            createSampleArticle(2L, "Sports Article 2", category, ArticleStatus.PUBLISHED)
        );
        
        when(articleRepository.findByCategoryAndStatus(category, ArticleStatus.PUBLISHED)).thenReturn(expectedArticles);

        // Act
        List<Article> result = articleRepository.findByCategoryAndStatus(category, ArticleStatus.PUBLISHED);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(article -> article.getCategory().equals(category)));
        verify(articleRepository).findByCategoryAndStatus(category, ArticleStatus.PUBLISHED);
    }

    @Test
    void incrementViewCount_ShouldIncrementViewCount() {
        // Arrange
        Long articleId = 1L;
        Article article = createSampleArticle(articleId, "Test Article", "deportes", ArticleStatus.PUBLISHED);
        article.setViewCount(10);
        
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        // Act
        newsService.incrementViewCount(articleId);

        // Assert
        assertEquals(11, article.getViewCount());
        verify(articleRepository).findById(articleId);
        verify(articleRepository).save(article);
    }

    @Test
    void createArticle_ShouldSetInitialValues() {
        // Arrange
        Article article = new Article();
        article.setTitle("New Article");
        article.setContent("Content");
        article.setCategory("tecnologia");
        
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        // Act
        Article savedArticle = newsService.createArticle(article);

        // Assert
        assertEquals(ArticleStatus.DRAFT, savedArticle.getStatus());
        assertEquals(0, savedArticle.getViewCount());
        verify(articleRepository).save(article);
    }

    @Test
    void updateArticle_ShouldUpdateAllFields() {
        // Arrange
        Article updatedArticle = createSampleArticle(1L, "New Title", "tecnologia", ArticleStatus.PUBLISHED);
        updatedArticle.setContent("Updated content");
        updatedArticle.setImageUrl("new-image.jpg");
        
        when(articleRepository.save(any(Article.class))).thenReturn(updatedArticle);

        // Act
        Article result = newsService.updateArticle(updatedArticle);

        // Assert
        assertEquals("New Title", result.getTitle());
        assertEquals("Updated content", result.getContent());
        assertEquals("tecnologia", result.getCategory());
        assertEquals("new-image.jpg", result.getImageUrl());
        assertEquals(ArticleStatus.PUBLISHED, result.getStatus());
        verify(articleRepository).save(updatedArticle);
    }


    @Test
    void findByStatus_ShouldFilterByStatus() {
        // Arrange
        List<Article> expectedArticles = Arrays.asList(
            createSampleArticle(1L, "Draft Article", "deportes", ArticleStatus.DRAFT),
            createSampleArticle(2L, "Another Draft", "tecnologia", ArticleStatus.DRAFT)
        );
        
        when(articleRepository.findByStatus(ArticleStatus.DRAFT))
            .thenReturn(expectedArticles);

        // Act
        List<Article> result = articleRepository.findByStatus(ArticleStatus.DRAFT);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(article -> article.getStatus() == ArticleStatus.DRAFT));
        verify(articleRepository).findByStatus(ArticleStatus.DRAFT);
    }
}
