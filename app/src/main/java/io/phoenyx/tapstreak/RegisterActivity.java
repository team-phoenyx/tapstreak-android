package io.phoenyx.tapstreak;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;

import org.apache.http.auth.AUTH;
import org.w3c.dom.Text;

import io.phoenyx.tapstreak.json_models.Authentication;
import io.phoenyx.tapstreak.registration_fragments.RegistrationConfirmPasswordFragment;
import io.phoenyx.tapstreak.registration_fragments.RegistrationPasswordFragment;
import io.phoenyx.tapstreak.registration_fragments.RegistrationUsernameFragment;
import io.phoenyx.tapstreak.registration_fragments.RegistrationWelcomeFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    RegistrationViewPager viewPager;
    PagerAdapter pagerAdapter;
    String username, password;

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
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setAllowedSwipeDirection(SwipeDirection.none);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    //TODO: CHECK FOR FIELDS FILLED IN
                    case USERNAME_FRAGMENT_TAG:
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
                        break;
                    case PASSWORD_FRAGMENT_TAG:
                        usernameView = ((RegistrationUsernameFragment) pagerAdapter.instantiateItem(viewPager, USERNAME_FRAGMENT_TAG)).getView();
                        View passwordView = ((RegistrationPasswordFragment) pagerAdapter.instantiateItem(viewPager, PASSWORD_FRAGMENT_TAG)).getView();
                        
                        viewPager.setAllowedSwipeDirection(SwipeDirection.left);
                        if (usernameView != null) {
                            EditText usernameEditText = usernameView.findViewById(R.id.username_edittext);
                            username = usernameEditText.getText().toString();
                        }
                        if (passwordView != null) {
                            Toast.makeText(RegisterActivity.this, "password view not null", Toast.LENGTH_SHORT).show();
                            EditText passwordEditText = passwordView.findViewById(R.id.password_edittext);
                            if (!passwordEditText.getText().toString().isEmpty()) {
                                Toast.makeText(RegisterActivity.this, "password text not empty", Toast.LENGTH_SHORT).show();
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
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                View welcomeView = ((RegistrationWelcomeFragment) pagerAdapter.instantiateItem(viewPager, WELCOME_FRAGMENT_TAG)).getView();
                                Button getStartedButton = welcomeView.findViewById(R.id.get_started_button);
                                TextView statusLabel = welcomeView.findViewById(R.id.status_label);
                                registerUser(username, password, getStartedButton, statusLabel);
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

    private void registerUser(final String username, String password, final Button getStartedButton, final TextView statusLabel) {
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
                    getStartedButton.setVisibility(View.GONE);
                    statusLabel.setText("Your username is taken :(");
                    viewPager.setAllowedSwipeDirection(SwipeDirection.left);
                } else {
                    viewPager.setAllowedSwipeDirection(SwipeDirection.none);

                    SharedPreferences sharedPreferences = getSharedPreferences("io.phoenyx.tapstreak", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("user_id", authentication.getUserId());
                    editor.putString("access_token", authentication.getAccessToken());
                    editor.apply();

                    getStartedButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent friendsIntent = new Intent(getApplicationContext(), FriendsActivity.class);
                            friendsIntent.putExtra("user_id", authentication.getUserId());
                            friendsIntent.putExtra("access_token", authentication.getAccessToken());
                            startActivity(friendsIntent);

                            finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Authentication> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Something went wrong :(", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void initUsernameFragment(View usernameView) {
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
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
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