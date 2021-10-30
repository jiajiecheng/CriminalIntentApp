package com.example.criminalintent.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.criminalintent.R;
import com.example.criminalintent.activitys.CrimeActivity;
import com.example.criminalintent.activitys.CrimePagerActivity;
import com.example.criminalintent.classs.Crime;
import com.example.criminalintent.classs.CrimeLab;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.SimpleFormatter;

import static com.example.criminalintent.fragments.CrimeFragment.ARG_CRIME_ID;

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    //标题与小标题隐藏与显示的标志位
    private boolean mSubtitleVisible;
    private Callback mCallback;


    //用于恢复数据
    private static final String SAVED_SUBTITLE_VISIBLE="subtitle";
    //回调接口
    public interface Callback{
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(@NonNull  Context context) {
        super.onAttach(context);
        mCallback= (Callback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback=null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //自定义方法
        if (savedInstanceState != null){
            mSubtitleVisible=savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        upDataUI();
        return view;
    }

    public void upDataUI() {
        //获得单例对象中的数据，并且设置mCrimeRecyclerView适配器
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    //内部类ViewHolder
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this::onClick);
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(mCrime.getDate()));

            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            mCallback.onCrimeSelected(mCrime);
        }
    }

    //内部类RecyclerView.Adapter
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull

        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeListFragment.CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
        public void setCrimes(List<Crime> crimes){
            mCrimes=crimes;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        upDataUI();
    }
    //用于展示工具栏
    @Override
    public void onCreateOptionsMenu(@NonNull  Menu menu, @NonNull  MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);



    }
    //用于工具栏事件的响应
    @Override
    public boolean onOptionsItemSelected(@NonNull  MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                Crime crime=new Crime();
                CrimeLab.get(getActivity()).add(crime);
                upDataUI();
                mCallback.onCrimeSelected(crime);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = ! mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void updateSubtitle(){
        CrimeLab crimeLab=CrimeLab.get(getActivity());
        //获取列表大小
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format,crimeCount);
        if(!mSubtitleVisible){
            subtitle=null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }
    //用于保存子标题栏状态
    @Override
    public void onSaveInstanceState(@NonNull  Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }
}
