package com.navnas.barcodereader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;


public class LocationService extends Service {

    private static final String TAG = "MyLocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 60000;
    private static final float LOCATION_DISTANCE = 10f;

    Double latitude ;
    Double longitude;

/*    Location mLastLocation;


    private class LocationListener implements android.location.LocationListener {
        //Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);

            if(mLastLocation==null) {
                mLastLocation = new Location(provider);
            }
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
*/
    /*
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    */

//    LocationListener[] mLocationListeners = new LocationListener[]{
//            new LocationListener(LocationManager.PASSIVE_PROVIDER)
//    };








    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }




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

//                mLocationManager.requestLocationUpdates(
//                        LocationManager.PASSIVE_PROVIDER,
//                        LOCATION_INTERVAL,
//                        LOCATION_DISTANCE,
//                        mLocationListeners[0]
//                );
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

        getLocation();


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

        // onDestory()方法中解除注册
        if(mBatInfoReceiver != null) {
            try {
                unregisterReceiver(mBatInfoReceiver);
            }
            catch(Exception e) {
            }
        }


//        if (mLocationManager != null) {
//            for (int i = 0; i < mLocationListeners.length; i++) {
//                try {
//                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        return;
//                    }
//                    mLocationManager.removeUpdates(mLocationListeners[i]);
//                } catch (Exception ex) {
//                    Log.i(TAG, "fail to remove location listener, ignore", ex);
//                }
//            }
//        }
    }

//    private Notification buildForegroundNotification() {
//        Notification.Builder builder = new Notification.Builder(this);
//
//        builder.setOngoing(true);
//
//        builder.setContentTitle("d")
//                .setContentText("d")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setTicker("d");
//        builder.setPriority(Notification.PRIORITY_MAX);
//        return builder.build();
//    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        //startForeground(1, buildForegroundNotification());//make it as foreground service, will not be killed


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
                //Log.e("location", mLastLocation.toString());
                Log.e("location",longitude+"/"+latitude);
                //Notification(context, mLastLocation.toString());

                CustomNotification(context,"2");


            }else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                //Log.e("location", mLastLocation.toString());

                //Notification(context, mLastLocation.toString());

                CustomNotification(context,"2");


            }
        }

        public void CustomNotification(Context mContext,String message) {
            // Using RemoteViews to bind custom layouts into Notification
            RemoteViews remoteViews = new RemoteViews(getPackageName(),
                    R.layout.custom_push);

            // Set Notification Title
            String strtitle = "title";
            // Set Notification Text
            String strtext = "text";

            // Open NotificationView Class on Notification Click
            Intent intent = new Intent(mContext.getApplicationContext(), MainActivity.class);
            // Send data to NotificationView Class
            intent.putExtra("title", strtitle);
            intent.putExtra("text", strtext);
            // Open NotificationView.java Activity
            PendingIntent pIntent = PendingIntent.getActivity(mContext.getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext.getApplicationContext(),"notify_001")
                    // Set Icon
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    // Set Ticker Message
                    // Dismiss Notification
                    .setAutoCancel(true)
                    // Set PendingIntent into Notification
                    .setContentIntent(pIntent)
                    // Set RemoteViews into Notification
                    .setContent(remoteViews);

            // Locate and set the Image into customnotificationtext.xml ImageViews
            remoteViews.setImageViewResource(R.id.imagenotileft,R.drawable.common_google_signin_btn_icon_dark_normal);
            remoteViews.setImageViewResource(R.id.imagenotiright,R.drawable.common_full_open_on_phone);

            // Locate and set the Text into customnotificationtext.xml TextViews
            remoteViews.setTextViewText(R.id.title,"text1");
            remoteViews.setTextViewText(R.id.text,"text2");

            remoteViews.setTextViewText(R.id.textView,longitude+"/"+latitude);

            Bitmap bitm=null;
            Ecoad ecc=new Ecoad(120, 40);
            try {
                bitm=ecc.bitmap1("012345");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            remoteViews.setImageViewBitmap(R.id.imagenotileft,bitm);





            // Create Notification Manager
            NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("notify_001",
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationmanager.createNotificationChannel(channel);
            }
            // Build Notification with Notification Manager
            notificationmanager.notify(0, builder.build());

        }

        public void Notification(Context mContext, String message) {
            // Set Notification Title
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(mContext.getApplicationContext(), "notify_001");
            Intent ii = new Intent(mContext.getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);


            Bitmap bitm=null;
            Ecoad ecc=new Ecoad(120, 40);
            try {
                bitm=ecc.bitmap1("012345");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            NotificationCompat.BigPictureStyle bigPictureStyle=new NotificationCompat.BigPictureStyle();
            //bigPictureStyle.bigLargeIcon(bitm);
            bigPictureStyle.bigPicture(bitm);

            bigPictureStyle.setBigContentTitle("big");

            bigPictureStyle.bigLargeIcon(bitm);







            mBuilder.setContentIntent(pendingIntent);

            mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setStyle(bigPictureStyle);

            mBuilder.setLargeIcon(bitm);











            NotificationManager mNotificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("notify_001",
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                mNotificationManager.createNotificationChannel(channel);
            }

            mNotificationManager.notify(0, mBuilder.build());






/*


            NotificationManager mNotificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("notify_001",
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(mContext.getApplicationContext(), "notify_001");
            Intent ii = new Intent(mContext.getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);




            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_push);
            contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
            contentView.setTextViewText(R.id.title, "Custom notification");
            contentView.setTextViewText(R.id.text, "This is a custom layout");

             mBuilder
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContent(contentView);

            Notification notification = mBuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            mNotificationManager.notify(1, notification);

*/

        }



    };

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: "+ LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void getLocation() {
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.d("lat", "" + latitude);
            Log.d("long", "" + longitude);


        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


    }

}

