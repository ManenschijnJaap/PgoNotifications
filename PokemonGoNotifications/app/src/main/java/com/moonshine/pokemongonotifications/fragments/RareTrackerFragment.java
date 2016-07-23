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
 * {@link RareTrackerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RareTrackerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RareTrackerFragment extends Fragment {


    public RareTrackerFragment() {
        // Required empty public constructor
    }

    public static RareTrackerFragment newInstance() {
        RareTrackerFragment fragment = new RareTrackerFragment();
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
        return inflater.inflate(R.layout.fragment_rare_tracker, container, false);
    }

}
