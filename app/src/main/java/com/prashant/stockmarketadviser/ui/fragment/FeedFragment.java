package com.prashant.stockmarketadviser.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.adapter.ShimmerAdapter;
import com.prashant.stockmarketadviser.databinding.FragmentFeedBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.FeedModel;
import com.prashant.stockmarketadviser.ui.admin.AddFeedActivity;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.LocalPreference;
import com.prashant.stockmarketadviser.util.MyAdapter;
import com.prashant.stockmarketadviser.util.VUtil;

import java.util.ArrayList;
import java.util.Collections;


public class FeedFragment extends Fragment {

    FragmentFeedBinding bind;

    ArrayList<FeedModel> feedList = new ArrayList<>();

    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentFeedBinding.inflate(inflater, container, false);

        if (LocalPreference.getUserType(mContext).equals("admin")){
            bind.addFeedBtn.setVisibility(View.VISIBLE);
        }

        //shimmer
        bind.shimmerRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        ShimmerAdapter shimmerAdapter = new ShimmerAdapter(R.layout.shimmer_feeds_layout);
        bind.shimmerRecyclerview.setAdapter(shimmerAdapter);


        setupListener();
        getFeeds();

        return bind.getRoot();
    }

    private void getFeeds() {
        if (bind == null) {
            return;
        }

        Constant.feedDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feedList.clear();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    FeedModel model = childSnapshot.getValue(FeedModel.class);
                    if (model != null) {
                        feedList.add(model);
                    }
                }


                Collections.reverse(feedList);

                Log.d("TAG", "onDataChange: " + feedList.toString());
                MyAdapter<FeedModel> adapter = new MyAdapter<>(feedList, new MyAdapter.OnBindInterface() {
                    @Override
                    public void onBindHolder(MyAdapter.MyHolder holder, int position) {
                        FeedModel model = feedList.get(position);

                        if (model != null) {

                            if (holder.itemView == null) {
                                return;
                            }


                            TextView postedBy = holder.itemView.findViewById(R.id.feeder_name);
                            TextView time = holder.itemView.findViewById(R.id.feed_time);
                            TextView feedDescription = holder.itemView.findViewById(R.id.feed_detail);

                            MaterialButton editFeedBtn = holder.itemView.findViewById(R.id.feed_edit_btn);
                            MaterialButton removeFeedBtn = holder.itemView.findViewById(R.id.feed_remove_btn);

                            LinearLayout feedActionLayout = holder.itemView.findViewById(R.id.feedActionLayout);

                            if (!LocalPreference.getUserType(mContext).equals("admin")){
                                feedActionLayout.setVisibility(View.GONE);
                            }

                            postedBy.setText(model.getPostedBy());
                            time.setText(model.getTime());
                            feedDescription.setText(model.getFeedDescription());

                            editFeedBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Intent intent = new Intent(mContext, AddFeedActivity.class);
                                    intent.putExtra("userId", model.getUserUid());
                                    intent.putExtra("feedId", model.getFeedId());
                                    intent.putExtra("feedDesc", model.getFeedDescription());
                                    intent.putExtra("feedTime", model.getTime());
                                    intent.putExtra("feedPostedBy", model.getPostedBy());
                                    startActivity(intent);
                                }
                            });


                            removeFeedBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    VUtil.showConfirmationDialog(mContext, "Are you sure want to delete this feed ?", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            CProgressDialog.mShow(mContext);

                                            Constant.feedDB.child(model.getFeedId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        CProgressDialog.mDismiss();
                                                        Toast.makeText(mContext, "Feed Deleted", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    CProgressDialog.mDismiss();
                                                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    }, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    });
                                }
                            });
                        }
                    }
                }, R.layout.cl_feeds_layout);
                bind.shimmerRecyclerview.setVisibility(View.GONE);
                bind.feedRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
                bind.feedRecyclerview.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListener() {
        bind.addFeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, AddFeedActivity.class));
            }
        });


    }


}