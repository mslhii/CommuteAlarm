package kritikalerror.com.commutealarm;

/**
 * Created by Michael on 2/23/2015.
 */
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        //this will update the UI with message
        MainActivity inst = MainActivity.instance();
        inst.setAlarmText("Alarm Ringing!");

        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);
        /*
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        if (!ringtone.isPlaying())
        {
            ringtone.play();
        }
        */

        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

}

/*
03-11 01:47:08.368    6322-6322/kritikalerror.com.commutealarm D/MyActivity﹕ Alarm On
03-11 01:48:08.442    6322-6322/kritikalerror.com.commutealarm D/Ringtone﹕ Successfully created local player
03-11 01:48:08.458    6322-6322/kritikalerror.com.commutealarm E/MediaPlayer﹕ Should have subtitle controller already set
03-11 01:48:15.875    6322-6322/kritikalerror.com.commutealarm D/Ringtone﹕ Successfully created local player
03-11 01:48:15.876    6322-6322/kritikalerror.com.commutealarm D/MyActivity﹕ Alarm Off
03-11 01:48:15.888    6322-6322/kritikalerror.com.commutealarm E/MediaPlayer﹕ Should have subtitle controller already set
03-11 01:48:16.933    6322-6322/kritikalerror.com.commutealarm D/MyActivity﹕ Alarm On
03-11 01:49:17.179    6322-6322/kritikalerror.com.commutealarm D/Ringtone﹕ Successfully created local player
03-11 01:49:17.211    6322-6322/kritikalerror.com.commutealarm E/MediaPlayer﹕ Should have subtitle controller already set
03-11 01:49:19.693    6322-6322/kritikalerror.com.commutealarm D/Ringtone﹕ Successfully created local player
03-11 01:49:19.693    6322-6322/kritikalerror.com.commutealarm D/MyActivity﹕ Alarm Off
 */