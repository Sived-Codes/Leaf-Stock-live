package com.prashant.stockmarketadviser.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.adapter.MyAdapter;
import com.prashant.stockmarketadviser.adapter.ReverseLinearLayoutManager;
import com.prashant.stockmarketadviser.databinding.FragmentPastPerformanceBinding;
import com.prashant.stockmarketadviser.firebase.AuthManager;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.model.PerformanceModel;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.MyDialog;
import com.prashant.stockmarketadviser.util.PerformanceGenerator;
import com.prashant.stockmarketadviser.util.VUtil;


public class PastPerformanceFragment extends Fragment {

    FragmentPastPerformanceBinding bind;
    private Context mContext;

    private FirebaseRecyclerAdapter<PerformanceModel, MyAdapter.MyHolder> adapter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = FragmentPastPerformanceBinding.inflate(inflater, container, false);

        AuthManager.adminChecker(bind.adminPastPerformance);
        AuthManager.userChecker(mContext);
        getPerformance();
        return bind.getRoot();
    }

    private void getPerformance() {
        bind.PastPerformanceRecyclerview.setLayoutManager(new ReverseLinearLayoutManager(mContext));

        VUtil.EmptyViewHandler(Constant.performanceDB, bind.view.empty, bind.progressBar.show);

        FirebaseRecyclerOptions<PerformanceModel> options = new FirebaseRecyclerOptions.Builder<PerformanceModel>().setQuery(Constant.performanceDB, PerformanceModel.class).build();

        adapter = new FirebaseRecyclerAdapter<PerformanceModel, MyAdapter.MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyAdapter.MyHolder holder, int position, @NonNull PerformanceModel model) {

                if (holder.itemView != null) {
                    TextView firstProfit = holder.itemView.findViewById(R.id.pf_first_profit);
                    TextView secondProfit = holder.itemView.findViewById(R.id.pf_second_profit);
                    TextView thirdProfit = holder.itemView.findViewById(R.id.pf_third_profit);
                    TextView performanceOfTheDay = holder.itemView.findViewById(R.id.pf_day);
                    TextView tip = holder.itemView.findViewById(R.id.pf_tip);
                    MaterialButton delete = holder.itemView.findViewById(R.id.deleteBtn);
                    MaterialButton download = holder.itemView.findViewById(R.id.downloadBtn);
                    RelativeLayout performanceView = holder.itemView.findViewById(R.id.performanceView);

                    if (AuthManager.isAdmin()) {
                        delete.setVisibility(View.VISIBLE);
                    }


                    performanceOfTheDay.setText(model.getPerformanceOfTheDay());
                    firstProfit.setText(model.getFirstProfit());
                    secondProfit.setText(model.getSecondProfit());
                    thirdProfit.setText(model.getThirdProfit());
                    tip.setText(model.getTip());

                    delete.setOnClickListener(view -> {
                        MyDialog dialog = new MyDialog(view.getContext(), R.layout.cl_alert);
                        TextView alert = dialog.getView().findViewById(R.id.alert_text);
                        MaterialButton yes = dialog.getView().findViewById(R.id.yes_btn);
                        MaterialButton no = dialog.getView().findViewById(R.id.no_btn);
                        alert.setText("Are you sure want to delete this performance !");

                        yes.setOnClickListener(view1 -> {
                            CProgressDialog.mShow(view1.getContext());
                            dialog.dismiss();
                            Constant.performanceDB.child(model.getUid()).removeValue().addOnCompleteListener(task -> {
                                CProgressDialog.mDismiss();
                                VUtil.showSuccessToast(view1.getContext(), "Performance Deleted");

                            }).addOnFailureListener(e -> {
                                CProgressDialog.mDismiss();
                                VUtil.showErrorToast(view1.getContext(), e.getMessage());
                            });


                        });

                        no.setOnClickListener(view12 -> dialog.dismiss());

                        dialog.show();
                    });

                    download.setOnClickListener(view -> PerformanceGenerator.generatePerformance(view.getContext(), performanceView));

                }
            }

            @NonNull
            @Override
            public MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_performance_layout, parent, false);
                return new MyAdapter.MyHolder(itemView);
            }
        };
        bind.PastPerformanceRecyclerview.setAdapter(adapter);
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