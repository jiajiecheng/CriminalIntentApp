package com.example.criminalintent.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.example.criminalintent.R;
import com.example.criminalintent.classs.Crime;
import com.example.criminalintent.fragments.CrimeFragment;
import com.example.criminalintent.fragments.CrimeListFragment;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callback, CrimeFragment.Callback {


    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.upDataUI();
    }
}