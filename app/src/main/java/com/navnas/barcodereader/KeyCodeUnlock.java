package com.navnas.barcodereader;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

/**
 *
 * @author yu_longji
 *
 */
public class KeyCodeUnlock extends Service {

    private static final String TAG = "MyLocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 60000;
    private static final float LOCATION_DISTANCE = 10f;

    Location mLastLocation;


    private class LocationListener implements android.location.LocationListener {
        //Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    /*
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    */

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.PASSIVE_PROVIDER)
    };





    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }


//    @Override
//    public boolean onStopJob(JobParameters params) {
//        // 停止跟踪这些作业参数，因为我们已经完成工作。
//        //Log.i(TAG, "on stop job: " + params.getJobId());
//
//        // 返回false来销毁这个工作
//        return false;
//    }
//
//
//
//
//    @Override
//    public boolean onStartJob(final JobParameters params) {
//        // The work that this service "does" is simply wait for a certain duration and finish
//        // the job (on another thread).
//
//
//        return true;
//    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("service","start");

        initializeLocationManager();

        try {

            Log.i("GET_LOCATION","start");

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {

                mLocationManager.requestLocationUpdates(
                        LocationManager.PASSIVE_PROVIDER,
                        LOCATION_INTERVAL,
                        LOCATION_DISTANCE,
                        mLocationListeners[0]
                );
            } else {
                //Toast.makeText(this, R.string.error_permission_map, Toast.LENGTH_LONG).show();
            }


        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }









        // onCreate()方法中注册
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mBatInfoReceiver, filter);

//        final IntentFilter homeFilter = new IntentFilter(
//                Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        registerReceiver(homePressReceiver, homeFilter);


    }



    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
//        if(homePressReceiver != null) {
//            try {
//                unregisterReceiver(homePressReceiver);
//            }
//            catch(Exception e) {
//            }
//        }
//
//        // onDestory()方法中解除注册
        if(mBatInfoReceiver != null) {
            try {
                unregisterReceiver(mBatInfoReceiver);
            }
            catch(Exception e) {
            }
        }

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex);
                }
            }
        }



    }

    private Notification buildForegroundNotification() {
        Notification.Builder builder = new Notification.Builder(this);

        builder.setOngoing(true);

        builder.setContentTitle("d")
                .setContentText("d")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("d");
        builder.setPriority(Notification.PRIORITY_MAX);
        return builder.build();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        startForeground(1, buildForegroundNotification());//make it as foreground service, will not be killed


        return super.onStartCommand(intent, flags, startId);
    }
//    //home键
//    private final BroadcastReceiver homePressReceiver = new BroadcastReceiver() {
//        final String SYSTEM_DIALOG_REASON_KEY = "reason";
//        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if(action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
//                if(reason != null
//                        && reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
//                    // 自己随意控制程序，关闭...
//                    Log.e("test", "HomeKey");
//                }
//            }
//        }
//    };
    //电源键
    // Intent.ACTION_SCREEN_OFF;
    // Intent.ACTION_SCREEN_ON;
    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if(Intent.ACTION_SCREEN_OFF.equals(action)) {
                Log.e("keyCodeUnlock", "PowerKey-off");

                //Notification(context, "Wifi Connection Off");

                //context.startService(new Intent(KeyCodeUnlock.this, LocationService.class));

                Intent serviceIntent = new Intent(context, LocationService.class);
                context.getApplicationContext().startService(serviceIntent);



            }else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                Log.e("keyCodeUnlock", "PowerKey-on");

                //Notification(context, "Wifi Connection Off");
                //context.startService(new Intent(KeyCodeUnlock.this, LocationService.class));

                Intent serviceIntent = new Intent(context, LocationService.class);
                context.getApplicationContext().startService(serviceIntent);




            }
        }

        public void Notification(Context mContext, String message) {
            // Set Notification Title
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(mContext.getApplicationContext(), "notify_001");
            Intent ii = new Intent(mContext.getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);

            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText("d");
            bigText.setBigContentTitle("Today's Bible Verse");
            bigText.setSummaryText("Text in detail");

            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
            mBuilder.setContentTitle("Your Title");
            mBuilder.setContentText("Your text");
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setStyle(bigText);

            NotificationManager mNotificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("notify_001",
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(channel);
            }

            mNotificationManager.notify(0, mBuilder.build());

        }



    };


    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: "+ LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }






}

