package th.co.bitfactory.kteddystonebg

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.google.firebase.firestore.FirebaseFirestore


class BeaconMessageReceiver : BroadcastReceiver() {
    companion object {
        val TAG = BeaconMessageReceiver::class.java.simpleName
    }

    override fun onReceive(context: Context, intent: Intent) {
        val db = FirebaseFirestore.getInstance()

        Nearby.getMessagesClient(context).handleIntent(intent, object : MessageListener() {
            override fun onFound(message: Message) {
                Log.i(TAG, "Found message via PendingIntent: $message")
                db.collection("scan-logs")
                        .add(mapOf( "event" to "found", "message" to message, "content" to message.content, "namespace" to message.namespace, "type" to message.type ))
                        .addOnSuccessListener { documentReference -> Log.d(TAG, "document added with id ${documentReference.id}") }
                        .addOnFailureListener { e -> Log.w(TAG, "error adding document", e) }

            }

            override fun onLost(message: Message) {
                Log.i(TAG, "Lost message via PendingIntent: $message")
                db.collection("scan-logs")
                        .add(mapOf( "event" to "lost", "message" to message, "content" to message.content, "namespace" to message.namespace, "type" to message.type ))
                        .addOnSuccessListener { documentReference -> Log.d(TAG, "document added with id ${documentReference.id}") }
                        .addOnFailureListener { e -> Log.w(TAG, "error adding document", e) }
            }
        })
    }
}
