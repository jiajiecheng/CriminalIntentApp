package com.example.criminalintent.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.criminalintent.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 此类用用于表示展示时间的对话框
 */
public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE="com.example.crimimslintent.date";
    private static final String ARG_DATE="date";
    private DatePicker mDatePicker;
    //用于展示日志
    private static final String TAG="DatePickerFragment";

    @NonNull

    @Override
    public Dialog onCreateDialog(@Nullable  Bundle savedInstanceState) {

        //获得CrimeFragment得到的时间的数据
        Date date= (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day =calendar.get(Calendar.DAY_OF_MONTH);

        //以下实例化对象一定需要在onCreateDialog方法中调用
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date,null);

        //初始化日历控件
        mDatePicker=v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year,month,day,null);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day  = mDatePicker.getDayOfMonth();
                        Log.d(TAG, "onClick: "+year);
                        Log.d(TAG, "onClick: "+month);
                        Log.d(TAG, "onClick: "+day);
                        Date date=new GregorianCalendar(year,month,day).getTime();
                        sendResult(Activity.RESULT_OK,date);
                    }
                })
                .create();
    }
    //使用此方法获得本类的对象
    public static DatePickerFragment newInstance(Date date){
        Bundle args=new Bundle();
        args.putSerializable(ARG_DATE,date);

        DatePickerFragment fragment=new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int code ,Date date){
        if (getTargetFragment()==null){
            return;
        }
        Intent intent=new Intent();
        intent.putExtra(EXTRA_DATE,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),code,intent);
    }
}
