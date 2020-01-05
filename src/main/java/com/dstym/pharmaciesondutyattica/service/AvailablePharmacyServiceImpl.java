package com.dstym.pharmaciesondutyattica.service;

import com.dstym.pharmaciesondutyattica.entity.AvailablePharmacy;
import com.dstym.pharmaciesondutyattica.entity.Pharmacy;
import com.dstym.pharmaciesondutyattica.repository.AvailablePharmacyRepository;
import com.dstym.pharmaciesondutyattica.repository.PharmacyRepository;
import com.dstym.pharmaciesondutyattica.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class AvailablePharmacyServiceImpl implements AvailablePharmacyService {
    private AvailablePharmacyRepository availablePharmacyRepository;
    private PharmacyRepository pharmacyRepository;

    @Autowired
    public AvailablePharmacyServiceImpl(AvailablePharmacyRepository availablePharmacyRepository,
                                        PharmacyRepository pharmacyRepository) {
        this.availablePharmacyRepository = availablePharmacyRepository;
        this.pharmacyRepository = pharmacyRepository;
    }

    @Override
    public List<AvailablePharmacy> findAll() {
        return availablePharmacyRepository.findAll();
    }

    @Override
    public List<AvailablePharmacy> findAllTodayByRegion(String urlRegion) {
        String region = URLDecoder.decode(urlRegion, StandardCharsets.UTF_8);

        List<Pharmacy> result = pharmacyRepository.findByRegion(region);

        if (result.isEmpty()) {
            throw new RuntimeException("Did not find pharmacy in region: " + region);
        }

        var daysFromToday = 0;
        var date = DateUtils.dateToString(DateUtils.getDateFromTodayPlusDays(daysFromToday));
        var tempAvailablePharmacy =
                (AvailablePharmacy) availablePharmacyRepository.findFirstByDateOrderByPulledVersionDesc(date)
                        .toArray()[0];
        var lastPulledVersion = tempAvailablePharmacy.getPulledVersion();

        return availablePharmacyRepository.findByDateAndAndPulledVersionAndPharmacyRegion(date, lastPulledVersion, region);
    }

    @Override
    public List<AvailablePharmacy> findAllToday() {
        var daysFromToday = 0;
        var date = DateUtils.dateToString(DateUtils.getDateFromTodayPlusDays(daysFromToday));
        var tempAvailablePharmacy =
                (AvailablePharmacy) availablePharmacyRepository.findFirstByDateOrderByPulledVersionDesc(date)
                        .toArray()[0];
        var lastPulledVersion = tempAvailablePharmacy.getPulledVersion();

        return availablePharmacyRepository.findByDateAndAndPulledVersion(date, lastPulledVersion);
    }

    @Override
    public List<AvailablePharmacy> findAllByDate(String urlDate) {
        var date = urlDate.replaceAll("-", "/");

        List<AvailablePharmacy> result = availablePharmacyRepository.findFirstByDateOrderByPulledVersionDesc(date);

        if (result.isEmpty()) {
            throw new RuntimeException("Did not find availablePharmacy for date: " + date);
        }

        var tempAvailablePharmacy = (AvailablePharmacy) result.toArray()[0];

        var lastPulledVersion = tempAvailablePharmacy.getPulledVersion();

        return availablePharmacyRepository.findByDateAndAndPulledVersion(date, lastPulledVersion);
    }

    @Override
    public AvailablePharmacy findById(long theId) {
        Optional<AvailablePharmacy> result = availablePharmacyRepository.findById(theId);

        AvailablePharmacy availablePharmacy;

        if (result.isPresent()) {
            availablePharmacy = result.get();
        } else {
            throw new RuntimeException("Did not find availablePharmacy with id: " + theId);
        }

        return availablePharmacy;
    }

    @Override
    public void save(AvailablePharmacy availablePharmacy) {
        availablePharmacyRepository.save(availablePharmacy);
    }

    @Override
    public void deleteById(long theId) {
        availablePharmacyRepository.deleteById(theId);
    }
}
