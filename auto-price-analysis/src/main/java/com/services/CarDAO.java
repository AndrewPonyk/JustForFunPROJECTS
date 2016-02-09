package com.services;

import com.dto.Advert;

import java.util.List;

/**
 * Created by andrii on 28.01.16.
 */
public interface CarDAO {
    public Integer saveCars(List<Advert> cars);
}
