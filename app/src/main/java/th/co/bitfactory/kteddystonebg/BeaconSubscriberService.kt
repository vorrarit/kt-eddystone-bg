package th.co.bitfactory.kteddystonebg

import android.app.PendingIntent
import android.arch.lifecycle.LifecycleService
import android.content.Intent
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Strategy
import com.google.android.gms.nearby.messages.SubscribeOptions

class BeaconSubscriberService: LifecycleService() {

    companion object {
        val TAG = BeaconSubscriberService::class.java.name
    }

    private fun getPendingIntent(): PendingIntent {
        return PendingIntent.getBroadcast(this, 0, Intent(this, BeaconMessageReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val options = SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .build()
        Nearby.getMessagesClient(this).subscribe(getPendingIntent(), options)

        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }
}