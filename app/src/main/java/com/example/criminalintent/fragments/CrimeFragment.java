package com.example.criminalintent.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.criminalintent.R;
import com.example.criminalintent.activitys.CrimeActivity;
import com.example.criminalintent.classs.Crime;
import com.example.criminalintent.classs.CrimeLab;
import com.example.criminalintent.classs.PictureUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.widget.CompoundButton.*;

public class CrimeFragment extends Fragment {
    public static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    //用于Date数据的回传
    private static final int REQUEST_DATE = 0;
    //用于获取系统中联系人的信息，这个是联系人数据的请求码
    private static final int REQUEST_CONTACT = 1;
    //用于获取拍摄的照片
    private static final int REQUEST_PHONE=2;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private UUID mCrimeId;
    //点击分享按钮
    private Button mReportButton;
    //点击选择联系人按钮
    private Button mSuspectButton;
    //用于拍照
    private ImageButton mPhoneButton;
    //用于显示照片
    private ImageView mPhoneView;
    //照片文件对象
    private File mPhoneFile;
    //用于展示大图
    private static final int REQUEST_PHOTO = 3;
    private static final String DIALOG_PHOTO = "DialogPhoto";

    private Callback mCallback;
    //回调接口
    public interface Callback{
        void onCrimeUpdated(Crime crime);
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
    private void updateCrime(){
        CrimeLab.get(getActivity()).upDateCrime(mCrime);
        mCallback.onCrimeUpdated(mCrime);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获得传入的ID数据来显示不同碎片界面
        setHasOptionsMenu(true);
        mCrimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(mCrimeId);
        //定义好相片文件保存的路径
        mPhoneFile =CrimeLab.get(getActivity()).getPhoneFile(mCrime);
    }

    //初始化视图
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = view.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mDateButton = view.findViewById(R.id.crime_date);
        mSolvedCheckBox = view.findViewById(R.id.crime_solved);
        mReportButton = view.findViewById(R.id.crime_report);
        mPhoneButton =view.findViewById(R.id.crime_camera);

        mSolvedCheckBox.setChecked(mCrime.isSolved());
        //按钮事件
        updateDate();
        //让用户不能够点击此按钮
        mDateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                //设置Date数据回传
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                //第二个参数为唯一的标志
                dialog.show(fragmentManager, DIALOG_DATE);
            }
        });
        //编辑框事件
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //当用户进行输入的时候就会调用这个方法
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //单选框事件
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        //此按钮用于向联系人发送对应的信息
        mReportButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                //用于显示全部的Activity列表
                i = Intent.createChooser(i,getString(R.string.send_report));
                startActivity(i);
            }
        });

        //用于读取系统中联系人数据，相当于让Android系统帮助我们来访问联系人的信息（数据库）
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = view.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }
        //当手机中没有可用的联系人应用的时候，此时读取联系人的按钮就会无法点击，这样可以防止应用崩溃
        PackageManager packageManager =getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY)==null){
            mSuspectButton.setEnabled(false);
        }
        //使用隐式意图来拍照
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //当我们的确定文件路径以及设备能够的支持拍照之后才开启拍照的按钮
        boolean canTakePhone = mPhoneFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhoneButton.setEnabled(canTakePhone);
        mPhoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.criminalintent.fileprovider",mPhoneFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                //解析Intent中信息并且进行统一的授权
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY);
                for(ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage,REQUEST_PHONE);
            }
        });
        mPhoneView =view.findViewById(R.id.crime_phone);
        //点击图片查看大图
        mPhoneView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhoneFile == null || !mPhoneFile.exists()) {
                    mPhoneView.setImageDrawable(null);
                } else {
                    FragmentManager manager = getFragmentManager();
                    PhotoDetailFragment dialog = PhotoDetailFragment.newInstance(mPhoneFile);
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_PHOTO);
                    dialog.show(manager, DIALOG_PHOTO);

                }

            }
        });
        upDatePhoneView();
        return view;
    }

    //此方法用于实例化本类,并且传输Id数据
    public static CrimeFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, uuid);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    //一定需要区分以下的两个参数
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        }else if (requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            //获取所有联系人的姓名
            String []queryFields  = new  String[]{ContactsContract.Contacts.DISPLAY_NAME};
            //用于查询表中的记录，并且返回一个记录对象
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            try {
                if (c.getCount() ==0){
                    return;
                }
                c.moveToFirst();
                String suspect  = c.getString(0);
                mCrime.setSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);
            }finally {
                c.close();
            }
        }else if (requestCode == REQUEST_PHONE){
            Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.criminalintent.fileprovider",mPhoneFile);
            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
            upDatePhoneView();
        }
    }

    private void updateDate() {
        mDateButton.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(mCrime.getDate()));
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).upDateCrime(mCrime);
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_fragment, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.del_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //用于拼接字符串,用作分享短信中的内容
    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }
    public void upDatePhoneView(){
        if (mPhoneView == null || !mPhoneFile.exists()){
            mPhoneView.setImageDrawable(null);
        }else {
            Bitmap bitmap = PictureUtils.getScaleBitMap(mPhoneFile.getPath(),getActivity());
            mPhoneView.setImageBitmap(bitmap);
        }
    }


}
