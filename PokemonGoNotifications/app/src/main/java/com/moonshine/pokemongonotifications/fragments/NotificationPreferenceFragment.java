package com.moonshine.pokemongonotifications.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moonshine.pokemongonotifications.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationPreferenceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationPreferenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationPreferenceFragment extends Fragment {

    public NotificationPreferenceFragment() {
        // Required empty public constructor
    }

    public static NotificationPreferenceFragment newInstance() {
        NotificationPreferenceFragment fragment = new NotificationPreferenceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification_preference, container, false);
    }
}
