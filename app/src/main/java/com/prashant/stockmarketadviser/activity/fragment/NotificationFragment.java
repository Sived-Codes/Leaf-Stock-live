package com.prashant.stockmarketadviser.activity.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.adapter.MyAdapter;
import com.prashant.stockmarketadviser.adapter.ReverseLinearLayoutManager;
import com.prashant.stockmarketadviser.databinding.FragmentNotificationBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.NotificationModel;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.MyDialog;
import com.prashant.stockmarketadviser.util.VUtil;

public class NotificationFragment extends Fragment {

    FragmentNotificationBinding bind;
    private FirebaseRecyclerAdapter<NotificationModel, MyAdapter.MyHolder> adapter;


    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = FragmentNotificationBinding.inflate(inflater, container, false);
        AuthManager.userChecker(mContext);
        getNotification();
        return bind.getRoot();
    }

    private void getNotification() {

        bind.notificationRecyclerview.setLayoutManager(new ReverseLinearLayoutManager(mContext));

        VUtil.EmptyViewHandler(Constant.notificationDB, bind.view.empty, bind.progressBar.show);

        FirebaseRecyclerOptions<NotificationModel> options = new FirebaseRecyclerOptions.Builder<NotificationModel>().setQuery(Constant.notificationDB, NotificationModel.class).build();

        adapter = new FirebaseRecyclerAdapter<NotificationModel, MyAdapter.MyHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull MyAdapter.MyHolder holder, int position, @NonNull NotificationModel model) {
                try {
                    if (holder.itemView != null) {
                        TextView title = holder.itemView.findViewById(R.id.notification_title);
                        TextView detail = holder.itemView.findViewById(R.id.notification_detail);
                        TextView time = holder.itemView.findViewById(R.id.notification_time);
                        MaterialButton delete = holder.itemView.findViewById(R.id.deleteBtn);

                        LinearLayout action = holder.itemView.findViewById(R.id.actionLayout);

                        if (!AuthManager.isAdmin()) {
                            action.setVisibility(View.GONE);
                        }

                        title.setText(model.getTitle());
                        detail.setText(model.getDescription());
                        time.setText(model.getTime());

                        delete.setOnClickListener(view -> {

                            MyDialog dialog = new MyDialog(view.getContext(), R.layout.cl_alert);
                            TextView alert = dialog.getView().findViewById(R.id.alert_text);
                            MaterialButton yes = dialog.getView().findViewById(R.id.yes_btn);
                            MaterialButton no = dialog.getView().findViewById(R.id.no_btn);


                            alert.setText("Are you sure want to delete this Notification !");
                            yes.setOnClickListener(view1 -> {
                                CProgressDialog.mShow(view1.getContext());
                                dialog.dismiss();
                                Constant.notificationDB.child(model.getUid()).removeValue().addOnCompleteListener(task -> {
                                    CProgressDialog.mDismiss();
                                    VUtil.showSuccessToast(view1.getContext(), "Notification Deleted");

                                }).addOnFailureListener(e -> {
                                    CProgressDialog.mDismiss();
                                    VUtil.showErrorToast(view1.getContext(), e.getMessage());
                                });


                            });

                            no.setOnClickListener(view12 -> dialog.dismiss());

                            dialog.show();
                        });

                    }
                } catch (Exception e) {
                    Log.e("YourTag", "An error occurred", e);
                }
            }

            @NonNull
            @Override
            public MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_notification, parent, false);
                return new MyAdapter.MyHolder(itemView);
            }
        };
        bind.notificationRecyclerview.setAdapter(adapter);
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