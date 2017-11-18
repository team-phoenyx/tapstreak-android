package io.phoenyx.tapstreak.main_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.phoenyx.tapstreak.MainActivity;
import io.phoenyx.tapstreak.R;

/**
 * Created by Terrance on 7/12/2017.
 */

public class QRNFCFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_qr_nfc, container, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                MainActivity parent = (MainActivity) getActivity();
                parent.initQRNFCView(viewGroup);
            }
        }).start();

        return viewGroup;
    }
}
