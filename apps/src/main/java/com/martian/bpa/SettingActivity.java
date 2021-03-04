package com.martian.bpa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.martian.bpa.Property.PropertySet;
import com.martian.bpa.alarm.TrackingAlarm;
import com.martian.bpa.util.Util;

public class SettingActivity extends AppCompatActivity {
    private EditText mDataCountCtl;
    private int mDataCount;
    private Switch mTrackingEnabled;

    private TrackingAlarm mTrackingAlarm;

    private PropertySet mPropertySet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initCtl();
        initData();
        initView();
    }

    void initCtl() {
        mDataCountCtl = (EditText)findViewById(R.id.et_datacount);
        ((Button)findViewById(R.id.btn_ok)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPropertySet.setDisplayCount(Integer.parseInt(
                                mDataCountCtl.getText().toString()));
                        MainActivity.updateSetting(mPropertySet);
                        finish();
                    }
                }
        );

        mTrackingEnabled = (Switch)findViewById(R.id.sw_tracking);
        mTrackingEnabled.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mTrackingAlarm.setAlarm(TrackingAlarm.INTERVAL_SEC);
                        } else {
                            mTrackingAlarm.releaseAlarm();
                        }

                        Util.setProperty(SettingActivity.this, PropertySet.TAG_CONF_TRACKIGN_ENABLED, isChecked);
                    }
                }
        );

        ((Button)findViewById(R.id.btn_check_alarm)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("BPA.Alarm", "Alarm Activate: " + mTrackingAlarm.isAlarmActivated());
                    }
                }
        );
    }

    void initData() {
        mDataCount = MainActivity.getSearchDataCount();
        mPropertySet = getIntent().getParcelableExtra(PropertySet.TAG_INTENTNAME);
        mTrackingAlarm = TrackingAlarm.getInstance(this);
    }

    void initView() {
        mDataCountCtl.setText(String.valueOf(mDataCount));

        mTrackingEnabled.setChecked(mTrackingAlarm.isAlarmActivated());
    }
}
