package com.prashant.stockmarketadviser.activity.fragment;

import static com.prashant.stockmarketadviser.firebase.Constant.ACHIEVED_IMG_URL;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.adapter.MyAdapter;
import com.prashant.stockmarketadviser.adapter.ReverseLinearLayoutManager;
import com.prashant.stockmarketadviser.databinding.FragmentPrimeBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.ScripModel;
import com.prashant.stockmarketadviser.activity.admin.PaymentPageActivity;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.LocalPreference;
import com.prashant.stockmarketadviser.util.MyDialog;
import com.prashant.stockmarketadviser.util.VUtil;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class PrimeFragment extends Fragment {

    private Context mContext;

    FragmentPrimeBinding bind;

    FirebaseRecyclerAdapter<ScripModel, MyAdapter.MyHolder> adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = FragmentPrimeBinding.inflate(inflater, container, false);

        AuthManager.userChecker(mContext);

        if (AuthManager.isSubscribed()) {
            getPrimeScrips();
        } else {
            bind.primeScripRecyclerview.setVisibility(View.GONE);
            bind.notPrime.notPrimeView.setVisibility(View.VISIBLE);
            bind.notPrime.getPrimeBtn.setOnClickListener(view -> startActivity(new Intent(mContext, PaymentPageActivity.class)));
        }

        return bind.getRoot();
    }

    private void getPrimeScrips() {
        if (bind == null) {
            return;
        }

        bind.primeScripRecyclerview.setVisibility(View.VISIBLE);


        VUtil.EmptyViewHandler(Constant.scripDB.child("Prime"), bind.view.empty, bind.progressBar.show);


        bind.primeScripRecyclerview.setLayoutManager(new ReverseLinearLayoutManager(mContext));

        FirebaseRecyclerOptions<ScripModel> options = new FirebaseRecyclerOptions.Builder<ScripModel>().setQuery(Constant.scripDB.child("Prime"), ScripModel.class).build();
        adapter = new FirebaseRecyclerAdapter<ScripModel, MyAdapter.MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyAdapter.MyHolder holder, int position, @NonNull ScripModel model) {

                try {
                    if (holder.itemView == null) {
                        return;
                    }

                    TextView stockName = holder.itemView.findViewById(R.id.stock_name);
                    TextView stockType = holder.itemView.findViewById(R.id.stock_type);
                    TextView t1 = holder.itemView.findViewById(R.id.T1);
                    TextView t2 = holder.itemView.findViewById(R.id.T2);
                    TextView t3 = holder.itemView.findViewById(R.id.T3);
                    TextView sl = holder.itemView.findViewById(R.id.stopLoss);
                    TextView stockTime = holder.itemView.findViewById(R.id.update_time);
                    TextView targetStatus = holder.itemView.findViewById(R.id.target_status);

                    ImageView sl_img = holder.itemView.findViewById(R.id.sl_img);
                    ImageView t1_img = holder.itemView.findViewById(R.id.t1_img);
                    ImageView t2_img = holder.itemView.findViewById(R.id.t2_img);
                    ImageView t3_img = holder.itemView.findViewById(R.id.t3_img);

                    stockName.setText(model.getScripName());
                    stockType.setText(model.getScripType());
                    stockTime.setText(model.getTime());
                    t1.setText(model.getFirstTarget());
                    t2.setText(model.getSecondTarget());
                    t3.setText(model.getThirdTarget());
                    sl.setText(model.getStopLoss());

                    String currentFirstTargetImg = model.getFirstTargetStatusImage();
                    String currentSecondTargetImg = model.getSecondTargetStatusImage();
                    String currentThirdTargetImg = model.getThirdTargetStatusImage();
                    String currentStopLossImg = model.getStopLossStatusImage();

                    Picasso.get().load(currentFirstTargetImg).placeholder(R.drawable.placeholder).into(t1_img);
                    Picasso.get().load(currentSecondTargetImg).placeholder(R.drawable.placeholder).into(t2_img);
                    Picasso.get().load(currentThirdTargetImg).placeholder(R.drawable.placeholder).into(t3_img);
                    Picasso.get().load(currentStopLossImg).placeholder(R.drawable.placeholder).into(sl_img);

                    String targetStatusText = "Waiting";

                    if (VUtil.extractDate(model.getTime()).equals(VUtil.getDateAndDay())){
                        holder.itemView.findViewById(R.id.newTag).setVisibility(View.VISIBLE);
                    }


                    if (model.getFirstTargetStatusImage().equals(ACHIEVED_IMG_URL)) {
                        targetStatusText = "1st Target hit";
                        targetStatus.setTextColor(ContextCompat.getColor(mContext, R.color.green));

                    }

                    if (model.getSecondTargetStatusImage().equals(ACHIEVED_IMG_URL)) {
                        targetStatusText = "2nd Target hit";
                        targetStatus.setTextColor(ContextCompat.getColor(mContext, R.color.green));


                    }

                    if (model.getThirdTargetStatusImage().equals(ACHIEVED_IMG_URL)) {
                        targetStatusText = "All Target hit";
                        targetStatus.setTextColor(ContextCompat.getColor(mContext, R.color.green));

                    }

                    if (model.getStopLossStatusImage().equals(ACHIEVED_IMG_URL)) {
                        targetStatusText = "Stop Loss hit";
                        targetStatus.setTextColor(ContextCompat.getColor(mContext, R.color.red));

                    }

                    targetStatus.setText(targetStatusText);

                    if (AuthManager.userChecker(mContext).getUserType().equals("admin")) {
                        String[] ImageUrls = {Constant.WAITING_IMG_URL, ACHIEVED_IMG_URL};

                        setupImageCycler(sl_img, ImageUrls, model, "stopLossStatusImage");
                        setupImageCycler(t1_img, ImageUrls, model, "firstTargetStatusImage");
                        setupImageCycler(t2_img, ImageUrls, model, "secondTargetStatusImage");
                        setupImageCycler(t3_img, ImageUrls, model, "thirdTargetStatusImage");

                        holder.itemView.setOnLongClickListener(view -> {
                            Vibrator vibrator = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                            if (vibrator != null && vibrator.hasVibrator()) {
                                vibrator.vibrate(90);
                            }

                            MyDialog dialog = new MyDialog(view.getContext(), R.layout.cl_alert);
                            TextView alert = dialog.getView().findViewById(R.id.alert_text);
                            MaterialButton yes = dialog.getView().findViewById(R.id.yes_btn);
                            MaterialButton no = dialog.getView().findViewById(R.id.no_btn);
                            alert.setText("Are you sure want to delete this Scrip !");

                            yes.setOnClickListener(view1 -> {
                                CProgressDialog.mShow(view1.getContext());
                                dialog.dismiss();
                                Constant.scripDB.child("Prime").child(model.getUid()).removeValue().addOnCompleteListener(task -> {
                                    CProgressDialog.mDismiss();
                                    VUtil.showSuccessToast(view1.getContext(), "Scrip Deleted");

                                }).addOnFailureListener(e -> {
                                    CProgressDialog.mDismiss();
                                    VUtil.showErrorToast(view1.getContext(), e.getMessage());
                                });


                            });

                            no.setOnClickListener(view12 -> dialog.dismiss());

                            dialog.show();
                            return false;
                        });


                    }


                }catch (Exception e){

                }

            }

            @NonNull
            @Override
            public MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_scrip_layout, parent, false);
                return new MyAdapter.MyHolder(itemView);
            }
        };
        bind.primeScripRecyclerview.setAdapter(adapter);
    }

    private void setupImageCycler(ImageView imageView, String[] imageUrls, ScripModel model, String key) {
        final int[] currentStatusIndex = {0};
        imageView.setOnClickListener(view -> {
            currentStatusIndex[0] = (currentStatusIndex[0] + 1) % imageUrls.length;
            String newImageUrl = imageUrls[currentStatusIndex[0]];

            DatabaseReference scripRef = Constant.scripDB.child("Prime").child(model.getUid());
            Map<String, Object> updates = new HashMap<>();
            updates.put(key, newImageUrl);
            scripRef.updateChildren(updates);

            Vibrator vibrator = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(50);
            }


        });
    }


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