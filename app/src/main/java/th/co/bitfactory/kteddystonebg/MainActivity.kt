package th.co.bitfactory.kteddystonebg

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.gms.nearby.messages.Strategy
import com.google.android.gms.nearby.messages.SubscribeOptions
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Start Background Subscribe", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            foregroundSubscribe()
//            backgroundSubscribe()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun foregroundSubscribe() {
        Log.i(TAG, "Subscribing for foreground updates.")
        val options = SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .build()
        Nearby.getMessagesClient(this).subscribe(object:MessageListener() {
            override fun onFound(message: Message) {
                Log.i(TAG, "Found message via PendingIntent: $message")
                db.collection("scan-logs")
                        .add(mapOf( "event" to "found", "message" to message, "content" to message.content, "namespace" to message.namespace, "type" to message.type ))
                        .addOnSuccessListener { documentReference -> Log.d(BeaconMessageReceiver.TAG, "document added with id ${documentReference.id}") }
                        .addOnFailureListener { e -> Log.w(BeaconMessageReceiver.TAG, "error adding document", e) }

            }

            override fun onLost(message: Message) {
                Log.i(TAG, "Lost message via PendingIntent: $message")
                db.collection("scan-logs")
                        .add(mapOf( "event" to "lost", "message" to message, "content" to message.content, "namespace" to message.namespace, "type" to message.type ))
                        .addOnSuccessListener { documentReference -> Log.d(BeaconMessageReceiver.TAG, "document added with id ${documentReference.id}") }
                        .addOnFailureListener { e -> Log.w(BeaconMessageReceiver.TAG, "error adding document", e) }
            }
        }, options)
    }

    // Subscribe to messages in the background.
    private fun backgroundSubscribe() {
        Log.i(TAG, "Subscribing for background updates.")
        val options = SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .build()
        Nearby.getMessagesClient(this).subscribe(getPendingIntent(), options)
    }

    private fun getPendingIntent(): PendingIntent {
        return PendingIntent.getBroadcast(this, 0, Intent(this, BeaconMessageReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
