package com.prashant.stockmarketadviser.ui.admin;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.databinding.ActivityTipGenBinding;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.firebase.NotificationSender;
import com.prashant.stockmarketadviser.firebase.StockDatabase;
import com.prashant.stockmarketadviser.firebase.TipGenValueCallback;
import com.prashant.stockmarketadviser.model.PerformanceModel;
import com.prashant.stockmarketadviser.model.ScripModel;
import com.prashant.stockmarketadviser.model.TipGenValueModel;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.LocalPreference;
import com.prashant.stockmarketadviser.util.MyDialog;
import com.prashant.stockmarketadviser.util.PerformanceGenerator;
import com.prashant.stockmarketadviser.util.PopupMenuHelper;
import com.prashant.stockmarketadviser.util.VUtil;

public class TipGenActivity extends AppCompatActivity implements PerformanceGenerator.OnUploadCompleteListener {

    ActivityTipGenBinding bind;
    String stopLoss, firstTarget, secondTarget, thirdTarget;
    String tip, first_target_achieved, second_target_achieved, third_target_achieved;
    String first_profit_booked, second_profit_booked, third_profit_booked;

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    MyDialog performanceGenDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityTipGenBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        getLocalGeneratedTip();

        bind.back.setOnClickListener(view -> finish());

        bind.moreBtn.setOnClickListener(view -> {
            PopupMenuHelper popupMenuHelper = new PopupMenuHelper(TipGenActivity.this, bind.moreBtn, R.layout.menu_tip_gen);

            popupMenuHelper.getView().findViewById(R.id.edit_sell_value).setOnClickListener(view12 -> {
                openTipGenValueEditorDialog("Sell");
                popupMenuHelper.dismiss();
            });

            popupMenuHelper.getView().findViewById(R.id.edit_buy_value).setOnClickListener(view1 -> {
                openTipGenValueEditorDialog("Buy");
                popupMenuHelper.dismiss();

            });


            popupMenuHelper.getView().findViewById(R.id.generate_performance).setOnClickListener(view1 -> {
                generatePerformance();
                popupMenuHelper.dismiss();

            });

            popupMenuHelper.show();

        });

        bind.generatedTipClearBtn.setOnClickListener(view -> {
            LocalPreference.clearStoredTipData(TipGenActivity.this);
            bind.generatedTip.setText("");
            bind.genRate.setText("");
            bind.genScriptName.setText("");
            bind.generatedFirstProfit.setText("");
            bind.generatedSecondProfit.setText("");
            bind.generatedThirdProfit.setText("");
            bind.generatedFirstTarget.setText("");
            bind.generatedSecondTarget.setText("");
            bind.generatedThirdTarget.setText("");

        });

        bind.generateBuyTipsBtn.setOnClickListener(view -> {
            String rate = bind.genRate.getText().toString();
            String scripName = bind.genScriptName.getText().toString();

            if (rate.isEmpty()) {
                bind.genRate.requestFocus();
                bind.genRate.setError("Empty");
            } else if (scripName.isEmpty()) {
                bind.genScriptName.requestFocus();
                bind.genScriptName.setError("Empty");
            } else {
                new GenerateTipsAsyncTask(rate, scripName, "Buy").execute();
            }


        });

        bind.generateSellTipsBtn.setOnClickListener(view -> {
            String rate = bind.genRate.getText().toString();
            String scripName = bind.genScriptName.getText().toString();

            if (rate.isEmpty()) {
                bind.genRate.requestFocus();
                bind.genRate.setError("Empty");
            } else if (scripName.isEmpty()) {
                bind.genScriptName.requestFocus();
                bind.genScriptName.setError("Empty");
            } else {
                new GenerateTipsAsyncTask(rate, scripName, "Sell").execute();
            }


        });


        String[] suggestions = {"Nifty 50 ", "Bank Nifty ", "Finifty "};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions);

        bind.genScriptName.setAdapter(adapter);

        setupCopyButton(bind.copyFirstProfitBtn, bind.generatedFirstProfit);
        setupCopyButton(bind.copySecondProfitBtn, bind.generatedSecondProfit);
        setupCopyButton(bind.copyThirdProfitBtn, bind.generatedThirdProfit);
        setupCopyButton(bind.copyFirstTargetBtn, bind.generatedFirstTarget);
        setupCopyButton(bind.copySecondTargetBtn, bind.generatedSecondTarget);
        setupCopyButton(bind.copyThirdTargetBtn, bind.generatedThirdTarget);
        setupCopyButton(bind.copyTipBtn, bind.generatedTip);


    }


    private void getLocalGeneratedTip() {
        TipGenValueModel model = LocalPreference.getStoredTipData(TipGenActivity.this);

        bind.generatedTip.setText(getNonEmptyString(model.getTip()));
        bind.genRate.setText(getNonEmptyString(model.getRate()));
        bind.genScriptName.setText(getNonEmptyString(model.getScripName()));

        bind.generatedFirstProfit.setText(getNonEmptyString(model.getFirstProfit()));
        bind.generatedSecondProfit.setText(getNonEmptyString(model.getSecondProfit()));
        bind.generatedThirdProfit.setText(getNonEmptyString(model.getThirdProfit()));

        bind.generatedFirstTarget.setText(getNonEmptyString(model.getFirstTarget()));
        bind.generatedSecondTarget.setText(getNonEmptyString(model.getSecondTarget()));
        bind.generatedThirdTarget.setText(getNonEmptyString(model.getThirdTarget()));
    }

    private String getNonEmptyString(String value) {
        return value != null ? value : "";
    }


    private void setupCopyButton(ImageView copyButton, EditText editText) {
        copyButton.setOnClickListener(view -> VUtil.copyText(TipGenActivity.this, editText));
    }

    @Override
    public void onUploadSuccess(String downloadUrl) {


    }

    @Override
    public void onUploadFailed() {
        performanceGenDialog.show();
    }


    @SuppressLint("StaticFieldLeak")
    private class GenerateTipsAsyncTask extends AsyncTask<Void, Void, TipGenValueModel> {
        private final String rate;
        private final String scripName;
        private final String type;

        public GenerateTipsAsyncTask(String rate, String scripName, String type) {
            this.rate = rate;
            this.scripName = scripName;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CProgressDialog.mShow(TipGenActivity.this);
        }

        @Override
        protected TipGenValueModel doInBackground(Void... voids) {
            fetchValueFromDatabase(rate, scripName, type);
            return null;
        }
    }

    private void fetchValueFromDatabase(String rate, String scripName, String type) {
        StockDatabase.getTipGenBuyValue(type, new TipGenValueCallback() {
            @Override
            public void onTipGenValueFetched(TipGenValueModel model) {
                processGeneratedTips(model, rate, scripName, type);
            }

            @Override
            public void onFailure(String errorMessage) {
                CProgressDialog.mDismiss();
                Toast.makeText(TipGenActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processGeneratedTips(TipGenValueModel model, String rate, String scripName, String type) {

        if (model != null) {

            String quantity = model.getQuantity();
            String lotSize = model.getLotSize();
            double t1 = Double.parseDouble(model.getFirstTarget());
            double t2 = Double.parseDouble(model.getSecondTarget());
            double t3 = Double.parseDouble(model.getThirdTarget());
            double sl = Double.parseDouble(model.getStopLoss());
            double lot = Double.parseDouble(lotSize);
            double qty = Double.parseDouble(quantity);
            double buy = Double.parseDouble(rate);

            if (type.equals("Buy")) {
                double sla = buy * sl;
                double truncated = buy - sla;
                double firstTG = buy * t1;
                double secondTG = firstTG * t2;
                double thirdTG = secondTG * t3;

                double firstPB = firstTG - buy;
                int value = (int) firstPB;
                String ff_1profit = String.valueOf(value * qty);

                double secPB = secondTG - buy;
                int value2 = (int) secPB;
                String ss_2profit = String.valueOf(value2 * qty);

                double thirdPB = thirdTG - buy;
                int value3 = (int) thirdPB;
                String tt_3profit = String.valueOf(value3 * qty);

                stopLoss = String.valueOf((int) truncated);
                firstTarget = String.valueOf((int) firstTG);
                secondTarget = String.valueOf((int) secondTG);
                thirdTarget = String.valueOf((int) thirdTG);

                tip = "Intraday Option Tips: " + " scrip name " + scripName + " Buy " + lotSize + " lot or " + quantity + " Qty @ " + rate + " TRG’S " + firstTarget + "-" + secondTarget + "-" + thirdTarget + " SL " + stopLoss;
                first_target_achieved = "Intraday " + firstTarget + " 1st Target achieved in " + scripName + " 2nd TRG is " + secondTarget + ", 3rd TRG is " + thirdTarget + " our subscribed members buying price is " + rate;
                second_target_achieved = "Intraday " + secondTarget + " 2nd Target achieved in " + scripName + " 3rd TRG is " + thirdTarget;
                third_target_achieved = "Intraday " + thirdTarget + " 3rd Target achieved in " + scripName + " booked " + quantity + " Qty profit is ";

                first_profit_booked = "Our subscribed members total profit booked Rs " + ff_1profit + " in " + scripName + " Qty " + quantity + " buying price and selling price is " + rate + " to " + firstTarget;
                second_profit_booked = "Our subscribed members total profit booked Rs " + ss_2profit + " in " + scripName + " Qty " + quantity + " buying price and selling price is " + rate + " to " + secondTarget;
                third_profit_booked = "Our subscribed members total profit booked Rs " + tt_3profit + " in " + scripName + " Qty " + quantity + " buying price and selling price is " + rate + " to " + thirdTarget;

                bind.generatedTip.setText(tip);

                bind.generatedFirstProfit.setText(first_profit_booked);
                bind.generatedSecondProfit.setText(second_profit_booked);
                bind.generatedThirdProfit.setText(third_profit_booked);

                bind.generatedFirstTarget.setText(first_target_achieved);
                bind.generatedSecondTarget.setText(second_target_achieved);
                bind.generatedThirdTarget.setText(third_target_achieved);


                CProgressDialog.mDismiss();
                Toast.makeText(this, "Buy Tip Generated", Toast.LENGTH_SHORT).show();

            } else if (type.equals("Sell")) {
                String ulv = String.valueOf(t1);
                String tg2_sell = String.valueOf(t2);
                String tg3_sell = String.valueOf(t3);
                String tgsl_sell = String.valueOf(sl);
                String lotSize_sell = String.valueOf(lot);
                String quantity_sell = String.valueOf(qty);

                double ul_sell = Double.parseDouble(ulv);
                double t2_sell = Double.parseDouble(tg2_sell);
                double t3_sell = Double.parseDouble(tg3_sell);
                double sl_sell = Double.parseDouble(tgsl_sell);
                double qty_sell = Double.parseDouble(quantity_sell);
                double buy_sell = Double.parseDouble(rate);

                double ab_sell = buy_sell * ul_sell;
                double truncated_sell = buy_sell - ab_sell;
                String atValue_sell = String.valueOf((int) truncated_sell);

                double sla_sell = buy_sell * sl_sell;
                double sc_sell = buy_sell - sla_sell;
                String stoploss_sell = String.valueOf((int) sc_sell);

                double secondTG_sell = truncated_sell * t2_sell;
                String secondTarget_sell = String.valueOf((int) secondTG_sell);

                double thirdtg = secondTG_sell * t3_sell;
                String thirdTarget_sell = String.valueOf((int) thirdtg);

                int value = (int) (buy_sell - truncated_sell);
                int ncv = value + 1;
                String fp1 = String.valueOf(ncv * qty_sell);

                double pb2 = secondTG_sell - truncated_sell;
                int value2 = (int) pb2;
                int ncv2 = value2 + 1;
                String fp2 = String.valueOf(ncv2 * qty_sell);

                double newData3 = thirdtg - truncated_sell;
                int value3 = (int) newData3;
                int ncv3 = value3 + 1;
                String fp3 = String.valueOf(ncv3 * qty_sell);

                tip = "Intraday Option Tips: " + " scrip name " + scripName + " Buy " + lotSize_sell + " lot or " + quantity_sell + " Qty @ " + atValue_sell + " TRG’S " + rate + "-" + secondTarget_sell + "-" + thirdTarget_sell + " SL " + stoploss_sell;
                first_target_achieved = "Intraday " + rate + " 1st Target achieved. And 2nd TRG is " + secondTarget_sell + ", 3rd TRG is " + thirdTarget_sell + ". our subscribed members buying price is " + atValue_sell + " and SL " + stoploss_sell;
                second_target_achieved = "Intraday " + secondTarget_sell + " is achieved. And 3rd TRG is " + thirdTarget_sell;
                third_target_achieved = "Intraday " + thirdTarget_sell + " is achieved. And booked " + qty_sell + " Qty profit is " + fp3;

                first_profit_booked = "our subscribed members total profit booked rs " + fp1 + " in " + scripName + " Qty " + quantity_sell + " buying price and selling price is " + atValue_sell + " to " + rate;
                second_profit_booked = "our subscribed members total profit booked rs " + fp2 + " in " + scripName + " Qty " + quantity_sell + " buying price and selling price is " + atValue_sell + " to " + secondTarget_sell;
                third_profit_booked = "our subscribed members total profit booked rs " + fp3 + " in " + scripName + " Qty " + quantity_sell + " buying price and selling price is " + atValue_sell + " to " + thirdTarget_sell;

                firstTarget = rate;
                secondTarget = secondTarget_sell;
                thirdTarget = thirdTarget_sell;
                stopLoss = stoploss_sell;

                bind.generatedTip.setText(tip);

                bind.generatedFirstProfit.setText(first_profit_booked);
                bind.generatedSecondProfit.setText(second_profit_booked);
                bind.generatedThirdProfit.setText(third_profit_booked);

                bind.generatedFirstTarget.setText(first_target_achieved);
                bind.generatedSecondTarget.setText(second_target_achieved);
                bind.generatedThirdTarget.setText(third_target_achieved);
                CProgressDialog.mDismiss();
                Toast.makeText(this, "Sell Tip Generated", Toast.LENGTH_SHORT).show();
            }

            String tipMessage = "Intraday Option Tips for " + scripName + ":\n" + "---------------------------------\n" + "Type: " + type + "\n" + "Lot Size: " + lotSize + " lots\n" + "Quantity: " + quantity + " Qty\n" + "Targets: " + firstTarget + ", " + secondTarget + ", " + thirdTarget + "\n" + "Stop Loss: " + stopLoss + "\n" + "Trade Strategy: " + (type.equals("Buy") ? "Buy" : "Sell") + " " + scripName + " at " + firstTarget + " with a stop loss at " + stopLoss + ". Place targets at " + firstTarget + ", " + secondTarget + ", and " + thirdTarget + ".\n" + "Note: This is an intraday trading tip and not investment advice.";

            String uid = Constant.scripDB.child(type).push().getKey();
            ScripModel scripModel = new ScripModel();
            scripModel.setScripName(scripName);
            scripModel.setScripType(type);
            scripModel.setStopLoss(stopLoss);
            scripModel.setFirstTarget(firstTarget);
            scripModel.setSecondTarget(secondTarget);
            scripModel.setThirdTarget(thirdTarget);
            scripModel.setTime(VUtil.getCurrentDateTimeFormatted());

            scripModel.setFirstTargetStatusImage(Constant.WAITING_IMG_URL);
            scripModel.setSecondTargetStatusImage(Constant.WAITING_IMG_URL);
            scripModel.setThirdTargetStatusImage(Constant.WAITING_IMG_URL);
            scripModel.setStopLossStatusImage(Constant.WAITING_IMG_URL);
            scripModel.setUid(uid);

            LocalPreference.storeGeneratedTip(TipGenActivity.this, scripName, rate, tip, first_target_achieved, second_target_achieved, third_target_achieved, first_target_achieved, second_profit_booked, third_profit_booked);

            CProgressDialog.mDismiss();

            new Handler().postDelayed(() -> {
                MyDialog dialog = new MyDialog(TipGenActivity.this, R.layout.cl_generate_tip_dialog);
                MaterialButton prime = dialog.getView().findViewById(R.id.sendToPrime);
                MaterialButton trial = dialog.getView().findViewById(R.id.sendToTrial);
                MaterialButton generatePerformance = dialog.getView().findViewById(R.id.performanceBtn);


                prime.setOnClickListener(view -> sendScrip("Prime", scripModel, tipMessage, dialog));
                trial.setOnClickListener(view -> sendScrip("Trial", scripModel, tipMessage, dialog));
                generatePerformance.setOnClickListener(view -> {
                    dialog.dismiss();
                    generatePerformance();
                });


                dialog.show();

            }, 350);


        } else {
            CProgressDialog.mDismiss();
            Toast.makeText(TipGenActivity.this, "Getting null value!", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendScrip(String type, ScripModel scripModel, String tipMessage, MyDialog dialog) {

        if (type.equals("Prime"))
            NotificationSender.sendNotificationToTopic(TipGenActivity.this, Constant.PRIME_NOTIFICATION_TOPIC, "Check out new tips", tipMessage);

        else {
            NotificationSender.sendNotificationToTopic(TipGenActivity.this, Constant.TRIAL_NOTIFICATION_TOPIC, "Check out new tips", tipMessage);

        }
        Constant.scripDB.child(type).child(scripModel.getUid()).setValue(scripModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialog.dismiss();
                CProgressDialog.mDismiss();
                Toast.makeText(TipGenActivity.this, "Posted to " + type, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TipGenActivity.this, "Failed to post !", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void openTipGenValueEditorDialog(String type) {

        MyDialog dialog = new MyDialog(TipGenActivity.this, R.layout.cl_edit_tip_gen_value);

        //Edit text
        EditText firstTargetEd = dialog.getView().findViewById(R.id.tg_first_target_value);
        EditText secondTargetEd = dialog.getView().findViewById(R.id.tg_second_target_value);
        EditText thirdTargetEd = dialog.getView().findViewById(R.id.tg_third_target_value);

        EditText stopLossEd = dialog.getView().findViewById(R.id.tg_stop_loss_value);
        EditText lotSizeEd = dialog.getView().findViewById(R.id.tg_lot_size_value);
        EditText quantityEd = dialog.getView().findViewById(R.id.tg_quantity_value);

        //Material Button 
        MaterialButton setValueBtn = dialog.getView().findViewById(R.id.tg_set_btn_value);

        setValueBtn.setOnClickListener(view -> {

            //Get text from edittext text
            String firstTarget = firstTargetEd.getText().toString();
            String secondTarget = secondTargetEd.getText().toString();
            String thirdTarget = thirdTargetEd.getText().toString();

            String stopLoss = stopLossEd.getText().toString();
            String lotSize = lotSizeEd.getText().toString();
            String quantity = quantityEd.getText().toString();

            if (firstTarget.isEmpty()) {
                firstTargetEd.requestFocus();
                firstTargetEd.setError("Empty");
            } else if (secondTarget.isEmpty()) {
                secondTargetEd.requestFocus();
                secondTargetEd.setError("Empty");
            } else if (thirdTarget.isEmpty()) {
                thirdTargetEd.requestFocus();
                thirdTargetEd.setError("Empty");
            } else if (stopLoss.isEmpty()) {
                stopLossEd.requestFocus();
                stopLossEd.setError("Empty");
            } else if (quantity.isEmpty()) {
                quantityEd.requestFocus();
                quantityEd.setError("Empty");
            } else if (lotSize.isEmpty()) {
                lotSizeEd.requestFocus();
                lotSizeEd.setError("Empty");
            } else {
                CProgressDialog.mShow(TipGenActivity.this);
                TipGenValueModel model = new TipGenValueModel();
                model.setFirstTarget(firstTarget);
                model.setSecondTarget(secondTarget);
                model.setThirdTarget(thirdTarget);
                model.setLotSize(lotSize);
                model.setQuantity(quantity);
                model.setStopLoss(stopLoss);


                Constant.tipGenDB.child(type).setValue(model).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CProgressDialog.mDismiss();
                        Toast.makeText(TipGenActivity.this, "Value updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TipGenActivity.this, "Failed to update !", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(e -> Toast.makeText(TipGenActivity.this, "" + e, Toast.LENGTH_SHORT).show());
            }


        });

        CProgressDialog.mShow(TipGenActivity.this);

        StockDatabase.getTipGenBuyValue(type, new TipGenValueCallback() {
            @Override
            public void onTipGenValueFetched(TipGenValueModel model) {
                if (model != null) {
                    firstTargetEd.setText(model.getFirstTarget());
                    secondTargetEd.setText(model.getSecondTarget());
                    thirdTargetEd.setText(model.getThirdTarget());

                    stopLossEd.setText(model.getStopLoss());
                    quantityEd.setText(model.getQuantity());
                    lotSizeEd.setText(model.getLotSize());
                    CProgressDialog.mDismiss();

                } else {
                    CProgressDialog.mDismiss();
                    Toast.makeText(TipGenActivity.this, "getting null value !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                CProgressDialog.mDismiss();
                Toast.makeText(TipGenActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

    }

    private void generatePerformance() {
        performanceGenDialog = new MyDialog(TipGenActivity.this, R.layout.cl_performance_layout);
        TextView firstProfit = performanceGenDialog.getView().findViewById(R.id.pf_first_profit);
        TextView secondProfit = performanceGenDialog.getView().findViewById(R.id.pf_second_profit);
        TextView thirdProfit = performanceGenDialog.getView().findViewById(R.id.pf_third_profit);
        TextView performanceText = performanceGenDialog.getView().findViewById(R.id.pf_day);
        TextView tip = performanceGenDialog.getView().findViewById(R.id.pf_tip);

        MaterialButton downloadBtn = performanceGenDialog.getView().findViewById(R.id.downloadBtn);
        MaterialButton postBtn = performanceGenDialog.getView().findViewById(R.id.shareBtn);
        RelativeLayout performanceView = performanceGenDialog.getView().findViewById(R.id.performanceView);

        postBtn.setText("Send Past Performance");

        PerformanceModel model = new PerformanceModel();
        model.setPerformanceOfTheDay("Performance of the day " + VUtil.getDateAndDay());
        model.setTip(bind.generatedTip.getText().toString());
        model.setFirstProfit(bind.generatedFirstProfit.getText().toString());
        model.setSecondProfit(bind.generatedSecondProfit.getText().toString());
        model.setThirdProfit(bind.generatedThirdProfit.getText().toString());

        String uid = Constant.performanceDB.push().getKey();
        performanceText.setText("Performance of the day " + VUtil.getDateAndDay());
        firstProfit.setText(model.getFirstProfit());
        secondProfit.setText(model.getSecondProfit());
        thirdProfit.setText(model.getThirdProfit());
        tip.setText(model.getTip());
        model.setUid(uid);
        downloadBtn.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(TipGenActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(TipGenActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                return;
            }

            PerformanceGenerator.generatePerformance(TipGenActivity.this, performanceView);
        });

        postBtn.setOnClickListener(view -> {
            if (uid != null) {
                Constant.performanceDB.child(uid).setValue(model).addOnCompleteListener(task -> {
                    NotificationSender.sendNotificationToTopic(TipGenActivity.this, Constant.All_NOTIFICATION_TOPIC, model.getPerformanceOfTheDay(), model.getFirstProfit() + "\n" + "\n" + model.getSecondProfit() + "\n" + "\n" + model.getThirdProfit());

                    VUtil.showSuccessToast(TipGenActivity.this, "Performance posted");
                }).addOnFailureListener(e -> VUtil.showErrorToast(TipGenActivity.this, e.toString()));
            }
        });

        performanceGenDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, reattempt the download
            } else {
                // Permission denied, show a message or take appropriate action
                Toast.makeText(this, "Permission denied. Cannot download image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CProgressDialog.mDismiss();
    }


}