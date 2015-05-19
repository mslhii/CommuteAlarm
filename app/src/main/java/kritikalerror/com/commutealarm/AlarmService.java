package kritikalerror.com.commutealarm;

/**
 * Created by Michael on 2/23/2015.
 */
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class AlarmService extends Service {

    private Ringtone mRingtone;
    private NotificationManager alarmNotificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        Log.d("AlarmService", "Ringtone playing!");
        mRingtone = RingtoneManager.getRingtone(this, alarmUri);
        mRingtone.play();

        AlarmSupport.createNotification(getApplicationContext(), "Alarm Ringing!");

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d("AlarmService", "Ringtone stopping!");
        mRingtone.stop();

        RingtoneManager ringtoneManager = new RingtoneManager(this);
        ringtoneManager.stopPreviousRingtone();
    }
}