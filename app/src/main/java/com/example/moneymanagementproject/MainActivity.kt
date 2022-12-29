package com.example.moneymanagementproject


import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.SurfaceControl
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.moneymanagementproject.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var mAuth: FirebaseAuth
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    public override fun onStart() {
        super.onStart()
        mAuth = Firebase.auth
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
//        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //buat login dan sign in
        val credential = oneTapClient.getSignInCredentialFromIntent(data)
        val idToken = credential.googleIdToken
        val username = credential.id
        val password = credential.password

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithCredential:success")
                                        val user = mAuth.currentUser

//                                        binding.newTransaction.setOnClickListener {
//                                            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//                                        }

//                            updateUI(user)
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithCredential:failure", task.exception)
//                            updateUI(null)?
                                    }
                                }
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(TAG, "No ID token!")
                        }
                    }
                } catch (e: ApiException) {

                }
            }
        }
    }


    //function to display onetap sign in
    private fun displaySignIn(){
        Log.d(TAG, "displaySignIn: ")
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Log.d(TAG, e.localizedMessage)
            }
    }

    private fun signOutAuth(){
        Firebase.auth.signOut()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // default fragment
        loadFragment(Home())

        // Bottom nav bar, navigating to another pages (fragments)
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home_ic -> loadFragment(Home())
                R.id.transaction_ic -> loadFragment(Transaction())
                R.id.stats_ic -> loadFragment(Statistics())
                R.id.stats_wallet_ic -> loadFragment(StatisticWallet())
                else -> {
                }
            }
            true
        }

        // Pop up window add transaction
        binding.addTransc.setOnClickListener {
            val intent = Intent(this, AddTransaction::class.java)
            // start your next activity
            startActivity(intent)
        }

            //Finding ID in act main
//        val googleSignIn: Button = findViewById<Button>(R.id.googleSignIn)
//        val googleSignOut: Button = findViewById<Button>(R.id.googleSignOut)


            mAuth = Firebase.auth
            oneTapClient = Identity.getSignInClient(this)
            signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .build()

//        googleSignIn.setOnClickListener {
//            displaySignIn()
//        }
//
//        googleSignOut.setOnClickListener{
//            signOutAuth()
//        }


    }

    private fun loadFragment(fragment: Fragment) {
        val transc = supportFragmentManager.beginTransaction()
        transc.replace(R.id.fragmentContainer, fragment)
        transc.addToBackStack(null)
        transc.commit()
    }



}



