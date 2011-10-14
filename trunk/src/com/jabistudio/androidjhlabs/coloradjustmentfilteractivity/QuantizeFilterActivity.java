package com.jabistudio.androidjhlabs.coloradjustmentfilteractivity;

import com.jabistudio.androidjhlabs.SuperFilterActivity;
import com.jabistudio.androidjhlabs.filter.DiffusionFilter;
import com.jabistudio.androidjhlabs.filter.util.AndroidUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class QuantizeFilterActivity extends SuperFilterActivity implements OnSeekBarChangeListener, OnCheckedChangeListener{
    private static final String TITLE = "Quantize";
    private static final String NUNBER_COLOR_STRING = "NUNBER COLOR:";
    private static final String COLORDITHER_STRING = "COLORDITHER";
    private static final String SERPENTINE_STRING = "SERPENTINE";
    private static final int MAX_VALUE = 16;
    
    private static final int NUNBER_COLOR_SEEKBAR_RESID = 21863;
    private static final int COLORDITHER_CHECKBOX_RESID = 21864;
    private static final int SERPENTINE_CHECKBOX_RESID = 21865;
    
    private SeekBar mNumberColorSeekBar;
    private TextView mNumberColorTextView;
    private CheckBox mColorDitherCheckBox;
    private CheckBox mSerpentineCheckBox;
    
    private int mNumberColorValue;
    private boolean mIsColorDither = false;
    private boolean mIsSerpentine = false;
    
    private ProgressDialog mProgressDialog;
    private int[] mColors;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TITLE);
        filterSeekBarSetup(mMainLayout);
    }
    
    /**
     * filterButtonSetting
     * @param mainLayout
     */
    private void filterSeekBarSetup(LinearLayout mainLayout){
        mNumberColorTextView = new TextView(this);
        mNumberColorTextView.setText(NUNBER_COLOR_STRING+mNumberColorValue);
        mNumberColorTextView.setTextSize(TITLE_TEXT_SIZE);
        mNumberColorTextView.setTextColor(Color.BLACK);
        mNumberColorTextView.setGravity(Gravity.CENTER);
        
        mNumberColorSeekBar = new SeekBar(this);
        mNumberColorSeekBar.setOnSeekBarChangeListener(this);
        mNumberColorSeekBar.setId(NUNBER_COLOR_SEEKBAR_RESID);
        mNumberColorSeekBar.setMax(MAX_VALUE);

        mColorDitherCheckBox = new CheckBox(this);
        mColorDitherCheckBox.setText(COLORDITHER_STRING);
        mColorDitherCheckBox.setTextSize(TITLE_TEXT_SIZE);
        mColorDitherCheckBox.setTextColor(Color.BLACK);
        mColorDitherCheckBox.setGravity(Gravity.CENTER);
        mColorDitherCheckBox.setOnCheckedChangeListener(this);
        mColorDitherCheckBox.setId(COLORDITHER_CHECKBOX_RESID);
        
        mSerpentineCheckBox = new CheckBox(this);
        mSerpentineCheckBox.setText(SERPENTINE_STRING);
        mSerpentineCheckBox.setTextSize(TITLE_TEXT_SIZE);
        mSerpentineCheckBox.setTextColor(Color.BLACK);
        mSerpentineCheckBox.setGravity(Gravity.CENTER);
        mSerpentineCheckBox.setOnCheckedChangeListener(this);
        mSerpentineCheckBox.setId(SERPENTINE_CHECKBOX_RESID);
      
        mainLayout.addView(mNumberColorTextView);
        mainLayout.addView(mNumberColorSeekBar);
        mainLayout.addView(mColorDitherCheckBox);
        mainLayout.addView(mSerpentineCheckBox);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch(seekBar.getId()){
        case NUNBER_COLOR_SEEKBAR_RESID:
            mNumberColorValue = progress;
            mNumberColorTextView.setText(NUNBER_COLOR_STRING+mNumberColorValue);
            break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        applyFilter();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundbutton, boolean isChecked) {
        switch(compoundbutton.getId()){
        case COLORDITHER_CHECKBOX_RESID:
            mIsColorDither = isChecked;
            applyFilter();
            break;
        case SERPENTINE_CHECKBOX_RESID:
            mIsSerpentine = isChecked;
            applyFilter();
            break;
        }
    }
    
    private void applyFilter(){
        final int width = mOriginalImageView.getDrawable().getIntrinsicWidth();
        final int height = mOriginalImageView.getDrawable().getIntrinsicHeight();
        
        mColors = AndroidUtils.drawableToIntArray(mOriginalImageView.getDrawable());
        mProgressDialog = ProgressDialog.show(this, "", "Wait......");
        
        Thread thread = new Thread(){
            public void run() {
                DiffusionFilter filter = new DiffusionFilter();
                filter.setColorDither(mIsColorDither);
                filter.setSerpentine(mIsSerpentine);
                filter.setLevels(mNumberColorValue);
                mColors = filter.filter(mColors, width, height);

                QuantizeFilterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setModifyView(mColors, width, height);
                    }
                });
                mProgressDialog.dismiss();
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}
