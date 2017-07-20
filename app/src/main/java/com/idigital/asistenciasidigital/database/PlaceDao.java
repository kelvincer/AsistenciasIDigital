package com.idigital.asistenciasidigital.database;

import com.idigital.asistenciasidigital.model.Place;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by USUARIO on 07/04/2017.
 */

public class PlaceDao {

    private Dao<Place, Integer> placeDao;

    public PlaceDao(DatabaseHelper helper) {
        try {
            this.placeDao = helper.getPlaceDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPlace(Place place) {

        try {
            placeDao.createOrUpdate(place);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPlaceList(List<Place> places) {

        for (Place p : places) {
            insertPlace(p);
        }
    }

    public List<Place> getAllPlaces() {

        try {
            return placeDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
