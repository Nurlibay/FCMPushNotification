package uz.texnopos.fcmpushnotification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import uz.texnopos.fcmpushnotification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch to AppTheme for displaying the activity
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Your code
        binding.buttonRetrieveToken.setOnClickListener {
            // Get token
            if ( checkGooglePlayServices() ) {
                // [START retrieve_current_token]
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(TAG, getString(R.string.token_error), task.exception)
                            return@OnCompleteListener
                        }

                        // Get new Instance ID token
                        val token = task.result

                        // Log and toast
                        val msg = getString(R.string.token_prefix, token)
                        Log.d("tekseriw", msg)
                        Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    })
                // [END retrieve_current_token]
            } else {
                //You won't be able to send notifications to this device
                Log.w(TAG, "Device doesn't have google play services")
            }
        }

        val bundle = intent.extras
        if (bundle != null) { //bundle must contain all info sent in "data" field of the notification
            binding.textViewNotification.text = bundle.getString("text")
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
            IntentFilter("MyData")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            binding.textViewNotification.text = intent.extras?.getString("message")
        }
    }

    private fun checkGooglePlayServices(): Boolean {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        return if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Error")
            // ask user to update google play services.
            false
        } else {
            Log.i(TAG, "Google play services updated")
            true
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}