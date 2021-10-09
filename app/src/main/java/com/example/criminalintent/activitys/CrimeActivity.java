package com.example.criminalintent.activitys;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.criminalintent.R;
import com.example.criminalintent.fragments.CrimeFragment;

import java.util.UUID;

/**
 * 此类已经废除
 */
public class CrimeActivity extends SingleFragmentActivity {
    private final static String EXTRA_CRIME_ID="com.example.criminalintent.crime_id";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frgment);

    }
    //继承SingleFragmentActivity抽象类实现的方法
    @Override
    protected Fragment createFragment() {
        UUID crimeId= (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }
    public static Intent newIntent(Context packageContext, UUID uuid){
        Intent intent=new Intent(packageContext,CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,uuid);
        return intent;
    }
}