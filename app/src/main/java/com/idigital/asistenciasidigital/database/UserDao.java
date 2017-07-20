package com.idigital.asistenciasidigital.database;

import com.idigital.asistenciasidigital.model.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

/**
 * Created by Kelvin on 19/07/2017.
 */

public class UserDao {

    private Dao<User, String> userDao;

    public UserDao(DatabaseHelper helper) {
        try {
            this.userDao = helper.getUserDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertUser(User user) {

        try {
            userDao.createOrUpdate(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User findUserById(String email) {

        QueryBuilder<User, String> queryBuilder = userDao.queryBuilder();

        try {
            User user = userDao.queryForId(email);
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public User findUserByLoggedIn() {

        QueryBuilder<User, String> queryBuilder = userDao.queryBuilder();
        try {
            queryBuilder.where().eq("loggedIn", true);
            User user = queryBuilder.queryForFirst();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void deleteUser(User user) {

        try {
            userDao.deleteById(user.getEmail());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
