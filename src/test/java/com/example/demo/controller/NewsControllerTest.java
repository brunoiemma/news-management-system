package com.example.demo.controller;

import com.example.demo.config.TestConfig;
import com.example.demo.config.TestSecurityConfig;
import com.example.demo.model.Article;
import com.example.demo.model.ArticleStatus;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.service.NewsService;
import com.example.demo.service.VisitorStatsService;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NewsController.class)
@Import(TestSecurityConfig.class)
public class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    @MockBean
    private ArticleRepository articleRepository;

    @MockBean
    private VisitorStatsService visitorStatsService;

    private Article createSampleArticle(Long id, String title, String category) {
        Article article = new Article();
        article.setId(id);
        article.setTitle(title);
        article.setContent("Sample content for " + title);
        article.setCategory(category);
        article.setStatus(ArticleStatus.PUBLISHED);
        article.setPublishDate(LocalDateTime.now());
        article.setViewCount(0);
        return article;
    }

    @Test
    @WithMockUser
    void home_ShouldDisplayFeaturedArticles() throws Exception {
        // Arrange
        List<Article> featuredArticles = Arrays.asList(
            createSampleArticle(1L, "Featured Article 1", "deportes"),
            createSampleArticle(2L, "Featured Article 2", "tecnologia")
        );
        
        when(newsService.getFeaturedArticles()).thenReturn(featuredArticles);

        // Act & Assert
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attribute("topArticles", featuredArticles));

        verify(visitorStatsService).recordVisit(any(), eq("home"));
    }

    @Test
    @WithMockUser
    void categoryNews_ShouldDisplayCategoryArticles() throws Exception {
        // Arrange
        String category = "deportes";
        List<Article> categoryArticles = Arrays.asList(
            createSampleArticle(1L, "Sports Article 1", category),
            createSampleArticle(2L, "Sports Article 2", category)
        );
        List<Article> topArticles = Arrays.asList(
            createSampleArticle(3L, "Top Article", "tecnologia")
        );
        
        when(articleRepository.findByCategoryAndStatus(category, ArticleStatus.PUBLISHED)).thenReturn(categoryArticles);
        when(newsService.getFeaturedArticles()).thenReturn(topArticles);

        // Act & Assert
        mockMvc.perform(get("/category/{category}", category))
            .andExpect(status().isOk())
            .andExpect(view().name("category"))
            .andExpect(model().attribute("articles", categoryArticles))
            .andExpect(model().attribute("topArticles", topArticles))
            .andExpect(model().attribute("category", category));

        verify(visitorStatsService).recordVisit(any(), eq("category/" + category));
    }

    @Test
    @WithMockUser
    void newsDetail_ShouldIncrementViewsAndRecordVisit() throws Exception {
        // Arrange
        Long articleId = 1L;
        Article article = createSampleArticle(articleId, "Test Article", "deportes");
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        // Act & Assert
        mockMvc.perform(get("/news/{id}", articleId))
            .andExpect(status().isOk())
            .andExpect(view().name("news-detail"))
            .andExpect(model().attribute("article", article));

        verify(newsService).incrementViewCount(articleId);
        verify(visitorStatsService).recordVisit(any(), eq("news/" + articleId));
    }

    @Test
    void home_WithoutAuth_ShouldAllowAccess() throws Exception {
        // Arrange
        List<Article> topArticles = Arrays.asList(
            createSampleArticle(1L, "Public Article", "deportes")
        );
        
        when(newsService.getFeaturedArticles()).thenReturn(topArticles);

        // Act & Assert
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attribute("topArticles", topArticles));
    }

    @Test
    void categoryNews_WithoutAuth_ShouldAllowAccess() throws Exception {
        // Arrange
        String category = "deportes";
        List<Article> articles = Arrays.asList(
            createSampleArticle(1L, "Public Sports Article", category)
        );
        
        when(articleRepository.findByCategoryAndStatus(category, ArticleStatus.PUBLISHED)).thenReturn(articles);
        when(newsService.getFeaturedArticles()).thenReturn(articles);

        // Act & Assert
        mockMvc.perform(get("/category/{category}", category))
            .andExpect(status().isOk())
            .andExpect(view().name("category"))
            .andExpect(model().attribute("articles", articles))
            .andExpect(model().attribute("topArticles", articles))
            .andExpect(model().attribute("category", category));
    }

    @Test
    void newsDetail_WithoutAuth_ShouldAllowAccess() throws Exception {
        // Arrange
        Long articleId = 1L;
        Article article = createSampleArticle(articleId, "Public Article", "deportes");
        
        // Explicitly configure all required mocks
        when(articleRepository.findById(eq(articleId))).thenReturn(Optional.of(article));
        doNothing().when(newsService).incrementViewCount(eq(articleId));
        doNothing().when(visitorStatsService).recordVisit(any(), anyString());

        // Act & Assert
        mockMvc.perform(get("/news/{id}", articleId))
               .andExpect(status().isOk())
               .andExpect(view().name("news-detail"))
               .andExpect(model().attribute("article", article))
               .andExpect(model().attributeExists("article"))
               .andExpect(model().attribute("article", hasProperty("title", equalTo("Public Article"))))
               .andExpect(model().attribute("article", hasProperty("category", equalTo("deportes"))));

        // Verify all service interactions
        verify(articleRepository, times(1)).findById(articleId);
        verify(newsService, times(1)).incrementViewCount(articleId);
        verify(visitorStatsService, times(1)).recordVisit(any(), eq("news/" + articleId));
    }
}
