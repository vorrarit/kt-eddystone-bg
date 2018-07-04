package th.co.bitfactory.kteddystonebg

import android.support.multidex.MultiDexApplication
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import org.altbeacon.beacon.*
import org.altbeacon.beacon.startup.BootstrapNotifier
import org.altbeacon.beacon.startup.RegionBootstrap
import java.util.*

class KtEddystoneBgApplication: MultiDexApplication(), BootstrapNotifier, BeaconConsumer, RangeNotifier {

    companion object {
        val TAG = KtEddystoneBgApplication::class.java.simpleName
    }


    private var db: FirebaseFirestore? = null
    private var beaconManager: BeaconManager? = null
    private var regionBootstrap: RegionBootstrap? = null

    override fun onCreate() {
        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()

        super.onCreate()
        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager!!.getBeaconParsers().add(BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=30,p:3-3:-41,i:4-11"))
        val region = Region("all-beacons-region", null, null, null)
        regionBootstrap = RegionBootstrap(this, region)

        beaconManager!!.bind(this)
    }

    override fun onBeaconServiceConnect() {
        beaconManager?.let {
            it.removeAllRangeNotifiers()
            it.addRangeNotifier(this)
        }
    }

    override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
        beacons?.forEach {
            Log.d(TAG, "didRangeBeaconsInRegion ${it.id1} ${it.id2}")
            db!!.collection("scan-logs")
                    .add(mapOf("timestamp" to Date(), "event" to "didRangeBeaconsInRegion", "content" to "didRangeBeaconsInRegion ${it.id1} ${it.id2}"))
                    .addOnSuccessListener { documentReference -> Log.d(BeaconMessageReceiver.TAG, "document added with id ${documentReference.id}") }
                    .addOnFailureListener { e -> Log.w(BeaconMessageReceiver.TAG, "error adding document", e) }
        }
    }

    override fun didDetermineStateForRegion(p0: Int, p1: Region?) {
        Log.d(TAG, "didDetermineStateForRegion")
        db!!.collection("scan-logs")
                .add(mapOf( "timestamp" to Date(), "event" to "didDetermineStateForRegion", "content" to "didEnterRegion ${p1?.id1} ${p1?.id2} ${p1?.uniqueId}"))
                .addOnSuccessListener { documentReference -> Log.d(BeaconMessageReceiver.TAG, "document added with id ${documentReference.id}") }
                .addOnFailureListener { e -> Log.w(BeaconMessageReceiver.TAG, "error adding document", e) }
    }

    override fun didEnterRegion(p0: Region?) {
        Log.d(TAG, "didEnterRegion ${p0?.id1} ${p0?.id2} ${p0?.uniqueId}")
        db!!.collection("scan-logs")
                .add(mapOf( "timestamp" to Date(), "event" to "didEnterRegion", "content" to "didEnterRegion ${p0?.id1} ${p0?.id2} ${p0?.uniqueId}"))
                .addOnSuccessListener { documentReference -> Log.d(BeaconMessageReceiver.TAG, "document added with id ${documentReference.id}") }
                .addOnFailureListener { e -> Log.w(BeaconMessageReceiver.TAG, "error adding document", e) }

    }

    override fun didExitRegion(p0: Region?) {
        Log.d(TAG, "didExitRegion ${p0?.id1} ${p0?.id2} ${p0?.uniqueId}")
        db!!.collection("scan-logs")
                .add(mapOf( "timestamp" to Date(), "event" to "didExitRegion", "content" to "didEnterRegion ${p0?.id1} ${p0?.id2} ${p0?.uniqueId}"))
                .addOnSuccessListener { documentReference -> Log.d(BeaconMessageReceiver.TAG, "document added with id ${documentReference.id}") }
                .addOnFailureListener { e -> Log.w(BeaconMessageReceiver.TAG, "error adding document", e) }
    }


}