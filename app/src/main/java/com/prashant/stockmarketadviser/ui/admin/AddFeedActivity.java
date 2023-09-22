package com.prashant.stockmarketadviser.ui.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.databinding.ActivityAddFeedBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.firebase.NotificationSender;
import com.prashant.stockmarketadviser.model.FeedModel;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.VUtil;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddFeedActivity extends AppCompatActivity {

    ActivityAddFeedBinding bind;
    String uFeedId, uFeedDesc, feedDescription;

    private static final int PICK_IMAGE_REQUEST = 1;
    Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityAddFeedBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        bind.back.setOnClickListener(view -> finish());

        FeedModel model = (FeedModel) getIntent().getSerializableExtra("model");
        if (model != null) {
            updateFeed(model);
        }else{
            newFeed();
        }

    }

    private void newFeed() {
        bind.feedPostBtn.setOnClickListener(view -> {
            CProgressDialog.mShow(AddFeedActivity.this);

            feedDescription = String.valueOf(bind.feedDetail.getText());

            if (feedDescription.isEmpty() || selectedImageUri == null) {
                VUtil.showWarning(AddFeedActivity.this, "Please add something !");
                CProgressDialog.mDismiss();
            } else {

               uploadImageToFirebaseStorage(selectedImageUri);

            }
        });

        bind.feedImageAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    private void updateFeed(FeedModel model) {
        bind.actionBarTitle.setText(getString(R.string.update_feed));
        bind.feedPostBtn.setText(getString(R.string.update_now));
        bind.feedDetail.setText(model.getFeedDescription());

        if (model.getFeedImageUrl()!=null && !model.getFeedImageUrl().isEmpty()){
            bind.feedImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(model.getFeedImageUrl()).placeholder(R.drawable.image_placeholder_2).into(bind.feedImageView);
        }
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                bind.feedImageView.setVisibility(View.VISIBLE);
                bind.feedImageView.setImageURI(selectedImageUri);
            }else {
                bind.feedImageView.setVisibility(View.GONE);
            }
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        String uid = AuthManager.getCurrentUser().getUid();
        String feedId = Constant.feedDB.push().getKey();
        FeedModel model = new FeedModel();
        model.setFeedDescription(feedDescription);
        model.setTime(VUtil.getCurrentDateTimeFormatted());
        model.setPostedBy("Admin");
        model.setUserUid(uid);
        model.setFeedId(feedId);


        if (imageUri == null) {
            uploadData(model, feedId);
        }else{
            uploadDataToWithImage(model, feedId, imageUri);

        }


    }

    private void uploadDataToWithImage(FeedModel model, String feedId, Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("Feed/" + UUID.randomUUID().toString());

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                if (feedId != null) {

                    model.setFeedImageUrl(uri.toString());

                    Constant.feedDB.child(feedId).setValue(model).addOnCompleteListener(task -> {
                        CProgressDialog.mDismiss();

                        if (task.isSuccessful()) {
                            NotificationSender.sendNotificationToTopic(AddFeedActivity.this, Constant.All_NOTIFICATION_TOPIC, getString(R.string.new_feed), feedDescription);
                            VUtil.showSuccessToast(AddFeedActivity.this, getString(R.string.feed_posted));
                            finish();
                        }
                    }).addOnFailureListener(e -> {
                        CProgressDialog.mDismiss();
                        VUtil.showErrorToast(AddFeedActivity.this, e.getMessage());
                    });
                }
            });
        }).addOnFailureListener(e -> {
            CProgressDialog.mDismiss();

            VUtil.showErrorToast(AddFeedActivity.this, e.getMessage());
        });
    }

    private void uploadData(FeedModel model, String feedId) {
        Constant.feedDB.child(feedId).setValue(model).addOnCompleteListener(task -> {
            CProgressDialog.mDismiss();

            if (task.isSuccessful()) {
                NotificationSender.sendNotificationToTopic(AddFeedActivity.this, Constant.All_NOTIFICATION_TOPIC, getString(R.string.new_feed), feedDescription);
                VUtil.showSuccessToast(AddFeedActivity.this, getString(R.string.feed_posted));
                finish();
            }
        }).addOnFailureListener(e -> {
            CProgressDialog.mDismiss();
            VUtil.showErrorToast(AddFeedActivity.this, e.getMessage());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CProgressDialog.mDismiss();
    }
}
