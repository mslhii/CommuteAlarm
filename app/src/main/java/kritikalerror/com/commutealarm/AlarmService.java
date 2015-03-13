package kritikalerror.com.commutealarm;

/**
 * Created by Michael on 2/23/2015.
 */
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class AlarmService extends IntentService {
    private NotificationManager alarmNotificationManager;

    public static final String ACTION_AlarmService = "kriticalerror.com.commutealarm.RESPONSE";

    public AlarmService() {
        super("AlarmService");

    }

    private Ringtone mRingtone;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onHandleIntent(Intent intent) {
        sendNotification("Wake Up! Wake Up!");
        /*
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        mRingtone = RingtoneManager.getRingtone(this, alarmUri);
        if (!mRingtone.isPlaying())
        {
            mRingtone.play();
        }
        //mRingtone.play();
        */
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_AlarmService);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra("AlarmPackage", "on");
        sendBroadcast(intentResponse);
    }

    @Override
    public void onDestroy()
    {
        //mRingtone.stop();
        Log.d("AlarmService", "Destroying service!");
    }

    private void sendNotification(String msg) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);
        /*
        alarmNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Alarm")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText(msg);

        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(0, alamNotificationBuilder.build());
        */
        Log.d("AlarmService", "Notification sent.");
    }
}
