package com.truthlens.scraper;

import com.truthlens.dto.response.EvidenceDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsScraperService {

    public List<EvidenceDto> scrapeNews(String query) {
        List<EvidenceDto> evidenceList = new ArrayList<>();
        try {
            // Encode query for URL
            String encodedQuery = query.replace(" ", "%20");
            String rssUrl = "https://news.google.com/rss/search?q=" + encodedQuery + "&hl=en-IN&gl=IN&ceid=IN:en";

            // Parse the XML RSS feed using Jsoup
            Document doc = Jsoup.connect(rssUrl).parser(Parser.xmlParser()).get();
            Elements items = doc.select("item");

            // Extract top 3 news articles
            for (int i = 0; i < Math.min(5, items.size()); i++) {
                Element item = items.get(i);
                String title = item.select("title").text();
                String link = item.select("link").text();
                String sourceName = item.select("source").text();

                evidenceList.add(EvidenceDto.builder()
                        .sourceName("Scraped Web (" + sourceName + ")")
                        .title(title)
                        .url(link)
                        .stance("NEUTRAL") // Default until analyzed
                        .build());
            }
        } catch (Exception e) {
            System.err.println("Scraping failed: " + e.getMessage());
        }
        return evidenceList;
    }
}