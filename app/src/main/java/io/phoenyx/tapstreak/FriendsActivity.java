package io.phoenyx.tapstreak;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

import io.phoenyx.tapstreak.json_models.Friend;
import io.phoenyx.tapstreak.json_models.ResponseCode;
import io.phoenyx.tapstreak.json_models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsActivity extends AppCompatActivity {

    TapstreakService service;
    String userID, accessToken;
    List<Friend> friends;
    FriendsAdapter friendsAdapter;
    ListView friendsListView;
    TextView lonelyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Bundle extras = getIntent().getExtras();
        userID = extras.getString("user_id");
        accessToken = extras.getString("access_token");

        service = RetrofitClient.getClient(getResources().getString(R.string.api_base_url)).create(TapstreakService.class);

        friendsListView = (ListView) findViewById(R.id.friendsListView);
        lonelyTextView = (TextView) findViewById(R.id.lonely_textview);

        refreshFriendsAdapter();

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
        */

        //QR
        FloatingActionButton qrFAB = (FloatingActionButton) findViewById(R.id.qrFAB);
        FloatingActionButton cameraFAB = (FloatingActionButton) findViewById(R.id.cameraFAB);

        qrFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder qrDialogBuilder = new AlertDialog.Builder(FriendsActivity.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View qrCodeView = layoutInflater.inflate(R.layout.qr_dialog, null);
                qrDialogBuilder.setView(qrCodeView);
                qrDialogBuilder.setCancelable(true);
                qrDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        refreshFriendsAdapter();
                    }
                });

                ImageView qrImageView = (ImageView) qrCodeView.findViewById(R.id.qrImageView);

                try {
                    //Appends the millis since epoch in hexadecimal to the userID
                    qrImageView.setImageBitmap(createBarcodeBitmap(Long.toString(System.currentTimeMillis(), 16) + ":" + userID));
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                qrDialogBuilder.create().show();
            }
        });

        cameraFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
                intent.setAction("com.google.zxing.client.android.SCAN");
                intent.putExtra("SAVE_HISTORY", false);
                startActivityForResult(intent, 0);
            }
        });
    }

    private Bitmap createBarcodeBitmap(String data) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String qrString = data.getStringExtra("SCAN_RESULT");
                String[] params = qrString.split(":");
                if (params.length != 2) {
                    refreshFriendsAdapter();
                    Snackbar.make(findViewById(android.R.id.content), "Something went wrong :(", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                
                long qrMillis = Long.parseLong(params[0], 16);
                long currentMillis = System.currentTimeMillis();
                if (qrMillis > currentMillis || currentMillis - qrMillis > 60000) {
                    refreshFriendsAdapter();
                    Snackbar.make(findViewById(android.R.id.content), "QR Code expired", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                String friendID = params[1];

                if (friendExists(friendID)) {
                    service.refreshStreak(userID, accessToken, friendID).enqueue(new Callback<ResponseCode>() {
                        @Override
                        public void onResponse(Call<ResponseCode> call, Response<ResponseCode> response) {
                            Snackbar.make(findViewById(android.R.id.content), response.body().getRespCode(), Snackbar.LENGTH_SHORT).show();
                            refreshFriendsAdapter();
                        }

                        @Override
                        public void onFailure(Call<ResponseCode> call, Throwable t) {
                            Snackbar.make(findViewById(android.R.id.content), "Failed to refresh streak", Snackbar.LENGTH_SHORT).show();
                            refreshFriendsAdapter();
                        }
                    });
                } else {
                    service.addFriend(userID, accessToken, friendID).enqueue(new Callback<ResponseCode>() {
                        @Override
                        public void onResponse(Call<ResponseCode> call, Response<ResponseCode> response) {
                            refreshFriendsAdapter();
                        }
    
                        @Override
                        public void onFailure(Call<ResponseCode> call, Throwable t) {
                            Snackbar.make(findViewById(android.R.id.content), "Failed to add friend", Snackbar.LENGTH_SHORT).show();
                            refreshFriendsAdapter();
                        }
                    });
                    
                }
                //TODO ADD FRIEND
            } else if (resultCode == RESULT_CANCELED) {
                refreshFriendsAdapter();
            }
        }
        refreshFriendsAdapter();

    }

    private boolean friendExists(String friendID) {
        if (friends == null || friends.size() == 0) return false;
        for (Friend friend : friends) {
            if (friend.getId().equals(friendID)) return true;
        }
        return false;
    }

    private void refreshFriendsAdapter() {
        service.getUserInternal(userID, accessToken).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if (user.getRespCode() == null) {
                    friends = user.getFriends();
                    friendsAdapter = new FriendsAdapter(FriendsActivity.this, R.layout.friend_row, friends);
                    if (friends.size() == 0) {
                        lonelyTextView.setVisibility(View.VISIBLE);
                    } else {
                        lonelyTextView.setVisibility(View.INVISIBLE);
                    }
                    friendsListView.setAdapter(friendsAdapter);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Something went wrong :(", Snackbar.LENGTH_SHORT).show();
            }
        });

    }
}
