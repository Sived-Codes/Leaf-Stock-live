package com.prashant.stockmarketadviser.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.databinding.ActivityAddFeedBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.firebase.NotificationSender;
import com.prashant.stockmarketadviser.model.FeedModel;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.VUtil;

import java.util.HashMap;
import java.util.Map;

public class AddFeedActivity extends AppCompatActivity {

    ActivityAddFeedBinding bind;
    String uFeedId, uFeedDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityAddFeedBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        bind.back.setOnClickListener(view -> finish());

        Intent intent = getIntent();
        if (intent != null) {
            uFeedId = intent.getStringExtra("feedId");
            uFeedDesc = intent.getStringExtra("feedDesc");

            if (uFeedId != null && !uFeedId.equals("")) {
                updateFeed();
            } else {
                newFeed();
            }

        }

    }

    private void newFeed() {
        bind.feedPostBtn.setOnClickListener(view -> {
            CProgressDialog.mShow(AddFeedActivity.this);

            String feedDescription = String.valueOf(bind.feedDetail.getText());

            if (feedDescription.isEmpty()) {
                bind.feedDetail.requestFocus();
                bind.feedDetail.setText(getString(R.string.write_something));
                CProgressDialog.mDismiss();
            } else {
                String uid = AuthManager.getCurrentUser().getUid();
                String feedId = Constant.feedDB.push().getKey();
                FeedModel model = new FeedModel();
                model.setFeedDescription(feedDescription);
                model.setTime(VUtil.getCurrentDateTimeFormatted());
                model.setPostedBy("Admin");
                model.setUserUid(uid);
                model.setFeedId(feedId);

                if (feedId != null) {
                    Constant.feedDB.child(feedId).setValue(model).addOnCompleteListener(task -> {
                        CProgressDialog.mDismiss();

                        if (task.isSuccessful()) {
                            NotificationSender.sendNotificationToTopic(AddFeedActivity.this, Constant.All_NOTIFICATION_TOPIC, getString(R.string.new_feed), feedDescription);
                            Toast.makeText(AddFeedActivity.this, getString(R.string.feed_posted), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddFeedActivity.this, getString(R.string.failed_to_post), Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(e -> {
                        CProgressDialog.mDismiss();
                        Toast.makeText(AddFeedActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    });
                }

            }
        });

    }

    private void updateFeed() {
        bind.actionBarTitle.setText(getString(R.string.update_feed));
        bind.feedPostBtn.setText(getString(R.string.update_now));
        bind.feedDetail.setText(uFeedDesc);
        bind.feedPostBtn.setOnClickListener(view -> {
            String updatedFeedDesc = bind.feedDetail.getText().toString();

            if (updatedFeedDesc.isEmpty()) {
                bind.feedDetail.requestFocus();
                bind.feedDetail.setText(getString(R.string.write_something));
                CProgressDialog.mDismiss();
            } else {
                Map<String, Object> updates = new HashMap<>();
                updates.put("feedDescription", updatedFeedDesc);

                Constant.feedDB.child(uFeedId).updateChildren(updates)
                        .addOnCompleteListener(task -> {
                            CProgressDialog.mDismiss();

                            if (task.isSuccessful()) {
                                Toast.makeText(AddFeedActivity.this, getString(R.string.feed_updated), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddFeedActivity.this, getString(R.string.failed_to_update), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            CProgressDialog.mDismiss();
                            Toast.makeText(AddFeedActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CProgressDialog.mDismiss();
    }
}
