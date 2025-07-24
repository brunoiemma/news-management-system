package com.example.demo.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WebScraper {
    
    public static Map<String, String> scrapeArticle(String url) throws IOException {
        Map<String, String> articleData = new HashMap<>();
        
        try {
            Document doc = Jsoup.connect(url)
                              .userAgent("Mozilla/5.0")
                              .timeout(10000)
                              .get();
            
            // Get title - try common meta tags and fallback to page title
            String title = doc.select("meta[property=og:title]").attr("content");
            if (title.isEmpty()) {
                title = doc.select("meta[name=twitter:title]").attr("content");
            }
            if (title.isEmpty()) {
                title = doc.title();
            }
            
            // Get content - try article content first, then main content
            String content = "";
            Elements articleContent = doc.select("article, [role=article]");
            if (!articleContent.isEmpty()) {
                content = articleContent.text();
            } else {
                content = doc.select("main, #content, .content").text();
            }
            if (content.isEmpty()) {
                content = doc.body().text();
            }
            
            // Get image - try common meta tags first
            String imageUrl = doc.select("meta[property=og:image]").attr("content");
            if (imageUrl.isEmpty()) {
                imageUrl = doc.select("meta[name=twitter:image]").attr("content");
            }
            if (imageUrl.isEmpty()) {
                Element firstImage = doc.select("article img, .content img").first();
                if (firstImage != null) {
                    imageUrl = firstImage.absUrl("src");
                }
            }
            
            // Try to determine category from URL or breadcrumbs
            String category = "";
            Elements breadcrumbs = doc.select(".breadcrumbs, nav[aria-label=breadcrumb]");
            if (!breadcrumbs.isEmpty()) {
                category = breadcrumbs.select("a").last().text();
            } else {
                // Try to extract from URL
                String[] pathParts = url.split("/");
                for (String part : pathParts) {
                    if (part.matches("(?i)(sports|tech|politics|business|entertainment|health|science|sports|deportes|tecnologia|politica|negocios)")) {
                        category = part.toLowerCase();
                        break;
                    }
                }
            }
            
            // Store the data
            articleData.put("title", title);
            articleData.put("content", content);
            articleData.put("imageUrl", imageUrl);
            articleData.put("category", category);
            articleData.put("sourceUrl", url);
            
            // Clean and truncate content if needed
            if (content.length() > 1900) {
                content = content.substring(0, 1900) + "...";
            }
            articleData.put("content", content);
            
        } catch (IOException e) {
            throw new IOException("Failed to scrape URL: " + url, e);
        }
        
        return articleData;
    }
}
