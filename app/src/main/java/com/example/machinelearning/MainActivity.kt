package com.example.machinelearning

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation.findNavController
import com.example.machinelearning.fragment.MainFragment

class MainActivity : AppCompatActivity(), MainFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onSupportNavigateUp() =
            findNavController(this, R.id.navHostFragment).navigateUp()
}
