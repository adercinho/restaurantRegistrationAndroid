package br.com.devops.restaurantregistration;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.devops.restaurantregistration.manager.DatabaseManager;
import br.com.devops.restaurantregistration.model.Restaurant;
import br.com.devops.restaurantregistration.model.Type;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private EditText name;
    private EditText observation;
    private EditText phoneNumber;
    private RadioGroup radioGrouptype;
    private Type type;
    private Long idRestaurant;
    private Button btnRegisterOrUpdate;
    private Location locationCurrent;
    private GoogleApiClient mGoogleApiClient;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseManager.init(this);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            idRestaurant = bundle.getLong("restaurantId");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignFieldToRegister();

        populateFieldIfEdit(idRestaurant);

        onClickRegister();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void assignFieldToRegister() {
        name = (EditText) findViewById(R.id.name);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        radioGrouptype = (RadioGroup) findViewById(R.id.radio_group_type);
        observation = (EditText) findViewById(R.id.observation);
        type = getSelectedType(radioGrouptype);
        btnRegisterOrUpdate = (Button) findViewById(R.id.register_button);
    }

    private void populateFieldIfEdit(final Long idRestaurant) {
        if(idRestaurant != null) {
            restaurant = DatabaseManager.getInstance().searchRestaurantById(idRestaurant);
            name.setText(restaurant.getName());
            phoneNumber.setText(restaurant.getPhoneNumber());
            setSelectedRadioGroupType(restaurant.getType());
            observation.setText(restaurant.getObservation());
            btnRegisterOrUpdate.setText("Update");
        }

    }

    private void onClickRegister() {

        btnRegisterOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignFieldToRegister();
                if (isValidField(name, "Name is Required!")
                        && isValidField(phoneNumber, "Phone Number is Required!")
                        && isCheckRadioGroup(radioGrouptype, "Select at least one type!")
                        && isValidField(observation, "Observation is Required!")) {
                    saveOrUpdateRestaurant(name.getText().toString(), phoneNumber.getText().toString(), observation.getText().toString(), type);
                    Intent intent = new Intent(RegisterActivity.this, RestaurantListActivity.class);
                    startActivity(intent);
                }
            }
        });
    }


    private void saveOrUpdateRestaurant(final String name, final String phoneNumber, final String observation, final Type type) {
        final Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setPhoneNumber(phoneNumber);
        restaurant.setObservation(observation);
        restaurant.setType(type);

        if(idRestaurant == null){
            if(locationCurrent != null) {
                restaurant.setLatitude(String.valueOf(locationCurrent.getLatitude()));
                restaurant.setLongitude(String.valueOf(locationCurrent.getLongitude()));
                DatabaseManager.getInstance().saveRestaurant(restaurant);
            }else{
                Toast.makeText(this, "Unable to fetch the current location. Try active the GPS!", Toast.LENGTH_SHORT).show();
            }
        }else{
            // update
            restaurant.setId(idRestaurant);
            restaurant.setLongitude(this.restaurant.getLongitude());
            restaurant.setLatitude(this.restaurant.getLatitude());
            DatabaseManager.getInstance().updateRestaurant(restaurant);
        }

    }

    private boolean isValidField(final EditText field, final String message){
        if ( field.getText().toString().equals("") ) {
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isCheckRadioGroup( final RadioGroup radioGroup, final String message){
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private Type getSelectedType(RadioGroup radioGrouptype) {
        final int checkedRadioButtonId = radioGrouptype.getCheckedRadioButtonId();
        Type typeSelected = null;
        switch (checkedRadioButtonId) {
            case R.id.radio_domicile:
                typeSelected = Type.DOMICILE;
                break;
            case R.id.radio_caster:
                typeSelected = Type.CASTER;
                break;
            case R.id.radio_fast_food:
                typeSelected = Type.FAST_FOOD;
                break;
            case R.id.radio_dont_know:
                typeSelected = Type.DONT_KNOW;
                break;
        }
        return typeSelected;
    }

    private void setSelectedRadioGroupType(final Type type) {
        if(type == Type.DOMICILE){
            radioGrouptype.check(R.id.radio_domicile);
        }else if(type == Type.CASTER){
            radioGrouptype.check(R.id.radio_caster);
        }else if(type == Type.FAST_FOOD){
            radioGrouptype.check(R.id.radio_fast_food);
        }else if(type == Type.DONT_KNOW){
            radioGrouptype.check(R.id.radio_dont_know);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationCurrent = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }
}
