package com.dstym.pharmaciesondutyattica.service;

import com.dstym.pharmaciesondutyattica.entity.AvailablePharmacy;

import java.util.List;

public interface AvailablePharmacyService {
    List<AvailablePharmacy> findAll();

    AvailablePharmacy findById(long theId);

    void save(AvailablePharmacy availablePharmacy);

    void deleteById(long theId);
}
