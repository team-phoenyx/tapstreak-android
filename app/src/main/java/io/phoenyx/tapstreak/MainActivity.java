package io.phoenyx.tapstreak;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.List;

import io.phoenyx.tapstreak.json_models.Friend;
import io.phoenyx.tapstreak.json_models.ResponseCode;
import io.phoenyx.tapstreak.json_models.User;
import io.phoenyx.tapstreak.main_fragments.FriendsFragment;
import io.phoenyx.tapstreak.main_fragments.QRNFCFragment;
import io.phoenyx.tapstreak.main_fragments.StreaksFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    SwipeViewPager viewPager;
    PagerAdapter pagerAdapter;
    String userID, accessToken, username;
    List<Friend> friends;
    Handler qrHandler;
    Thread generateQRThread;
    Bitmap qrBitmap, lastQRBitmap;
    long lastRefreshTime;
    boolean isQRGenerationRunning = true;

    TapstreakService service;

    ListView streaksListView;
    TextView lonelyTextView;
    ImageView qrImageView;
    ProgressBar qrNFCLoadingProgressCircle;
    ProgressBar qrRefreshProgressBar;

    private static final int NUM_PAGES = 3;

    private static final int QR_NFC_FRAGMENT_TAG = 0;
    private static final int STREAKS_FRAGMENT_TAG = 1;
    private static final int FRIENDS_FRAGMENT_TAG = 2;
    
    private static final int QR_INTERVAL = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        userID = extras.getString("user_id");
        accessToken = extras.getString("access_token");
        username = extras.getString("username");

        service = RetrofitClient.getClient(getResources().getString(R.string.api_base_url)).create(TapstreakService.class);

        viewPager = findViewById(R.id.pager);
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(STREAKS_FRAGMENT_TAG);
        viewPager.setAllowedSwipeDirection(SwipeDirection.all);
        
        qrHandler = new Handler();
        
        generateQRThread = new Thread(new Runnable() {

            @Override
            public void run() {
                if (!isQRGenerationRunning) return;
                while (qrImageView == null) {
                    //waits until qrImageView is populated
                }
                if (qrBitmap == null) {
                    qrBitmap = createBarcodeBitmap(Long.toString(System.currentTimeMillis() + QR_INTERVAL, 16) + ":" + userID);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qrRefreshProgressBar.setProgress(100);
                        qrImageView.setImageBitmap(qrBitmap);
                        lastQRBitmap = qrBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        if (qrNFCLoadingProgressCircle != null) {
                            qrNFCLoadingProgressCircle.setVisibility(View.INVISIBLE);
                        }
                        ObjectAnimator animation = ObjectAnimator.ofInt(qrRefreshProgressBar, "progress", 0);
                        animation.setDuration(QR_INTERVAL); // 0.5 second
                        animation.setInterpolator(new LinearInterpolator());
                        lastRefreshTime = System.currentTimeMillis();
                        animation.start();
                    }
                });
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        qrBitmap = createBarcodeBitmap(Long.toString(System.currentTimeMillis() + QR_INTERVAL, 16) + ":" + userID);
                    }
                }).start();
                qrHandler.postDelayed(this, QR_INTERVAL);
            }
        });

        generateQRThread.start();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case QR_NFC_FRAGMENT_TAG:
                        View qrnfcView = ((QRNFCFragment) pagerAdapter.instantiateItem(viewPager, QR_NFC_FRAGMENT_TAG)).getView();
                        qrImageView = qrnfcView.findViewById(R.id.qr_imageview);
                        qrNFCLoadingProgressCircle = qrnfcView.findViewById(R.id.qr_nfc_loading_progresscircle);
                        qrRefreshProgressBar = qrnfcView.findViewById(R.id.qr_refresh_progressbar);
                        
                        if (qrBitmap == null) qrNFCLoadingProgressCircle.setVisibility(View.VISIBLE);

                        /* PUT IN ANOTHER THREAD
                        //NFC
                        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
                        if (nfcAdapter == null) return;  // NFC not available on this device
                        try {
                            NdefMessage ndefMessage = new NdefMessage(intToByteArray(Integer.parseInt(userID)));
                            nfcAdapter.setNdefPushMessage(ndefMessage, this);
                        } catch (FormatException e) {
                            e.printStackTrace();
                        }
                        */

                        if (lastQRBitmap != null) {
                            qrRefreshProgressBar.setProgress(((int) (QR_INTERVAL - (System.currentTimeMillis() - lastRefreshTime))) / 600);
                            qrImageView.setImageBitmap(lastQRBitmap);
                            qrNFCLoadingProgressCircle.setVisibility(View.INVISIBLE);
                            ObjectAnimator animation = ObjectAnimator.ofInt(qrRefreshProgressBar, "progress", 0);
                            animation.setDuration(QR_INTERVAL - (System.currentTimeMillis() - lastRefreshTime)); // However much time is left in the cycle
                            animation.setInterpolator(new LinearInterpolator());
                            animation.start();
                        } else {
                            qrNFCLoadingProgressCircle.setVisibility(View.VISIBLE);
                        }
                        break;
                    case STREAKS_FRAGMENT_TAG:
                        View streaksView = ((StreaksFragment) pagerAdapter.instantiateItem(viewPager, STREAKS_FRAGMENT_TAG)).getView();
                        initStreaksView(streaksView);

                        break;
                    case FRIENDS_FRAGMENT_TAG:

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void initQRNFCView(View qrNFCView) {
        qrImageView = qrNFCView.findViewById(R.id.qr_imageview);
        qrNFCLoadingProgressCircle = qrNFCView.findViewById(R.id.qr_nfc_loading_progresscircle);
        qrRefreshProgressBar = qrNFCView.findViewById(R.id.qr_refresh_progressbar);
    }

    public void initStreaksView(View streaksView) {
        streaksListView = streaksView.findViewById(R.id.streaks_listview);
        lonelyTextView = streaksView.findViewById(R.id.lonely_textview);
        FloatingActionButton cameraFAB = streaksView.findViewById(R.id.scanner_fab);
        ImageButton settingsButton = streaksView.findViewById(R.id.settings_button);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                Bundle settingsBundle = getIntent().getExtras();
                settingsIntent.putExtras(settingsBundle);
                startActivity(settingsIntent);
            }
        });

        cameraFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(MainActivity.this).setBeepEnabled(false).initiateScan();
            }
        });

        refreshFriendsAndStreaks();
    }

    private Bitmap createBarcodeBitmap(String data) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            int size = (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.7);
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            int backgroundColor = getColor(R.color.background);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : backgroundColor);
                }
            }
            return bmp;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * In this case, handles response from QR scanner activity
     * @param requestCode Request identifier from calling Intent
     * @param resultCode Result of the separate activity
     * @param data Data sent by separate activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 49374) { //Specific requestCode of ZXing scanner
            if (resultCode == RESULT_OK) { //Check that result was OK
                String qrString = data.getStringExtra("SCAN_RESULT"); //Gets raw string data from scan

                //Parse data to timestamp + userID
                String[] params = qrString.split(":");

                if (params.length != 2) { //Terminate if there are more than 2 parameters
                    refreshFriendsAndStreaks();
                    Snackbar.make(findViewById(android.R.id.content), "Something went wrong :(", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                long qrMillis = Long.parseLong(params[0], 16);
                long currentMillis = System.currentTimeMillis();
                if (qrMillis > currentMillis || currentMillis - qrMillis > 60000) { //Terminate if QR code is expired
                    refreshFriendsAndStreaks();
                    Snackbar.make(findViewById(android.R.id.content), "QR Code expired", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                String friendID = params[1];

                if (friendExists(friendID)) {
                    service.refreshStreak(userID, accessToken, friendID).enqueue(new Callback<ResponseCode>() {
                        @Override
                        public void onResponse(Call<ResponseCode> call, Response<ResponseCode> response) {
                            refreshFriendsAndStreaks();
                        }

                        @Override
                        public void onFailure(Call<ResponseCode> call, Throwable t) {
                            Snackbar.make(findViewById(android.R.id.content), "Failed to refresh streak", Snackbar.LENGTH_SHORT).show();
                            refreshFriendsAndStreaks();
                        }
                    });
                } else {
                    service.addFriend(userID, accessToken, friendID).enqueue(new Callback<ResponseCode>() {
                        @Override
                        public void onResponse(Call<ResponseCode> call, Response<ResponseCode> response) {
                            refreshFriendsAndStreaks();
                        }

                        @Override
                        public void onFailure(Call<ResponseCode> call, Throwable t) {
                            Snackbar.make(findViewById(android.R.id.content), "Failed to add friend", Snackbar.LENGTH_SHORT).show();
                            refreshFriendsAndStreaks();
                        }
                    });

                }
                //TODO ADD FRIEND
            } else if (resultCode == RESULT_CANCELED) {
                refreshFriendsAndStreaks();
            }
            refreshFriendsAndStreaks();
        }


    }

    /**
     * Checks if another user with id = friendID already exists in the user's friend list
     * @param friendID
     * @return true if another user with id = friendID exists in user's friend list, false if not
     */
    private boolean friendExists(String friendID) {
        if (friends == null || friends.size() == 0) return false;
        for (Friend friend : friends) {
            if (friend.getId().equals(friendID)) return true;
        }
        return false;
    }

    /**
     * Pulls new user information from API, and updates the streaks and friends fragment listviews
     */
    private void refreshFriendsAndStreaks() {
        service.getUserInternal(userID, accessToken).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if (user.getRespCode() == null) { //Checks if response is successful; resp_code should be null
                    friends = user.getFriends();
                    //TODO SORT FRIENDS BY URGENCY, THEN STREAK COUNT DESC

                    //Update streaksListView with new user streak data
                    StreaksAdapter streaksAdapter = new StreaksAdapter(MainActivity.this, R.layout.friend_row, friends);
                    if (friends.size() == 0) {
                        lonelyTextView.setVisibility(View.VISIBLE);
                    } else {
                        lonelyTextView.setVisibility(View.INVISIBLE);
                    }
                    streaksListView.setAdapter(streaksAdapter);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Something went wrong :(", Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        isQRGenerationRunning = true; //Activate generateQRThread
        refreshFriendsAndStreaks(); //Update user streaks and friends

        //If last QR code is expired, restart generateQRThread to generate a new one immediately
        if (QR_INTERVAL - (System.currentTimeMillis() - lastRefreshTime) < 1 && lastRefreshTime != 0) {
            //Reset qrBitmap, lastQRBitmap, and qrImageView
            qrBitmap = null;
            lastQRBitmap = null;
            qrImageView.setImageBitmap(null);
            qrNFCLoadingProgressCircle.setVisibility(View.VISIBLE);

            //Restart generateQRThread, start new instance of generateQRThread without delay
            qrHandler.removeCallbacks(generateQRThread);
            qrHandler.post(generateQRThread);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isQRGenerationRunning = false; //Deactivate generateQRThread as to not waste system resources
    }

    /**
     * PagerAdapter to facilitate the page changes between QR/NFC, Streaks, and Friends fragments
     */
    private class MainPagerAdapter extends FragmentStatePagerAdapter {

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case QR_NFC_FRAGMENT_TAG:
                    return new QRNFCFragment();
                case STREAKS_FRAGMENT_TAG:
                    return new StreaksFragment();
                case FRIENDS_FRAGMENT_TAG:
                    return new FriendsFragment();
                default:
                    return new StreaksFragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
