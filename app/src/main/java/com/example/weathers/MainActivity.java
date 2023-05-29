package com.example.weathers;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {
    String CITY1;
    TextView addressT, updated_atT, statusT, tempT ,windT, humidityT;
    ImageView image;
    EditText CITY;
    String ad,up,st,tm,wn,hu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = findViewById(R.id.back);
        CITY=findViewById(R.id.city);
        getVal();
        Toast.makeText(MainActivity.this, "Welcome!!", Toast.LENGTH_SHORT).show();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, "Thank you!!", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        Toast.makeText(MainActivity.this, "Thank you!!", Toast.LENGTH_SHORT).show();

    }

    //method to get values from the sharedpreferences.
    private void getVal() {
        SharedPreferences sh= getSharedPreferences("MySharedPref",MODE_PRIVATE);
        ad=sh.getString("ad","");
        up=sh.getString("up","");
        st=sh.getString("st","");
        tm=sh.getString("tm","");
        wn=sh.getString("wn","");
        hu=sh.getString("hu","");
        addressT = findViewById(R.id.address);
        updated_atT = findViewById(R.id.updated_at);
        statusT = findViewById(R.id.status);
        tempT = findViewById(R.id.temp);
        windT = findViewById(R.id.wind);
        humidityT = findViewById(R.id.humidity);
        addressT.setText(ad);
        updated_atT.setText(up);
        statusT.setText(st);
        tempT.setText(tm);
        windT.setText(wn);
        humidityT.setText(hu);
    }


    public void run(View view){
        CITY1 = CITY.getText().toString();
        new weatherTask().execute();
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String args[]) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q="+CITY1+"&APPID=b1e434ad96c97b67e2290c0ba37515e1");
            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            addressT = findViewById(R.id.address);
            updated_atT = findViewById(R.id.updated_at);
            statusT = findViewById(R.id.status);
            tempT = findViewById(R.id.temp);
            windT = findViewById(R.id.wind);
            humidityT = findViewById(R.id.humidity);

            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp");
                String humidity = main.getString("humidity");
                String windSpeed = wind.getString("speed")+"m/s";
                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");
                addressT.setText(address);
                updated_atT.setText(updatedAtText);
                statusT.setText(weatherDescription.toUpperCase());
                float temperature= Math.round(new Float(temp));
                temperature= (float) (temperature-273.15);
                temp= new String(String.valueOf(temperature)) +"°C";
                tempT.setText(temp);
                windT.setText(windSpeed);
                humidityT.setText(humidity);
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.overviewContainer).setVisibility(View.VISIBLE);
                findViewById(R.id.addressContainer).setVisibility(View.VISIBLE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Results available!!", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("ad", address);
                myEdit.putString("up", updatedAtText);
                myEdit.putString("st", weatherDescription.toUpperCase());
                myEdit.putString("tm", temp);
                myEdit.putString("wn", windSpeed);
                myEdit.putString("hu", humidity);
                myEdit.apply();

            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.overviewContainer).setVisibility(View.GONE);
                findViewById(R.id.addressContainer).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
                CITY.setText("");
            }

        }
    }
}


