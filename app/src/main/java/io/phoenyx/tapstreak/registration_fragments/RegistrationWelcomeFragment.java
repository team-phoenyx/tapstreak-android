package io.phoenyx.tapstreak.registration_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import io.phoenyx.tapstreak.R;
import io.phoenyx.tapstreak.RegistrationViewPager;
import io.phoenyx.tapstreak.SwipeDirection;

/**
 * Created by Terrance on 7/12/2017.
 */

public class RegistrationWelcomeFragment extends Fragment {

    EditText usernameEditText;
    RegistrationViewPager parentViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_register_welcome, container, false);

        return viewGroup;
    }
}
