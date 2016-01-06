package br.com.devops.restaurantregistration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.com.devops.restaurantregistration.manager.DatabaseManager;
import br.com.devops.restaurantregistration.model.Restaurant;

public class RestaurantListActivity extends AppCompatActivity {

    private ListView restaurantListView;
    private Button searchButton;
    private EditText searchText;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseManager.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        restaurantListView = (ListView) findViewById(R.id.restaurantListView);
        searchText = (EditText) findViewById(R.id.search_text);
        searchButton = (Button) findViewById(R.id.search_button);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               loadGrid(RestaurantListActivity.this);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        loadGrid(this);
    }

    private void loadGrid(final Activity activity) {
        final List<Restaurant> restaurants = DatabaseManager.getInstance().findRestaurantByName(searchText.getText().toString());
        List<String> titles = new ArrayList<String>();
        for (Restaurant restaurant : restaurants) {
            titles.add(restaurant.getName().toUpperCase());
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
        restaurantListView.setAdapter(adapter);

        restaurantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Restaurant restaurant = restaurants.get(position);
                Intent intent = new Intent(activity, RestaurantDetailActivity.class);
                intent.putExtra("restaurantId", restaurant.getId());
                startActivity(intent);
            }
        });
    }


}
