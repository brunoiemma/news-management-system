package com.example.demo.service;

import com.example.demo.model.Article;
import com.example.demo.model.ArticleStatus;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.util.WebScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NewsService {

    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> getTopArticles() {
        return articleRepository.findByStatusOrderByPublishDateDesc(ArticleStatus.PUBLISHED);
    }

    public List<Article> getAllArticles(ArticleStatus status) {
        if (status != null) {
            return articleRepository.findByStatusOrderByPublishDateDesc(status);
        }
        return articleRepository.findAllByOrderByPublishDateDesc();
    }

    public List<Article> getArticlesByCategory(String category) {
        return articleRepository.findPublishedByCategory(category);
    }

    public long getTotalArticles() {
        return articleRepository.count();
    }

    public Article scrapeAndCreateArticle(String url) throws IOException {
        Map<String, String> scrapedData = WebScraper.scrapeArticle(url);
        
        Article article = new Article();
        article.setTitle(scrapedData.get("title"));
        article.setContent(scrapedData.get("content"));
        article.setCategory(scrapedData.get("category"));
        article.setImageUrl(scrapedData.get("imageUrl"));
        article.setSourceUrl(scrapedData.get("sourceUrl"));
        article.setSource(getDomainFromUrl(url));
        article.setPublishDate(LocalDateTime.now());
        article.setViewCount(0L);
        article.setStatus(ArticleStatus.DRAFT); // All new articles start as drafts
        
        return articleRepository.save(article);
    }
    
    private String getDomainFromUrl(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (Exception e) {
            return url;
        }
    }

    public void incrementViews(Long articleId) {
        articleRepository.findById(articleId).ifPresent(article -> {
            article.setViewCount(article.getViewCount() + 1);
            articleRepository.save(article);
        });
    }

    public Article getArticleById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    public void updateArticle(Long id, Article updatedArticle) {
        articleRepository.findById(id).ifPresent(article -> {
            article.setTitle(updatedArticle.getTitle());
            article.setContent(updatedArticle.getContent());
            article.setCategory(updatedArticle.getCategory());
            article.setSource(updatedArticle.getSource());
            article.setSourceUrl(updatedArticle.getSourceUrl());
            article.setImageUrl(updatedArticle.getImageUrl());
            article.setStatus(updatedArticle.getStatus());
            articleRepository.save(article);
        });
    }

    public void createArticle(Article article) {
        article.setPublishDate(LocalDateTime.now());
        article.setViewCount(0L);
        article.setStatus(ArticleStatus.DRAFT);  // Always set DRAFT status for new articles
        articleRepository.save(article);
    }

    private String[] getTitlesForCategory(String category) {
        switch (category.toLowerCase()) {
            case "deportes":
                return new String[] {
                    "Gran victoria en el clásico del fútbol local",
                    "Nuevo récord mundial en atletismo",
                    "El equipo nacional se prepara para el mundial",
                    "Sorprendente resultado en el torneo de tenis",
                    "Histórica actuación en los Juegos Olímpicos"
                };
            case "tecnologia":
                return new String[] {
                    "Nueva revolución en inteligencia artificial",
                    "El futuro de los smartphones",
                    "Avances en computación cuántica",
                    "La tecnología 6G ya está en desarrollo",
                    "Innovaciones en energía renovable"
                };
            case "politica":
                return new String[] {
                    "Importantes acuerdos en la cumbre internacional",
                    "Nuevas políticas económicas anunciadas",
                    "Resultados de las últimas elecciones",
                    "Debates sobre reformas legislativas",
                    "Cambios en el gabinete de gobierno"
                };
            default:
                return new String[] {
                    "Últimas noticias del momento",
                    "Desarrollos importantes en " + category,
                    "Novedades en el sector de " + category,
                    "Lo más destacado en " + category,
                    "Actualidad en " + category
                };
        }
    }

    private String[] getContentsForCategory(String category) {
        switch (category.toLowerCase()) {
            case "deportes":
                return new String[] {
                    "En un partido lleno de emoción y jugadas espectaculares, el equipo local logró una victoria histórica que quedará en la memoria de todos los aficionados.",
                    "Los atletas demostraron su excelente preparación y dedicación, superando todas las expectativas y estableciendo nuevas marcas.",
                    "La selección nacional intensifica sus entrenamientos de cara al próximo mundial, con nuevas estrategias y jugadores prometedores.",
                    "El torneo fue testigo de uno de los encuentros más emocionantes de la temporada, con un desenlace que nadie esperaba.",
                    "La delegación olímpica consiguió resultados históricos, superando el récord de medallas en una sola edición de los juegos."
                };
            case "tecnologia":
                return new String[] {
                    "Los últimos avances en IA están revolucionando la forma en que interactuamos con la tecnología, abriendo nuevas posibilidades en diversos campos.",
                    "La industria móvil se prepara para una nueva era con innovaciones que cambiarán la forma en que usamos nuestros dispositivos.",
                    "Científicos logran importantes avances en computación cuántica, acercándonos a una nueva era en el procesamiento de datos.",
                    "Las investigaciones en conectividad móvil continúan avanzando, prometiendo velocidades nunca antes vistas.",
                    "Nuevas tecnologías en energía renovable prometen transformar el sector energético en los próximos años."
                };
            case "politica":
                return new String[] {
                    "Los líderes mundiales alcanzaron importantes acuerdos que marcarán el futuro de las relaciones internacionales.",
                    "El gobierno anunció nuevas medidas económicas que buscan impulsar el crecimiento y desarrollo del país.",
                    "Los resultados electorales marcan un punto de inflexión en la política nacional, con importantes cambios en el panorama político.",
                    "El parlamento debate importantes reformas que podrían transformar el marco legislativo actual.",
                    "Significativos cambios en el gabinete gubernamental prometen una nueva dirección en las políticas públicas."
                };
            default:
                return new String[] {
                    "Importantes desarrollos en el sector marcan una nueva etapa en " + category + ".",
                    "Los expertos analizan las últimas tendencias y cambios en el ámbito de " + category + ".",
                    "Nuevas perspectivas y desafíos emergen en el campo de " + category + ".",
                    "El sector de " + category + " experimenta transformaciones significativas.",
                    "Análisis detallado de los últimos acontecimientos en " + category + "."
                };
        }
    }
}
