package io.phoenyx.tapstreak;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.phoenyx.tapstreak.jsonmodels.Friend;
import io.phoenyx.tapstreak.jsonmodels.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendsActivity extends AppCompatActivity {

    TapstreakService service;
    String userID, accessToken;
    ArrayList<Friend> friends;
    FriendsAdapter friendsAdapter;
    ListView friendsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Bundle extras = getIntent().getExtras();
        userID = extras.getString("user_id");
        accessToken = extras.getString("access_token");

        service = RetrofitClient.getClient(getResources().getString(R.string.api_base_url)).create(TapstreakService.class);

        friendsListView = (ListView) findViewById(R.id.friendsListView);

        refreshFriendsAdapter();

        friendsListView.setAdapter(friendsAdapter);

        /*
        //NFC
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) return;  // NFC not available on this device
        try {
            NdefMessage ndefMessage = new NdefMessage(intToByteArray(Integer.parseInt(userID)));
            nfcAdapter.setNdefPushMessage(ndefMessage, this);
        } catch (FormatException e) {
            e.printStackTrace();
        }

        //QR
        FloatingActionButton qrFAB = (FloatingActionButton) findViewById(R.id.qrFAB);

        qrFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder qrDialogBuilder = new AlertDialog.Builder(FriendsActivity.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View qrCodeView = layoutInflater.inflate(R.layout.qr_dialog, null);
                qrDialogBuilder.setView(qrCodeView);

                ImageView qrImageView = (ImageView) qrCodeView.findViewById(R.id.qrImageView);

                try {
                    qrImageView.setImageBitmap(createBarcodeBitmap(userID));
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                qrDialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                qrDialogBuilder.setNeutralButton("Scan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new IntentIntegrator(FriendsActivity.this).initiateScan();
                    }
                });
            }
        });
        */

    }

    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }


    private Bitmap createBarcodeBitmap(String data) throws WriterException {
        Writer writer = new QRCodeWriter();
        String finaldata = Uri.encode(data, "utf-8");

        BitMatrix bm = writer.encode(finaldata, BarcodeFormat.QR_CODE, 150, 150);
        Bitmap ImageBitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < 150; i++) {//width
            for (int j = 0; j < 150; j++) {//height
                ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK: Color.WHITE);
            }
        }

        return ImageBitmap;
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                int friendID = Integer.parseInt(result.getContents());
                int positionOfFriend = getPositionOfFriend(friends, friendID);


                if (positionOfFriend == -1) {
                    service.createFriends(userID, Integer.toString(friendID));
                    refreshFriendsAdapter();
                    friendsListView.setAdapter(friendsAdapter);
                } else {
                    int elapsedMillis = (int) (Calendar.getInstance().getTimeInMillis() - friends.get(positionOfFriend).getLastStreak());
                    int elapsedMins = elapsedMillis / 60000;

                    if (elapsedMins >= 1080 && elapsedMins <= 1440) {
                        service.refreshStreak(userID, Integer.toString(friendID));
                        refreshFriendsAdapter();
                        friendsListView.setAdapter(friendsAdapter);
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private int getPositionOfFriend(ArrayList<Friend> friends, int friendID) {
        for (int i = 0; i < friends.size(); i++) {
            if (Integer.parseInt(friends.get(i).getFriendId()) == friendID) return i;
        }
        return -1;
    }
    */

    private void refreshFriendsAdapter() {
        service.getUserInternal(userID, accessToken).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if (user.getRespCode().equals("100")) {
                    List<Friend> friends = user.getFriends();
                    friendsAdapter = new FriendsAdapter(FriendsActivity.this, R.layout.friend_row, friends);
                } else {
                    //TODO: sum shit happen, log user out lmao
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Something went wrong :(", Snackbar.LENGTH_SHORT).show();
            }
        });

    }
}
