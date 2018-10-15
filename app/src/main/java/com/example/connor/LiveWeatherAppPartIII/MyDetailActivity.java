package com.example.connor.LiveWeatherAppPartIII;

import android.graphics.Typeface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyDetailActivity extends AppCompatActivity {

    TextView cv_tvIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Back");

        Bundle extras = getIntent().getExtras();
        int position = extras.getInt("Position");
        ArrayList<City> MyCityData = extras.getParcelableArrayList("MyCityData");

        City cv_detailCity = MyCityData.get(position);

        String mv_strCityName = cv_detailCity.getCityName();
        String mv_strCityState = cv_detailCity.getCityState();
        Double mv_dbCityTemp = cv_detailCity.getCityTemp();
        Double mv_dbCityTempHigh = cv_detailCity.getCityTempHigh();
        Double mv_dbCityTempLow = cv_detailCity.getCityTempLow();
        String mv_dbCityWeatherDesc = cv_detailCity.getCityWeatherDesc();
        String mv_dbCityWeatherTypeIcon = cv_detailCity.getCityWeatherTypeIcon();

        TextView cv_tvDetailCityName = (TextView) findViewById(R.id.vv_tvDetailCityAndState);
        cv_tvDetailCityName.setText(mv_strCityName +", " + mv_strCityState);

        TextView cv_tvDetailCityTemp = (TextView) findViewById(R.id.vv_tvDetailCityTemp);
        cv_tvDetailCityTemp.setText(mv_dbCityTemp.toString());

        TextView cv_tvDetailCityWeatherIcon = (TextView) findViewById(R.id.vv_tvDetailCityWeatherIcon);
        cv_tvDetailCityWeatherIcon.setText(mv_dbCityWeatherTypeIcon);

        TextView cv_tvDetailCityWeatherDesc = (TextView) findViewById(R.id.vv_tvDetailCityWeatherDesc);
        cv_tvDetailCityWeatherDesc.setText(mv_dbCityWeatherDesc);

        TextView cv_tvDetailCityTempHigh = (TextView) findViewById(R.id.vv_tvDetailCityTempHigh);
        cv_tvDetailCityTempHigh.setText(mv_dbCityTempHigh.toString());

        TextView cv_tvDetailCityTempLow = (TextView) findViewById(R.id.vv_tvDetailCityTempLow);
        cv_tvDetailCityTempLow.setText(mv_dbCityTempLow.toString());

        TextView cv_tvDetailCityTempAbv = (TextView) findViewById(R.id.vv_detailTempAbv);
        cv_tvDetailCityTempAbv.setText(cv_detailCity.getCityTempFormatAbv());



        cv_tvIcon = (TextView) findViewById(R.id.vv_tvDetailCityWeatherIcon);

        Typeface lv_customFont = Typeface.createFromAsset(getAssets(), "fonts/Climacons.ttf");
        cv_tvIcon.setTypeface(lv_customFont);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

}
