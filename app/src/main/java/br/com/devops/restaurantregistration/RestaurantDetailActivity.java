package br.com.devops.restaurantregistration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.com.devops.restaurantregistration.manager.DatabaseManager;
import br.com.devops.restaurantregistration.model.Restaurant;

public class RestaurantDetailActivity extends AppCompatActivity {

    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseManager.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle bundle = getIntent().getExtras();
        Long idRestaurant = bundle.getLong("restaurantId");
        restaurant = DatabaseManager.getInstance().searchRestaurantById(idRestaurant);

        setInformationView(restaurant);

        Button btnDelete = (Button) findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseManager.getInstance().deleteRestaurant(restaurant);
                Intent intent = new Intent(RestaurantDetailActivity.this, RestaurantListActivity.class);
                startActivity(intent);
            }
        });

        Button btnEdit = (Button) findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(RestaurantDetailActivity.this, RegisterActivity.class);
               intent.putExtra("restaurantId", restaurant.getId());
               startActivity(intent);
           }
        });
    }



    private void setInformationView(Restaurant restaurant) {
        TextView textViewName = (TextView)findViewById(R.id.name);
        textViewName.setText("Name Restaurant: "+restaurant.getName());

        TextView textViewPhoneNumber = (TextView)findViewById(R.id.phoneNumber);
        textViewPhoneNumber.setText("Phone Number: "+restaurant.getPhoneNumber());

        TextView textViewType = (TextView)findViewById(R.id.type);
        if(restaurant.getType() != null) {
            textViewType.setText("Type: " + restaurant.getType().getDescription());
        }
        TextView textViewObservation = (TextView)findViewById(R.id.observation);
        textViewObservation.setText("Observation: "+restaurant.getObservation());

        TextView textViewLatitude = (TextView)findViewById(R.id.latitude);
        textViewLatitude.setText("Latitude: "+restaurant.getLatitude());

        TextView textViewLongitude = (TextView)findViewById(R.id.longitude);
        textViewLongitude.setText("Longitude: "+restaurant.getLongitude());
    }
}
