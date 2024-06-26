package com.prashant.stockmarketadviser.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.activity.admin.AddFeedActivity;
import com.prashant.stockmarketadviser.adapter.MyAdapter;
import com.prashant.stockmarketadviser.adapter.ReverseLinearLayoutManager;
import com.prashant.stockmarketadviser.databinding.FragmentFeedBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.FeedModel;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.VUtil;
import com.squareup.picasso.Picasso;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding bind;
    private FirebaseRecyclerAdapter<FeedModel, MyAdapter.MyHolder> adapter;
    private Context mContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = FragmentFeedBinding.inflate(inflater, container, false);
        mContext = requireContext();

        initializeViews();
        setupListener();
        getFeeds();

        return bind.getRoot();
    }

    private void initializeViews() {



        if (AuthManager.userChecker(mContext).getUserType().equals("admin")) {
            bind.addFeedBtn.setVisibility(View.VISIBLE);
        }
        AuthManager.userChecker(mContext);

        bind.feedRecyclerview.setLayoutManager(new ReverseLinearLayoutManager(mContext));
    }

    private void setupListener() {
        bind.addFeedBtn.setOnClickListener(view -> startActivity(new Intent(mContext, AddFeedActivity.class)));
    }

    private void getFeeds() {
        VUtil.EmptyViewHandler(Constant.feedDB, bind.view.empty, bind.progressBar.show);

        FirebaseRecyclerOptions<FeedModel> options = new FirebaseRecyclerOptions.Builder<FeedModel>().setQuery(Constant.feedDB, FeedModel.class).build();

        adapter = new FirebaseRecyclerAdapter<FeedModel, MyAdapter.MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyAdapter.MyHolder holder, int position, @NonNull FeedModel model) {
                bindFeedData(holder, model, position);

            }

            @NonNull
            @Override
            public MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_feeds_layout, parent, false);
                return new MyAdapter.MyHolder(itemView);
            }
        };
        bind.feedRecyclerview.setAdapter(adapter);
    }

    private void bindFeedData(@NonNull MyAdapter.MyHolder holder, @NonNull FeedModel model, int position) {

        try {
            TextView time = holder.itemView.findViewById(R.id.feed_time);
            TextView feedDescription = holder.itemView.findViewById(R.id.feed_detail);
            ImageView imageView = holder.itemView.findViewById(R.id.feedImageView);
            RelativeLayout action = holder.itemView.findViewById(R.id.actionLayout);

            ImageView editFeedBtn = holder.itemView.findViewById(R.id.feed_edit_btn);
            ImageView removeFeedBtn = holder.itemView.findViewById(R.id.feed_remove_btn);

            if (model.getFeedImageUrl() != null || !model.getFeedImageUrl().equals("")) {
                imageView.setVisibility(View.VISIBLE);
                Picasso.get().load(model.getFeedImageUrl()).placeholder(R.drawable.place_holder).into(imageView);
            }

            if (!AuthManager.isAdmin()) {
                action.setVisibility(View.GONE);
            }
            time.setText(model.getTime());
            feedDescription.setText(model.getFeedDescription());

            editFeedBtn.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, AddFeedActivity.class);
                intent.putExtra("model", model);
                startActivity(intent);
            });

            removeFeedBtn.setOnClickListener(view -> VUtil.showConfirmationDialog(mContext, "Are you sure want to delete this feed ?", yes -> {
                CProgressDialog.mShow(mContext);
                Constant.feedDB.child(model.getFeedId()).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CProgressDialog.mDismiss();
                        adapter.notifyItemRemoved(position);
                        VUtil.showSuccessToast(mContext, "Feed Deleted");
                    }
                }).addOnFailureListener(e -> {
                    CProgressDialog.mDismiss();
                    VUtil.showErrorToast(mContext, e.getMessage());
                });
            }, view12 -> {

            }));
        }catch (Exception e){

        }



    }


    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }

    }
}
