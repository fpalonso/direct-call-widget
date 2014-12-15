package com.blaxsoftware.directcallwidget.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blaxsoftware.directcallwidget.R;

public class CustomizeWidgetFragment extends Fragment {

    private static final String TAG = "CustomizeWidget";

    public static CustomizeWidgetFragment findOrCreate(FragmentManager fragMngr) {
        CustomizeWidgetFragment frag = (CustomizeWidgetFragment) fragMngr
                .findFragmentByTag(TAG);
        if (frag == null) {
            frag = new CustomizeWidgetFragment();
            // fragMngr.beginTransaction()
            // .add(R.id.customizeFragContainer, frag, TAG).commit();
        }
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customize_widget,
                container, false);
        return rootView;
    }
}
