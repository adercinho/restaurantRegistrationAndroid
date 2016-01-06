package br.com.devops.restaurantregistration.manager;

import android.content.Context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.devops.restaurantregistration.helper.RestaurantHelper;
import br.com.devops.restaurantregistration.model.Restaurant;

/**
 * Created by adercio on 23/12/15.
 */
public class DatabaseManager {
    static private DatabaseManager instance;

    static public void init(Context ctx) {
        if (null==instance) {
            instance = new DatabaseManager(ctx);
        }
    }

    static public DatabaseManager getInstance() {
        return instance;
    }

    private RestaurantHelper helper;
    private DatabaseManager(Context ctx) {
        helper = new RestaurantHelper(ctx);
    }

    private RestaurantHelper getHelper() {
        return helper;
    }

    public List<Restaurant> getAllRestaurantLists() {
        List<Restaurant> restaurantList = new ArrayList<>();
        try {
            restaurantList = getHelper().getRestaurantDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurantList;
    }

    public List<Restaurant> findRestaurantByName(final String name) {
        List<Restaurant> restaurantList = new ArrayList<>();
        try {
            restaurantList = getHelper().getRestaurantDao().queryBuilder().where().like("name", "%"+name+"%").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurantList;
    }

    public void saveRestaurant(final Restaurant restaurant) {
        try {
            getHelper().getRestaurantDao().create(restaurant);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRestaurant(final Restaurant restaurant) {
        try {
            getHelper().getRestaurantDao().update(restaurant);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRestaurant(final Restaurant restaurant) {
        try {
            getHelper().getRestaurantDao().deleteById(restaurant.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Restaurant searchRestaurantById(final Long id) {
        Restaurant restaurant = null;
        try {
            restaurant = getHelper().getRestaurantDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurant;
    }
}
