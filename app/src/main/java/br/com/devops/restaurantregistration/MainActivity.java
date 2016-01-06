package br.com.devops.restaurantregistration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.devops.restaurantregistration.manager.DatabaseManager;
import br.com.devops.restaurantregistration.model.Restaurant;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String URL_SERVICE = "https://guarded-lowlands-1463.herokuapp.com/api/restaurants";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RestaurantSyncData restaurantSyncData = new RestaurantSyncData();
        restaurantSyncData.execute(URL_SERVICE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_register) {
            // Handle the register action
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        } else if (id == R.id.nav_my_restaurants) {
            startActivity(new Intent(MainActivity.this, RestaurantListActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this, RestaurantAboutActivity.class));
        }else if (id == R.id.nav_maps_restaurants) {
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class RestaurantSyncData extends AsyncTask<String, Void, String> {

        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(
                    MainActivity.this,
                    "Main",
                    "Buscando restaurantes em serviço externo"
            );
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                if (connection.getResponseCode() == 200) {

                    Reader reader = new InputStreamReader(connection.getInputStream());
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                    Gson gson = gsonBuilder.create();
                    List<Restaurant> restaurantList = new ArrayList<Restaurant>();
                    restaurantList = Arrays.asList(gson.fromJson(reader, Restaurant[].class));
                    synchronizedRestaurant(restaurantList);
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
                progress.dismiss();
            }
            return null;

        }

        private void synchronizedRestaurant(List<Restaurant> restaurantListService) {
            if(restaurantListService != null && !restaurantListService.isEmpty()){
                DatabaseManager.init(MainActivity.this);
                List<Restaurant> allRestaurantListsDB = DatabaseManager.getInstance().getAllRestaurantLists();
                for (Restaurant restaurantService: restaurantListService) {
                    int indexRestaurant = allRestaurantListsDB.indexOf(restaurantService);
                    if(indexRestaurant == -1){
                        // Restaurante não registrado na base - Registre o novo restaurante do servico
                        DatabaseManager.getInstance().saveRestaurant(restaurantService);
                    }
                }
            }
        }
    }
}
