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

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import marm.mobile.cassi.model.CASSIServiceCallback;
import marm.mobile.cassi.model.PSMapping;

/**
 * The main CASSI service responsible for handling incoming messages and output to the connected
 * device.
 *
 * @author Martin Armbruster
 */
public class CASSIService extends Service {
    /**
     * ID of the notification to inform the user about the current status.
     */
    private static final int NOTIFICATION_ID = 1;
    /**
     * ID for a notification indicating the service has stopped.
     */
    private static final int NOTIFICATION_END_ID = 3;
    /**
     * Notification channel id.
     */
    private static final String NOTIFICATION_CHANNEL = "2";
    /**
     * The notification manager.
     */
    private NotificationManager notificationManager;
    /**
     * Builder for notifications.
     */
    private NotificationCompat.Builder notificationBuilder;
    /**
     * Mapping for services to patterns.
     */
    private PSMapping map;
    /**
     * Connection handler for Bluetooth Low Energy on Android.
     */
    private AndroidBLEConnectionHandler bleHandler;
    /**
     * Broadcast receiver for incoming messages.
     */
    private CallReceiver broadcastReceiver;
    /**
     * ThreadPool for executing the actual work.
     */
    private ExecutorService threadPool;
    /**
     * Callback for the BLE connection handler.
     */
    private CASSIServiceCallback.OnBLEStateChanged bleCallback =
            new CASSIServiceCallback.OnBLEStateChanged() {
                @Override
                public void onBLEStateChanged(int newState) {
                    switch(newState) {
                        case CASSIServiceCallback.BLE_STATE_SCANNING:
                            updateNotification(getString(R.string.cassi_notification_scan));
                            break;
                        case CASSIServiceCallback.BLE_STATE_SCAN_DEVICE_NOT_FOUND:
                            endNotification(String.format(
                                    getString(R.string.cassi_notification_scan_not_found),
                                    bleHandler.getDeviceName()));
                            threadPool.shutdown();
                            stopSelf();
                            break;
                        case CASSIServiceCallback.BLE_STATE_CONNECTING:
                            updateNotification(String.format(
                                    getString(R.string.cassi_notification_connecting),
                                    bleHandler.getDeviceName()));
                            break;
                        case CASSIServiceCallback.BLE_STATE_CONNECTED:
                            updateNotification(String.format(
                                    getString(R.string.cassi_notification_connected),
                                    bleHandler.getDeviceName()));
                            broadcastReceiver = new CallReceiver(messCallback);
                            IntentFilter filter = new IntentFilter();
                            filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
                            filter.addAction(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION);
                            filter.addAction(Telephony.Sms.Intents.SMS_CB_RECEIVED_ACTION);
                            registerReceiver(broadcastReceiver, filter);
                            break;
                        case CASSIServiceCallback.BLE_STATE_DISCONNECTING:
                            updateNotification(String.format(
                                    getString(R.string.cassi_notification_disconnecting),
                                    bleHandler.getDeviceName()));
                            break;
                        case CASSIServiceCallback.BLE_STATE_DISCONNECTED:
                            endNotification(getString(R.string.cassi_notification_disconnected));
                            threadPool.shutdown();
                            stopSelf();
                            break;
                    }
                }
            };
    /**
     * Callback to this service when a message is delivered.
     */
    private CASSIServiceCallback.OnMessageDelivery messCallback =
            new CASSIServiceCallback.OnMessageDelivery() {
                @Override
                public void onCallStart() {
                    threadPool.submit(new Runnable() {
                        @Override
                        public void run() {
                            bleHandler.playPattern(map.getMapping(PSMapping.CALL), true);
                        }
                    });
                }

                @Override
                public void onCallStop() {
                    bleHandler.stopPlaying();
                }

                @Override
                public void onSMSMMSReceived() {
                    threadPool.submit(new Runnable() {
                        @Override
                        public void run() {
                            bleHandler.playPattern(map.getMapping(PSMapping.SMS), false);
                        }
                    });
                }
            };

    /**
     * Creates a new instance.
     */
    public CASSIService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        setUpNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(threadPool!=null) {
            return Service.START_STICKY;
        }
        threadPool = Executors.newFixedThreadPool(2);
        final Context context = this;
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                map = new PSMapping();
                bleHandler = new AndroidBLEConnectionHandler(context, bleCallback);
                bleHandler.connect();
            }
        });
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                long time = 15L*60*1000;
                try {
                    Thread.sleep(time);
                    bleHandler.stopPlaying();
                    bleHandler.disconnect();
                } catch(InterruptedException e) {
                }
            }
        });
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(broadcastReceiver!=null) {
            unregisterReceiver(broadcastReceiver);
        }
        if(!threadPool.isTerminated()) {
            threadPool.shutdownNow();
        }
    }

    /**
     * Sets up the notification channel for devices running Android 8+.
     */
    @TargetApi(26)
    private void setUpNotificationChannel() {
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL
                , getString(R.string.cassi_notification_channel_title), importance);
        mChannel.setDescription(getString(R.string.cassi_notification_channel_description));
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(mChannel);
    }

    /**
     * Sets up the notification and foreground service.
     */
    private void setUpNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setUpNotificationChannel();
        }
        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL);
        startForeground(NOTIFICATION_ID, createNotification(
                getString(R.string.cassi_notification_title),
                getString(R.string.cassi_notification_scan)));
    }

    /**
     * Updates the notification.
     *
     * @param text text to display in the notification.
     */
    private void updateNotification(String text) {
        notificationManager.notify(NOTIFICATION_ID, createNotification(
                getString(R.string.cassi_notification_title), text));
    }

    /**
     * Shows a ending notification.
     *
     * @param text text to display in the notification.
     */
    private void endNotification(String text) {
        notificationManager.notify(2, createNotification(
                getString(R.string.cassi_notification_end_title), text));
    }

    /**
     * Creates a notification.
     *
     * @param title title of the notification.
     * @param text text of the notification.
     * @return the created notification.
     */
    private Notification createNotification(String title, String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        return notificationBuilder
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_cassi_notification)
                .setContentIntent(pendingIntent)
                .build();
    }
}