package io.phoenyx.tapstreak.registration_fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.phoenyx.tapstreak.FriendsActivity;
import io.phoenyx.tapstreak.R;
import io.phoenyx.tapstreak.RegisterActivity;
import io.phoenyx.tapstreak.RegistrationViewPager;
import io.phoenyx.tapstreak.json_models.Authentication;

/**
 * Created by Terrance on 7/12/2017.
 */

public class RegistrationWelcomeFragment extends Fragment {

    Button getStartedButton;
    TextView statusTextView;
    RegistrationViewPager parentViewPager;
    public boolean isActive;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_register_welcome, container, false);

        getStartedButton = (Button) viewGroup.findViewById(R.id.get_started_button);
        statusTextView = (TextView) viewGroup.findViewById(R.id.status_label);

        return viewGroup;

    }

    @Override
    public void onResume() {
        super.onResume();


    }
}
