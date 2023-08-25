package com.prashant.stockmarketadviser.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.prashant.stockmarketadviser.R;
import com.prashant.stockmarketadviser.databinding.ActivityTipGenBinding;
import com.prashant.stockmarketadviser.firebase.Constant;
import com.prashant.stockmarketadviser.firebase.StockDatabase;
import com.prashant.stockmarketadviser.firebase.TipGenValueCallback;
import com.prashant.stockmarketadviser.model.TipGenValueModel;
import com.prashant.stockmarketadviser.util.CProgressDialog;
import com.prashant.stockmarketadviser.util.MyDialog;
import com.prashant.stockmarketadviser.util.PopupMenuHelper;

public class TipGenActivity extends AppCompatActivity {

    ActivityTipGenBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityTipGenBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        bind.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenuHelper popupMenuHelper = new PopupMenuHelper(TipGenActivity.this, bind.moreBtn, R.layout.menu_tip_gen);

                popupMenuHelper.getView().findViewById(R.id.edit_sell_value).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openTipGenValueEditorDialog("sellValue");
                        popupMenuHelper.dismiss();
                    }
                });

                popupMenuHelper.getView().findViewById(R.id.edit_buy_value).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openTipGenValueEditorDialog("buyValue");
                        popupMenuHelper.dismiss();

                    }
                });

                popupMenuHelper.show();

            }
        });

        bind.generateBuyTipsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rate = bind.genRate.getText().toString();
                String scripName = bind.genScriptName.getText().toString();

                if (rate.isEmpty()) {
                    bind.genRate.requestFocus();
                    bind.genRate.setError("Empty");
                } else if (scripName.isEmpty()) {
                    bind.genScriptName.requestFocus();
                    bind.genScriptName.setError("Empty");
                } else {
                    generateTips(rate, scripName, "buyValue");
                }


            }
        });

        bind.generateSellTipsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rate = bind.genRate.getText().toString();
                String scripName = bind.genScriptName.getText().toString();

                if (rate.isEmpty()) {
                    bind.genRate.requestFocus();
                    bind.genRate.setError("Empty");
                } else if (scripName.isEmpty()) {
                    bind.genScriptName.requestFocus();
                    bind.genScriptName.setError("Empty");
                } else {
                    generateTips(rate, scripName, "sellValue");
                }


            }
        });
    }

    private void generateTips(String rate, String scripName, String type) {
        CProgressDialog.mShow(TipGenActivity.this);
        StockDatabase.getTipGenBuyValue(type, new TipGenValueCallback() {
            @Override
            public void onTipGenValueFetched(TipGenValueModel model) {
                if (model != null) {
                    String quantity = model.getQuantity();
                    String lotSize = model.getLotSize();
                    double t1 = Double.parseDouble(model.getFirstTarget());
                    double t2 = Double.parseDouble(model.getSecondTarget());
                    double t3 = Double.parseDouble(model.getThirdTarget());
                    double sl = Double.parseDouble(model.getStopLoss());
                    double lot = Double.parseDouble(lotSize);
                    double qty = Double.parseDouble(quantity);
                    double buy = Double.parseDouble(rate); // Assuming rate is defined and holds the buy value

                    if (type.equals("buyValue")) {
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

                        String stoploss = String.valueOf((int) truncated);
                        String firstTarget = String.valueOf((int) firstTG);
                        String secondTarget = String.valueOf((int) secondTG);
                        String thirdTarget = String.valueOf((int) thirdTG);

                        String Tip = "Intraday Option Tips: " + " scrip name " + scripName + " Buy " + lotSize + " lot or " + quantity + " Qty @ " + rate + " TRG’S " + firstTarget + "-" + secondTarget + "-" + thirdTarget + " SL " + stoploss;
                        String first_target_achieved = "Intraday " + firstTarget + " 1st Target achieved in " + scripName + " 2nd TRG is " + secondTarget + ", 3rd TRG is " + thirdTarget + " our subscribed members buying price is " + rate;
                        String second_target_achieved = "Intraday " + secondTarget + " 2nd Target achieved in " + scripName + " 3rd TRG is " + thirdTarget;
                        String third_target_achieved = "Intraday " + thirdTarget + " 3rd Target achieved in " + scripName + " booked " + quantity + " Qty profit is ";

                        String first_profit_booked = "Our subscribed members total profit booked Rs " + ff_1profit + " in " + scripName + " Qty " + quantity + " buying price and selling price is " + rate + " to " + firstTarget;
                        String second_profit_booked = "Our subscribed members total profit booked Rs " + ss_2profit + " in " + scripName + " Qty " + quantity + " buying price and selling price is " + rate + " to " + secondTarget;
                        String third_profit_booked = "Our subscribed members total profit booked Rs " + tt_3profit + " in " + scripName + " Qty " + quantity + " buying price and selling price is " + rate + " to " + thirdTarget;

                        bind.generatedTip.setText(Tip);

                        bind.generatedFirstProfit.setText(first_profit_booked);
                        bind.generatedSecondProfit.setText(second_profit_booked);
                        bind.generatedThirdProfit.setText(third_profit_booked);

                        bind.generatedFirstTarget.setText(first_target_achieved);
                        bind.generatedSecondTarget.setText(second_target_achieved);
                        bind.generatedThirdTarget.setText(third_target_achieved);
                    } else if (type.equals("sellValue")) {
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
                        double lot_sell = Double.parseDouble(lotSize_sell);
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

                        double pb1 = buy_sell - truncated_sell;
                        Double newData = new Double(pb1);
                        int value = newData.intValue();
                        int ncv = (int) value + 1;
                        String fp1 = String.valueOf(ncv * qty_sell);

                        double pb2 = secondTG_sell - truncated_sell;
                        Double newData2 = new Double(pb2);
                        int value2 = newData2.intValue();
                        int ncv2 = value2 + 1;
                        String fp2 = String.valueOf(ncv2 * qty_sell);

                        double pb3 = thirdtg - truncated_sell;
                        Double newData3 = new Double(pb3);
                        int value3 = newData3.intValue();
                        int ncv3 = value3 + 1;
                        String fp3 = String.valueOf(ncv3 * qty_sell);

                        String sell_Tip = "Intraday Option Tips: " + " scrip name " + scripName + " Buy " + lotSize_sell + " lot or " + quantity_sell + " Qty @ " + atValue_sell + " TRG’S " + rate + "-" + secondTarget_sell + "-" + thirdTarget_sell + " SL " + stoploss_sell;
                        String first_target_achieved = "Intraday " + rate + " 1st Target achieved. And 2nd TRG is " + secondTarget_sell + ", 3rd TRG is " + thirdTarget_sell + ". our subscribed members buying price is " + atValue_sell + " and SL " + stoploss_sell;
                        String second_target_achieved = "Intraday " + secondTarget_sell + " is achieved. And 3rd TRG is " + thirdTarget_sell;
                        String third_target_achieved = "Intraday " + thirdTarget_sell + " is achieved. And booked " + qty_sell + " Qty profit is " + fp3;

                        String first_profit_booked = "our subscribed members total profit booked rs " + fp1 + " in " + scripName + " Qty " + quantity_sell + " buying price and selling price is " + atValue_sell + " to " + rate;
                        String second_profit_booked = "our subscribed members total profit booked rs " + fp2 + " in " + scripName + " Qty " + quantity_sell + " buying price and selling price is " + atValue_sell + " to " + secondTarget_sell;
                        String third_profit_booked = "our subscribed members total profit booked rs " + fp3 + " in " + scripName + " Qty " + quantity_sell + " buying price and selling price is " + atValue_sell + " to " + thirdTarget_sell;

                        bind.generatedTip.setText(sell_Tip);

                        bind.generatedFirstProfit.setText(first_profit_booked);
                        bind.generatedSecondProfit.setText(second_profit_booked);
                        bind.generatedThirdProfit.setText(third_profit_booked);

                        bind.generatedFirstTarget.setText(first_target_achieved);
                        bind.generatedSecondTarget.setText(second_target_achieved);
                        bind.generatedThirdTarget.setText(third_target_achieved);
                    }

                    CProgressDialog.mDismiss();
                } else {
                    CProgressDialog.mDismiss();
                    Toast.makeText(TipGenActivity.this, "Getting null value!", Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onFailure(String errorMessage) {
                CProgressDialog.mDismiss();
                Toast.makeText(TipGenActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateBuyTips(String rate, String scripName) {
       
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

        setValueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


                    Constant.tipGenDB.child(type).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                CProgressDialog.mDismiss();
                                Toast.makeText(TipGenActivity.this, "Value updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TipGenActivity.this, "Failed to update !", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TipGenActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CProgressDialog.mDismiss();
    }
}