package com.example.criminalintent.activitys;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.criminalintent.R;


/**
 * 本类是一个抽象类，用于解决重复代码的问题
 */

public abstract class  SingleFragmentActivity extends AppCompatActivity {
    //抽象方法，继承此类必须要实现
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frgment);
        FragmentManager fm= getSupportFragmentManager();
        Fragment fragment =fm.findFragmentById(R.id.fragment_container);
        //如果对应的视图中没有Fragment就添加一个Fragment
        if (fragment==null){
            //这里具体实例化哪个Fragment由子类决定
            fragment=createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container,fragment)
                    .commit();
        }

    }
}
