package com.example.machinelearning

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import com.appspector.sdk.AppSpector
import com.bosphere.filelogger.FL
import com.bosphere.filelogger.FLConfig
import com.bosphere.filelogger.FLConst
import com.example.machinelearning.`interface`.OnFragmentInteractionListener
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : AppCompatActivity(), OnFragmentInteractionListener {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppSpector
                .build(this)
                .withDefaultMonitors().run("android_OGIzODc4OWMtZGQ3Ni00Mzg3LWI5MjQtNjJiZTM0Y2UxNTVh")

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

    }

    override fun onSupportNavigateUp() =
            findNavController(this, R.id.navHostFragment).navigateUp()
}
