package com.services;

import com.dto.Advert;

import java.util.List;

/**
 * Created by andrii on 24.01.16.
 */
public interface SeleniumService {
    public List<Advert> getAllCars(String carUrl);
}
