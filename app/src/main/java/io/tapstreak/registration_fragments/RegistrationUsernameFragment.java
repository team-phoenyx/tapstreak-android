package io.tapstreak.registration_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.tapstreak.R;
import io.tapstreak.RegisterActivity;

/**
 * Created by Terrance on 7/12/2017.
 */

public class RegistrationUsernameFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_register_username, container, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                RegisterActivity parent = (RegisterActivity) getActivity();
                parent.initUsernameFragment(viewGroup);
            }
        }).start();
        return viewGroup;
    }
}
