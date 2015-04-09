package kritikalerror.com.commutealarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Michael on 4/9/2015.
 */
public class TrafficUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {

    }
}
