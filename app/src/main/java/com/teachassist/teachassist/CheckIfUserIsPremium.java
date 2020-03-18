package com.teachassist.teachassist;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.api.Billing;

import java.util.ArrayList;
import java.util.List;

public class CheckIfUserIsPremium {
    BillingClient billingClient;
    Context context;
    private String REMOVE_ADS_SKU = "remove.ads";
    public void check(final Context mContext, final Activity activity, final boolean shouldStartPurchase){
        context = mContext;
        //setup google play billing
        billingClient = BillingClient.newBuilder(context).setListener(new onPurchaseUpdated()).enablePendingPurchases().build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    List<String> skuList = new ArrayList<>();
                    skuList.add(REMOVE_ADS_SKU);
                    //skuList.add("android.test.purchased");
                    if (shouldStartPurchase) {
                        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                        billingClient.querySkuDetailsAsync(params.build(),
                                new SkuDetailsResponseListener() {
                                    @Override
                                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                                     List<SkuDetails> skuDetailsList) {
                                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                .setSkuDetails(skuDetailsList.get(0))
                                                .build();
                                        billingClient.launchBillingFlow(activity, flowParams);
                                    }
                                });
                    } else {

                        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, new PurchaseHistoryResponseListener() {
                            @Override
                            public void onPurchaseHistoryResponse(BillingResult billingResult, List<PurchaseHistoryRecord> list) {}
                        });
                        List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
                        System.out.println(purchases);
                        for (Purchase purchase : purchases) {
                            verifyPurchase(purchase);
                        }
                        checkForPendingPurchases(purchases);

                    }
                }else if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isPremiumUser", true);
                    editor.apply();
                    Toast.makeText(context, "Congratulations, it looks like you've already upgraded!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context, "Something went wrong while trying to launch Google Play Billing", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Toast.makeText(context, "Something went wrong while trying to connect", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private class onPurchaseUpdated implements PurchasesUpdatedListener {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    if(purchase.getSku().equals(REMOVE_ADS_SKU)) {
                        verifyPurchase(purchase);
                    }
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                for (Purchase purchase : purchases) {
                    if(purchase.getSku().equals(REMOVE_ADS_SKU)) {
                        verifyPurchase(purchase);
                    }
                }
                Toast.makeText(context, "It looks like you have already upgraded! Your purchase has been restored.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Something went wrong, Error code: "+billingResult.getResponseCode()+"\nPlease try again in 24 hours.", Toast.LENGTH_LONG).show();
            }
            checkForPendingPurchases(purchases);
        }
    }

    private void verifyPurchase(Purchase purchase){
        // Acknowledge the purchase if it hasn't already been acknowledged.
        if(purchase.getSku().equals(REMOVE_ADS_SKU)) {
            if (!purchase.isAcknowledged() && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isPremiumUser", true);
                            editor.apply();
                            Toast.makeText(context, "Purchase Successful, Thank You!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isPremiumUser", true);
                editor.apply();
            }
        }
    }
    private void checkForPendingPurchases(List<Purchase> purchases){
        if(purchases == null){
            return;
        }
        for (Purchase purchase : purchases) {
            long time= System.currentTimeMillis();
            long ONE_MINUTE = 60000;
            if(purchase.getPurchaseState() == Purchase.PurchaseState.PENDING && (time-purchase.getPurchaseTime()) > ONE_MINUTE*60*24 ) {
                ConsumeParams consumePurchaseParams =
                        ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.consumeAsync(consumePurchaseParams, new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(BillingResult billingResult, String s) {
                        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.ITEM_NOT_OWNED) {
                            Toast.makeText(context, "Your purchase was declined with error code: " + billingResult.getResponseCode()+". If you have already bought Teachassist pro and everything works fine you can ignore this message.", Toast.LENGTH_LONG).show();
                       }
                    }
                });
            }
        }
    }
}
