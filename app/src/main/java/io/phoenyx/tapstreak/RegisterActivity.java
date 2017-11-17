package io.phoenyx.tapstreak;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.text.TextWatcher;
import android.text.Editable;

import java.util.Timer;
import java.util.TimerTask;

import io.phoenyx.tapstreak.json_models.Authentication;
import io.phoenyx.tapstreak.json_models.ResponseCode;
import io.phoenyx.tapstreak.registration_fragments.RegistrationConfirmPasswordFragment;
import io.phoenyx.tapstreak.registration_fragments.RegistrationPasswordFragment;
import io.phoenyx.tapstreak.registration_fragments.RegistrationUsernameFragment;
import io.phoenyx.tapstreak.registration_fragments.RegistrationWelcomeFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    SwipeViewPager viewPager;
    PagerAdapter pagerAdapter;
    String username, password;
    boolean isPasswordFragmentSelected;

    private Timer timer;
    private final long DELAY = 500; // in ms

    TapstreakService service;

    private static final int NUM_PAGES = 4;

    private static final int USERNAME_FRAGMENT_TAG = 0;
    private static final int PASSWORD_FRAGMENT_TAG = 1;
    private static final int CONFIRM_PASSWORD_FRAGMENT_TAG = 2;
    private static final int WELCOME_FRAGMENT_TAG = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        service = RetrofitClient.getClient(getResources().getString(R.string.api_base_url)).create(TapstreakService.class);

        viewPager = findViewById(R.id.pager);
        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);
        pagerAdapter = new RegisterPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setAllowedSwipeDirection(SwipeDirection.none);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case USERNAME_FRAGMENT_TAG:
                        isPasswordFragmentSelected = false;
                        View usernameView = ((RegistrationUsernameFragment) pagerAdapter.instantiateItem(viewPager, USERNAME_FRAGMENT_TAG)).getView();
                        initUsernameFragment(usernameView);
                        /*
                        View usernameView = ((RegistrationUsernameFragment) pagerAdapter.instantiateItem(viewPager, USERNAME_FRAGMENT_TAG)).getView();
                        viewPager.setAllowedSwipeDirection(SwipeDirection.none);
                        if (usernameView != null) {
                            EditText usernameEditText = usernameView.findViewById(R.id.username_edittext);
                            if (!usernameEditText.getText().toString().isEmpty()) {
                                viewPager.setAllowedSwipeDirection(SwipeDirection.right);
                            }
                            usernameEditText.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    
                                }
                    
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (s.length() > 0) {
                                        viewPager.setAllowedSwipeDirection(SwipeDirection.right);
                                    } else {
                                        viewPager.setAllowedSwipeDirection(SwipeDirection.none);
                                    }
                                }
                    
                                @Override
                                public void afterTextChanged(Editable s) {
                    
                                }
                            });

                        }
                        */
                        break;
                    case PASSWORD_FRAGMENT_TAG:
                        isPasswordFragmentSelected = true;
                        usernameView = ((RegistrationUsernameFragment) pagerAdapter.instantiateItem(viewPager, USERNAME_FRAGMENT_TAG)).getView();
                        View passwordView = ((RegistrationPasswordFragment) pagerAdapter.instantiateItem(viewPager, PASSWORD_FRAGMENT_TAG)).getView();
                        
                        viewPager.setAllowedSwipeDirection(SwipeDirection.left);
                        if (usernameView != null) {
                            EditText usernameEditText = usernameView.findViewById(R.id.username_edittext);
                            username = usernameEditText.getText().toString();
                        }
                        if (passwordView != null) {
                            EditText passwordEditText = passwordView.findViewById(R.id.password_edittext);
                            if (!passwordEditText.getText().toString().isEmpty()) {
                                viewPager.setAllowedSwipeDirection(SwipeDirection.all);
                            }
                            passwordEditText.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    
                                }
                    
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (s.length() > 0) {
                                        viewPager.setAllowedSwipeDirection(SwipeDirection.all);
                                    } else {
                                        viewPager.setAllowedSwipeDirection(SwipeDirection.left);
                                    }
                                }
                    
                                @Override
                                public void afterTextChanged(Editable s) {
                    
                                }
                            });
                        }
                        break;
                    case CONFIRM_PASSWORD_FRAGMENT_TAG:
                        isPasswordFragmentSelected = false;
                        passwordView = ((RegistrationPasswordFragment) pagerAdapter.instantiateItem(viewPager, PASSWORD_FRAGMENT_TAG)).getView();
                        View confirmPasswordView = ((RegistrationConfirmPasswordFragment) pagerAdapter.instantiateItem(viewPager, CONFIRM_PASSWORD_FRAGMENT_TAG)).getView();
                        
                        viewPager.setAllowedSwipeDirection(SwipeDirection.left);
                        if (passwordView != null) {
                            EditText passwordEditText = passwordView.findViewById(R.id.password_edittext);
                            password = passwordEditText.getText().toString();
                        }
                        if (confirmPasswordView != null) {
                            EditText confirmPasswordEditText = confirmPasswordView.findViewById(R.id.confirm_password_edittext);
                            if (confirmPasswordEditText.getText().toString().equals(password)) {
                                viewPager.setAllowedSwipeDirection(SwipeDirection.all);
                            }
                            confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    
                                }
                    
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (s.length() > 0 && password.equals(s.toString())) {
                                        viewPager.setAllowedSwipeDirection(SwipeDirection.all);
                                    } else {
                                        viewPager.setAllowedSwipeDirection(SwipeDirection.left);
                                    }
                                }
                    
                                @Override
                                public void afterTextChanged(Editable s) {
                    
                                }
                            });
                        }
                        break;
                    case WELCOME_FRAGMENT_TAG:
                        isPasswordFragmentSelected = false;
                        viewPager.setAllowedSwipeDirection(SwipeDirection.none);
                        //Hide soft keyboard
                        View view = getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        //Register user in another thread
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                View welcomeView = ((RegistrationWelcomeFragment) pagerAdapter.instantiateItem(viewPager, WELCOME_FRAGMENT_TAG)).getView();
                                Button getStartedButton = welcomeView.findViewById(R.id.get_started_button);
                                TextView statusLabel = welcomeView.findViewById(R.id.status_label);
                                ProgressBar registrationProgressCircle = welcomeView.findViewById(R.id.registration_progresscircle);
                                registerUser(username, password, getStartedButton, statusLabel, registrationProgressCircle);
                            }
                        }).start();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void registerUser(final String username, final String password, final Button getStartedButton, final TextView statusLabel, final ProgressBar registrationProgressCircle) {
        byte[] salt = PasswordManager.getNextSalt();
        byte[] passHashed = PasswordManager.hash(password.toCharArray(), salt);

        String dbSalt = Base64.encodeToString(salt, Base64.NO_WRAP);
        String dbHashedPass = Base64.encodeToString(passHashed, Base64.NO_WRAP);

        dbSalt = dbSalt.replace('/', '-');
        dbHashedPass = dbHashedPass.replace('/','-');

        service.registerUser(username, dbHashedPass, dbSalt).enqueue(new Callback<Authentication>() {
            @Override
            public void onResponse(Call<Authentication> call, Response<Authentication> response) {
                final Authentication authentication = response.body();

                if (!authentication.getRespCode().equals("100") || authentication.getUserId().isEmpty() || authentication.getAccessToken().isEmpty()) {
                    registrationProgressCircle.setVisibility(View.INVISIBLE);
                    getStartedButton.setText("try again?");
                    statusLabel.setText("something went wrong");
                    getStartedButton.setVisibility(View.VISIBLE);
                    statusLabel.setVisibility(View.VISIBLE);

                    getStartedButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                            startActivity(registerIntent);
                            finish();
                        }
                    });
                } else {
                    registrationProgressCircle.setVisibility(View.INVISIBLE);
                    getStartedButton.setVisibility(View.VISIBLE);
                    statusLabel.setVisibility(View.VISIBLE);

                    SharedPreferences sharedPreferences = getSharedPreferences("io.phoenyx.tapstreak", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("user_id", authentication.getUserId());
                    editor.putString("access_token", authentication.getAccessToken());
                    editor.apply();

                    getStartedButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                            mainIntent.putExtra("user_id", authentication.getUserId());
                            mainIntent.putExtra("access_token", authentication.getAccessToken());
                            startActivity(mainIntent);

                            finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Authentication> call, Throwable t) {
                registrationProgressCircle.setVisibility(View.INVISIBLE);
                getStartedButton.setText("try again?");
                statusLabel.setText("something went wrong");
                getStartedButton.setVisibility(View.VISIBLE);
                statusLabel.setVisibility(View.VISIBLE);

                getStartedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivity(registerIntent);
                        finish();
                    }
                });
            }
        });
    }

    public void initUsernameFragment(View usernameView) {
        if (usernameView != null) {
            final EditText usernameEditText = usernameView.findViewById(R.id.username_edittext);
            final ProgressBar checkUNProgressCircle = usernameView.findViewById(R.id.check_username_progresscircle);
            final ImageView uniqueUNImageView = usernameView.findViewById(R.id.username_unique_imageview);
            final TextView usernameTakenLabel = usernameView.findViewById(R.id.username_taken_label);

            if (!usernameEditText.getText().toString().isEmpty()) {
                viewPager.setAllowedSwipeDirection(SwipeDirection.all);
            }

            if (isPasswordFragmentSelected) return;
            usernameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (timer != null) timer.cancel();
                    viewPager.setAllowedSwipeDirection(SwipeDirection.none);
                    usernameTakenLabel.setVisibility(View.INVISIBLE);
                    usernameEditText.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                    uniqueUNImageView.setVisibility(View.INVISIBLE);
                    if (s.length() <= 0) {
                        checkUNProgressCircle.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(final Editable s) {
                    if (s.length() <= 0) return;
                    if (checkUNProgressCircle.getVisibility() != View.VISIBLE) checkUNProgressCircle.setVisibility(View.VISIBLE);
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    uniqueUNImageView.setVisibility(View.INVISIBLE);
                                }
                            });
                            service.checkUsernameExists(s.toString()).enqueue(new Callback<ResponseCode>() {
                                @Override
                                public void onResponse(Call<ResponseCode> call, final Response<ResponseCode> response) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (response.body().getRespCode().equals("100")) {
                                                uniqueUNImageView.setVisibility(View.VISIBLE);
                                                viewPager.setAllowedSwipeDirection(SwipeDirection.all);
                                            } else if (response.body().getRespCode().equals("2")) {
                                                usernameTakenLabel.setVisibility(View.VISIBLE);
                                                usernameEditText.getBackground().setColorFilter(getResources().getColor(R.color.edittext_error), PorterDuff.Mode.SRC_ATOP);
                                                viewPager.setAllowedSwipeDirection(SwipeDirection.none);
                                            } else {
                                                Snackbar.make(findViewById(android.R.id.content), "Something went wrong :(", Snackbar.LENGTH_LONG).show();
                                                viewPager.setAllowedSwipeDirection(SwipeDirection.none);
                                            }
                                            checkUNProgressCircle.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<ResponseCode> call, Throwable t) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            checkUNProgressCircle.setVisibility(View.INVISIBLE);
                                            viewPager.setAllowedSwipeDirection(SwipeDirection.none);
                                        }
                                    });
                                }
                            });
                        }
                    }, DELAY);
                }
            });

        }
    }

    private class RegisterPagerAdapter extends FragmentStatePagerAdapter {
        public RegisterPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case USERNAME_FRAGMENT_TAG:
                    return new RegistrationUsernameFragment();
                case PASSWORD_FRAGMENT_TAG:
                    return new RegistrationPasswordFragment();
                case CONFIRM_PASSWORD_FRAGMENT_TAG:
                    return new RegistrationConfirmPasswordFragment();
                case WELCOME_FRAGMENT_TAG:
                    return new RegistrationWelcomeFragment();
                default:
                    return new RegistrationUsernameFragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}