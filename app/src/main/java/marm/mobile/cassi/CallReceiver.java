/* ########################################################
 * #####    CASSI, Call Assistant - The MIT-License    ####
 * ########################################################
 *
 * Copyright (C) 2018, Martin Armbruster
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.*/

package marm.mobile.cassi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import marm.mobile.cassi.model.CASSIServiceCallback;

/**
 * Broadcast receiver for receiving incoming SMS/MMS and calls and forwarding it to the CASSI
 * service.
 *
 * @author Martin Armbruster
 */
public class CallReceiver extends BroadcastReceiver {
    /**
     * Callback to the CASSI service.
     */
    private CASSIServiceCallback.OnMessageDelivery cassiCallback;

    /**
     * Creates a new instance.
     *
     * @param callback callback to the CASSI service.
     */
    public CallReceiver(CASSIServiceCallback.OnMessageDelivery callback) {
        cassiCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                cassiCallback.onCallStart();
            } else {
                cassiCallback.onCallStop();
            }
        } else if(intent.getAction().equals(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION)
                || intent.getAction().equals(Telephony.Sms.Intents.SMS_CB_RECEIVED_ACTION)) {
            cassiCallback.onSMSMMSReceived();
        }
    }
}