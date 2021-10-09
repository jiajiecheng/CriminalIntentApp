package com.example.criminalintent.fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.criminalintent.R;
import com.example.criminalintent.classs.PictureUtils;

import java.io.File;

public class PhotoDetailFragment extends DialogFragment {
    private static final String ARG_File = "file";
    private ImageView mPhotoView;

    public static PhotoDetailFragment newInstance(File file) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_File, file);

        PhotoDetailFragment fragment = new PhotoDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        File file = (File) getArguments().getSerializable(ARG_File);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.phone_dialog, null);
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo_detail);

        Bitmap bitmap = PictureUtils.getScaleBitMap(
                file.getPath(), getActivity());
        mPhotoView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                //.setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }


}
