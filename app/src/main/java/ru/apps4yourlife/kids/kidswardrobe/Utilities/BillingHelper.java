package ru.apps4yourlife.kids.kidswardrobe.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 08-Aug-18.
 */

public class BillingHelper {

    public static final class SKUCodes {
        public final static String noAdsCode = "noads149";
        //public final static String noAdsCode = "android.test.purchased";
        public final static String help2500Code = "help2500";
        public final static String help500Code = "help500";
        public final static String help99Code = "help99";
    }


    public interface LastPurchaseListener {
        public void setLastPurchase(String code);
    }

    private BillingClient mBillingClient;
    private Context mContext;
    private LastPurchaseListener mPurchasesListener;

    public BillingHelper(Context context, PurchasesUpdatedListener listener, LastPurchaseListener listener2) {
        mContext = context;
        mBillingClient = BillingClient.newBuilder(mContext).setListener(listener).build();
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        listener.onPurchasesUpdated(BillingClient.BillingResponse.OK, purchasesResult.getPurchasesList());
        mPurchasesListener = listener2;
    }

    protected void RouteOperation(String skuCode,int operation_code, String extra1, String extra2) {
        switch (operation_code) {
            case 0: // check status

            break;
            case 1:
                if (skuCode.equalsIgnoreCase(SKUCodes.noAdsCode)) {
                    // если все ок - показываем сообщение
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    String message = mContext.getResources().getString(R.string.noAdsFramgent_text) + " " + extra1 + " " + extra2;
                    builder.setMessage(message)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //Toast.makeText(mContext,"START BUY",Toast.LENGTH_LONG).show();
                                    startPurchase(SKUCodes.noAdsCode);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    builder.create().show();
                } else {
                    startPurchase(skuCode);
                }
            break;
        }
    }

    protected void startPurchase(String skuCode) {

        mPurchasesListener.setLastPurchase(skuCode);
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(skuCode)
                .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                .build();
        int responseCode = mBillingClient.launchBillingFlow( (Activity) mContext, flowParams);
        //Log.e("ADS","CODE IS = " + responseCode);
    }


    public void StartOperationInStore(final String skuCode, final int operation_code) {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    List skuList = new ArrayList<>();
                    skuList.add(skuCode);
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    mBillingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                                    if (responseCode == BillingClient.BillingResponse.OK) {
                                        for (SkuDetails skuDetails : skuDetailsList) {
                                            String sku = skuDetails.getSku();
                                            String price = skuDetails.getPrice();
                                            if (sku.equalsIgnoreCase(skuCode)) {
                                                RouteOperation(skuCode,operation_code, price, "");
                                            }
                                        }

                                    }
                                }
                            });                        //
                } else {
                    Toast.makeText(mContext,"Ошибка при попытке соединения с Google Play. Попробуйте еще раз.", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(mContext,"Ошибка при попытке соединения с Google Play. Попробуйте еще раз.", Toast.LENGTH_LONG).show();
            }
        });
    }

}
