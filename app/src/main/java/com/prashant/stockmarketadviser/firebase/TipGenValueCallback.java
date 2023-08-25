package com.prashant.stockmarketadviser.firebase;

import com.prashant.stockmarketadviser.model.TipGenValueModel;


public interface TipGenValueCallback {
    void onTipGenValueFetched(TipGenValueModel valueModel);
    void onFailure(String errorMessage);
}
