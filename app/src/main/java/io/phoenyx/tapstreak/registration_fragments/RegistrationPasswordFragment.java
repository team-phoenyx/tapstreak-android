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

public class RegistrationPasswordFragment extends Fragment {

    EditText passwordEditText;
    RegistrationViewPager parentViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_register_password, container, false);

        passwordEditText = (EditText) viewGroup.findViewById(R.id.password_edittext);

        parentViewPager = (RegistrationViewPager) getActivity().findViewById(R.id.pager);
        parentViewPager.setAllowedSwipeDirection(SwipeDirection.left);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    parentViewPager.setAllowedSwipeDirection(SwipeDirection.all);
                } else {
                    parentViewPager.setAllowedSwipeDirection(SwipeDirection.left);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return viewGroup;
    }
}
