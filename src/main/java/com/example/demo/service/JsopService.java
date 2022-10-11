package com.example.demo.service;

import com.example.demo.repository.AdData;
import com.example.demo.repository.AdsRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class JsopService {

    private static String PRICE_PATTERN = "[^0-9]";

    private static String BASE_URL = "https://realt.by/rent/flat-for-long/?search=";
    private static String QUERY1 = "eJxdzcEOwiAMBuCngZsJqEvmwYOT92gWwgxxA1IgU59eCgkxXtry0T9Nfnfg5s2wYVrtszRVJlHalSnBLneq01irYkqyUdb5xtH7LZbd34j8V0l65EtGZ1NGuuLMh%2FDAY%2FJo3lVatsPSpL0jD2g1JR9Vz0J0WasMRV6gfXYJgtV0J%2FZ1ySGalEMBNBqCQQhz%2BzqJLwyvS9s%3D";
    private static String QUERY2 = "eJxdjcEKAyEMRL9Gz7GtsD3soVv%2FQ6yEInU16C79%2FSYWytLLzPAyQ7b6Lr6EFZVdcnqxOU7ANisH6noXXaahTjmjJjPyTbda187d48T8UyP0pANRTqFElNOjHxdWU0tR3j9lM18AfiQPYpn4jttOjBpGT9g8hW%2F9DB%2F6fDX%2B";
    private static String QUERY3 = "eJxdykEKgCAQheHT6Foz27WJ7jGEDCFUDqNCx28sCGr1w%2Fcep7Rn5SdUfpYYyWg1f9U27f7aNXWaOAaRaW089sa8st0yiJwQUj0KUAylMub3bjVkLJUEGAMQMtDyTM5clNguQw%3D%3D&view=1";

    private final AdsRepository adsRepository;

    public List<String> start() {

        List<String> queries = Arrays.asList(QUERY1, QUERY2, QUERY3);

        List<String> results = new ArrayList<>();

        queries.forEach(query -> {

            int limit = 30;
            boolean findLimit = false;

            for (int i = 0; i < limit; i++) {
                try {
                    log.info("i: {}", i);
                    String url = BASE_URL + query + "&page=" + i;

                    log.info("url: {}", url);

                    Document doc = Jsoup.connect(url)
                            .userAgent("Chrome/4.0.249.0 Safari/532.5")
                            .referrer("http://www.google.com")
                            .get();

                    Elements listNews = doc.getElementsByClass("col-12");


                    if(!findLimit) {
                        limit = listNews.get(5).getElementsByClass("col-12").get(0).getElementsByClass("paging-list").get(0).getAllElements().size() - 1;
                        findLimit = true;
                    }

                    for (int j = 0; j < 37; j++) {
                        try {
                            log.info("j: {}", j);

                            String price = listNews.get(4)
                                    .getElementsByClass("listing view-format").get(0)
                                    .getElementsByClass("listing-item").get(j)
                                    .getElementsByClass("col-auto text-truncate")
                                    .get(0).text().replaceAll(PRICE_PATTERN, "");

                            Boolean fromAgent = null;
                            try {
                                fromAgent = !listNews.get(4)
                                        .getElementsByClass("listing view-format").get(0)
                                        .getElementsByClass("listing-item").get(j)
                                        .getElementsByClass("teaser-tile teaser-tile-right").get(0)
                                        .getElementsByClass("contacts").get(0)
                                        .getElementsByClass("logo").isEmpty();
                            } catch (Exception ex) {
                                log.warn("не удалось определить агент или собственник");
                            }

                            String address = listNews.get(4)
                                    .getElementsByClass("listing view-format").get(0)
                                    .getElementsByClass("listing-item").get(j)
                                    .getElementsByClass("location color-graydark").get(0)
//                                .wholeText().replaceAll("[^A-Za-zА-Яа-я0-9]", "");
                                    .wholeText().replace("\n", "").replace("\r", "").trim();

                            String link = listNews.get(4)
                                    .getElementsByClass("listing view-format").get(0)
                                    .getElementsByClass("listing-item").get(j)
                                    .getElementsByClass("teaser-title").get(0)
                                    .attr("href");

                            AdDataDto adDataDto = AdDataDto.builder()
                                    .price(price)
                                    .address(address)
                                    .link(link)
                                    .fromAgent(fromAgent)
                                    .build();

                            boolean result = save(adDataDto);
                            if(result) {
                                results.add(mapToString(adDataDto.getPrice(), adDataDto.getLink(), adDataDto.getAddress(), adDataDto.getFromAgent()));
                            }
                        } catch (Exception ex) {
                            log.info(ex.getLocalizedMessage(), ex);
                        }
                    }

                    Thread.sleep(4000);
                } catch (Exception ex){
                    log.info(ex.getLocalizedMessage(), ex);
                    break;
                }
            }

        });


        return results;
    }

    @Transactional
    public boolean save(AdDataDto adDataDto){
        Optional<AdData> adDataByLink = adsRepository.findAdDataByLink(adDataDto.getLink());
        log.info("try saving ads... data: {}", adDataDto);
        if(adDataByLink.isPresent()) {

            if(!adDataByLink.get().getAddress().equals(adDataDto.getAddress()) || !adDataByLink.get().getPrice().equals(adDataDto.getPrice())) {
                log.info("ad exists, but data has changed, update...");
                AdData adData = adDataByLink.get();
                adData.setPrice(adDataDto.getPrice());
                adData.setAddress(adDataDto.getAddress());
                adData.setUpdate(LocalDateTime.now());
                adData.setUpdate(LocalDateTime.now());
                adData.setFromAgent(adDataDto.getFromAgent());
                adsRepository.save(adData);
            } else {
                log.info("ad is actual, skip...");
                return false;
            }

        } else {
            log.info("ad not exists, create...");
            LocalDateTime now = LocalDateTime.now();
            AdData adData = new AdData();
            adData.setCreate(now);
            adData.setUpdate(now);
            adData.setLink(adDataDto.getLink());
            adData.setAddress(adDataDto.getAddress());
            adData.setPrice(adDataDto.getPrice());
            adData.setFromAgent(adDataDto.getFromAgent());
            adsRepository.save(adData);
        }
        return true;
    }

    @Transactional(readOnly = true)
    public List<String> getAllAdsByData(LocalDateTime data){
        return adsRepository.findAdDataByUpdateAfter(data).stream().map(ad -> mapToString(ad.getPrice(), ad.getLink(), ad.getAddress(), ad.getFromAgent())).collect(Collectors.toList());
    }

    private String mapToString(String price, String link, String address, Boolean fromAgent){
        String fromAgentString = "";
        if(fromAgent != null) {
            fromAgentString = fromAgent ? "от агента" : "от собственника";
        }
        return String.format("цена: %s, ссылка: %s, адрес: %s\n(%s)", price, link, address, fromAgentString);
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class AdDataDto {
        private String price;
        private String address;
        private String link;
        private Boolean fromAgent;
    }

}
