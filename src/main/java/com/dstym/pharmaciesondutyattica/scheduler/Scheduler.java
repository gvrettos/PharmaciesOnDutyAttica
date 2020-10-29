package com.dstym.pharmaciesondutyattica.scheduler;

import com.dstym.pharmaciesondutyattica.scraper.AvailablePharmacyScraper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@EnableScheduling
@Component
public class Scheduler {
    // cron properties
    // <second> <minute> <hour> <day-of-month> <month> <day of week>

    private final CacheManager cacheManager;

    public Scheduler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void clearCache() {
        Objects.requireNonNull(cacheManager.getCache("workingHourCache")).clear();
        Objects.requireNonNull(cacheManager.getCache("workingHoursCache")).clear();
        Objects.requireNonNull(cacheManager.getCache("pharmacyCache")).clear();
        Objects.requireNonNull(cacheManager.getCache("pharmaciesCache")).clear();
        Objects.requireNonNull(cacheManager.getCache("availablePharmaciesCache")).clear();
    }

    @Scheduled(cron = "0 0 0/7 * * *")
    public void getAvailablePharmaciesTwicePerDay() {
        var daysFromToday = 0;
        AvailablePharmacyScraper.saveAvailablePharmacies(daysFromToday);
        clearCache();
    }

    // run only once after startup
    @EventListener(ApplicationReadyEvent.class)
    public void getAvailablePharmaciesAfterStartup() {
        var daysFromToday = 0;
        AvailablePharmacyScraper.saveAvailablePharmacies(daysFromToday);
        clearCache();
    }

//    @EventListener(ApplicationReadyEvent.class)
//    public void getAvailablePharmaciesForLastDaysAfterStartup() {
//        var numOfDays = 10;
//        AvailablePharmacyScraper.saveAvailablePharmaciesForLastDays(numOfDays);
//        clearCache();
//    }

//    @EventListener(ApplicationReadyEvent.class)
//    public void getPharmaciesAndWorkingHoursAfterStartup() {
//        PharmacyScraper.savePharmacies();
//        WorkingHourScraper.saveWorkingHours();
//        clearCache();
//    }
}
