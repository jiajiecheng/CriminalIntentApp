package com.example.criminalintent.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.criminalintent.R;
import com.example.criminalintent.classs.Crime;
import com.example.criminalintent.classs.CrimeLab;
import com.example.criminalintent.fragments.CrimeFragment;
import com.example.criminalintent.fragments.CrimeListFragment;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callback {
    private static final String EXTRA_CRIME_ID= "com.example.criminalintent.crimePagerActivity.crime_id";
    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        //获取本类newIntent方法中的Intent对象后，再获取其中得数据
        UUID uuid = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        mViewPager=findViewById(R.id.crime_view_pager);
        mCrimes= CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @NonNull

            @Override
            public Fragment getItem(int position) {
                Crime crime=mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(uuid)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
    public static Intent newIntent(Context context, UUID crimeId){
        Intent intent=new Intent(context,CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeId);
        return intent;
    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }
}