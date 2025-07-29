package com.example.demo.service;

import com.example.demo.model.Article;
import com.example.demo.model.ArticleStatus;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.util.WebScraper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        article.setViewCount(0L);
        return article;
    }

    @Test
    void getTopArticles_ShouldReturnPublishedArticles() {
        // Arrange
        List<Article> expectedArticles = Arrays.asList(
            createSampleArticle(1L, "Article 1", "deportes", ArticleStatus.PUBLISHED),
            createSampleArticle(2L, "Article 2", "tecnologia", ArticleStatus.PUBLISHED)
        );
        
        when(articleRepository.findByStatusOrderByPublishDateDesc(ArticleStatus.PUBLISHED))
            .thenReturn(expectedArticles);

        // Act
        List<Article> result = newsService.getTopArticles();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Article 1", result.get(0).getTitle());
        assertEquals("Article 2", result.get(1).getTitle());
        verify(articleRepository).findByStatusOrderByPublishDateDesc(ArticleStatus.PUBLISHED);
    }

    @Test
    void getArticlesByCategory_ShouldReturnArticlesForCategory() {
        // Arrange
        String category = "deportes";
        List<Article> expectedArticles = Arrays.asList(
            createSampleArticle(1L, "Sports Article 1", category, ArticleStatus.PUBLISHED),
            createSampleArticle(2L, "Sports Article 2", category, ArticleStatus.PUBLISHED)
        );
        
        when(articleRepository.findPublishedByCategory(category)).thenReturn(expectedArticles);

        // Act
        List<Article> result = newsService.getArticlesByCategory(category);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(article -> article.getCategory().equals(category)));
        verify(articleRepository).findPublishedByCategory(category);
    }

    @Test
    void incrementViews_ShouldIncrementViewCount() {
        // Arrange
        Long articleId = 1L;
        Article article = createSampleArticle(articleId, "Test Article", "deportes", ArticleStatus.PUBLISHED);
        article.setViewCount(10L);
        
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        // Act
        newsService.incrementViews(articleId);

        // Assert
        assertEquals(11L, article.getViewCount());
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
        newsService.createArticle(article);

        // Assert
        assertEquals(ArticleStatus.DRAFT, article.getStatus());
        assertEquals(0L, article.getViewCount());
        assertNotNull(article.getPublishDate());
        verify(articleRepository).save(article);
    }

    @Test
    void updateArticle_ShouldUpdateAllFields() {
        // Arrange
        Long articleId = 1L;
        Article existingArticle = createSampleArticle(articleId, "Old Title", "deportes", ArticleStatus.DRAFT);
        Article updatedArticle = createSampleArticle(articleId, "New Title", "tecnologia", ArticleStatus.PUBLISHED);
        updatedArticle.setContent("Updated content");
        updatedArticle.setImageUrl("new-image.jpg");
        
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(existingArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(existingArticle);

        // Act
        newsService.updateArticle(articleId, updatedArticle);

        // Assert
        assertEquals("New Title", existingArticle.getTitle());
        assertEquals("Updated content", existingArticle.getContent());
        assertEquals("tecnologia", existingArticle.getCategory());
        assertEquals("new-image.jpg", existingArticle.getImageUrl());
        assertEquals(ArticleStatus.PUBLISHED, existingArticle.getStatus());
        verify(articleRepository).save(existingArticle);
    }

    @Test
    void scrapeAndCreateArticle_ShouldCreateArticleFromScrapedData() throws IOException {
        // Arrange
        String url = "https://example.com/article";
        Map<String, String> scrapedData = new HashMap<>();
        scrapedData.put("title", "Scraped Title");
        scrapedData.put("content", "Scraped Content");
        scrapedData.put("category", "tecnologia");
        scrapedData.put("imageUrl", "image.jpg");
        scrapedData.put("sourceUrl", url);
        
        try (var mockedStatic = mockStatic(WebScraper.class)) {
            mockedStatic.when(() -> WebScraper.scrapeArticle(url)).thenReturn(scrapedData);
            
            when(articleRepository.save(any(Article.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Article result = newsService.scrapeAndCreateArticle(url);

            // Assert
            assertEquals("Scraped Title", result.getTitle());
            assertEquals("Scraped Content", result.getContent());
            assertEquals("tecnologia", result.getCategory());
            assertEquals("image.jpg", result.getImageUrl());
            assertEquals(url, result.getSourceUrl());
            assertEquals("example.com", result.getSource());
            assertEquals(ArticleStatus.DRAFT, result.getStatus());
            assertEquals(0L, result.getViewCount());
            assertNotNull(result.getPublishDate());
            
            verify(articleRepository).save(any(Article.class));
        }
    }

    @Test
    void getAllArticles_WithStatus_ShouldFilterByStatus() {
        // Arrange
        List<Article> expectedArticles = Arrays.asList(
            createSampleArticle(1L, "Draft Article", "deportes", ArticleStatus.DRAFT),
            createSampleArticle(2L, "Another Draft", "tecnologia", ArticleStatus.DRAFT)
        );
        
        when(articleRepository.findByStatusOrderByPublishDateDesc(ArticleStatus.DRAFT))
            .thenReturn(expectedArticles);

        // Act
        List<Article> result = newsService.getAllArticles(ArticleStatus.DRAFT);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(article -> article.getStatus() == ArticleStatus.DRAFT));
        verify(articleRepository).findByStatusOrderByPublishDateDesc(ArticleStatus.DRAFT);
    }

    @Test
    void getAllArticles_WithoutStatus_ShouldReturnAllArticles() {
        // Arrange
        List<Article> expectedArticles = Arrays.asList(
            createSampleArticle(1L, "Published Article", "deportes", ArticleStatus.PUBLISHED),
            createSampleArticle(2L, "Draft Article", "tecnologia", ArticleStatus.DRAFT)
        );
        
        when(articleRepository.findAllByOrderByPublishDateDesc()).thenReturn(expectedArticles);

        // Act
        List<Article> result = newsService.getAllArticles(null);

        // Assert
        assertEquals(2, result.size());
        verify(articleRepository).findAllByOrderByPublishDateDesc();
    }
}
