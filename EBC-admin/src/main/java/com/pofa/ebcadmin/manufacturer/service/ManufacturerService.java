package com.pofa.ebcadmin.manufacturer.service;

import com.pofa.ebcadmin.manufacturer.dto.Manufacturer;
import com.pofa.ebcadmin.manufacturer.entity.ManufacturerInfo;

import java.util.List;

public interface ManufacturerService {

    int addManufacturer(Manufacturer.AddDTO dto);

    int editManufacturer(Manufacturer.EditDTO dto);

    List<ManufacturerInfo> getManufacturersByProductId(Long productId);


    int deprecateManufacturersByUid(Long uid);

    int deleteManufacturersByUid(Long uid);


}
