package io.phoenyx.tapstreak.main_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.phoenyx.tapstreak.MainActivity;
import io.phoenyx.tapstreak.R;
import io.phoenyx.tapstreak.RegisterActivity;

public class StreaksFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_streaks, container, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                MainActivity parent = (MainActivity) getActivity();
                parent.initStreaksView(viewGroup);
            }
        }).start();

        return viewGroup;
    }
}