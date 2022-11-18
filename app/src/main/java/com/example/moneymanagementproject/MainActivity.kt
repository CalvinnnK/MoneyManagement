package com.example.moneymanagementproject


import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.navigation.findNavController
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var mAuth: FirebaseAuth
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity

    private lateinit  var oneTapClient: SignInClient
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
        setContentView(R.layout.activity_main)
        loadFragment(home())

        // default page
        val fragmentManager = supportFragmentManager


//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
//        val navController = findNavController(R.id.fragmentContainer)
//        bottomNavigationView.setupWithNavController(navController)

        // Bottom nav bar, navigating to another pages (fragments)
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemReselectedListener {
            fragmentManager.commit {
                setReorderingAllowed(true)
                when (it.itemId) {
                    R.id.home_ic -> {
                        Log.d(TAG, "Frag 1 page")
                        loadFragment(home())
                        Log.d(TAG, "Frag 1 page : Done")
                        return@setOnItemReselectedListener
                    }

                    R.id.transaction_ic -> {
                        Log.d(TAG, "Frag 2 page")
                        loadFragment(transaction())
                        Log.d(TAG, "Frag 2 page: Done")
                        return@setOnItemReselectedListener
                    }

                    R.id.stats_ic -> {
                        Log.d(TAG, "Frag 3 page")
                        loadFragment(Statistics())
                        Log.d(TAG, "Frag 3 page: Done")
                        return@setOnItemReselectedListener
                    }
                }
            }


//        setSupportActionBar(binding.addTransc)
//
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

            binding.addTransc.setOnClickListener {
                Toast.makeText(this, "Replace with your own action", Toast.LENGTH_LONG).show()
                Log.d(TAG, "Me pop up")
            }

            setContentView(R.layout.activity_main)

            //Finding ID in act main

//        val googleSignIn: Button = findViewById<Button>(R.id.googleSignIn)
//        val googleSignOut: Button = findViewById<Button>(R.id.googleSignOut)

            
            val newTransc: FloatingActionButton = findViewById(R.id.addTransc)
            val botNav : BottomNavigationView = findViewById(R.id.bottomNavigationView);



            botNav.setOnClickListener{
                Log.d(TAG, "onCreate: Oji")
            }


//            newTransc.setOnClickListener{
//                Log.d(TAG,"masuk jozzz")
//                val intent = Intent(this, AddTransaction::class.java)
//                startActivity(intent)
//            }


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
    }



    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        val transc = supportFragmentManager.beginTransaction()
        transc.replace(R.id.fragmentContainer, fragment)
        transc.addToBackStack(null)
        transc.commit()
    }


    fun basicReadWrite() {

        // [START write_message]
        // Write a message to the database
        val database = Firebase.database
        val myRef =
            database.getReference("https://money-management-app-9810f-default-rtdb.asia-southeast1.firebasedatabase.app")

        myRef.setValue("Hello, World!")
        // [END write_message]

        // [START read_message]
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<String>()
                Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
        // [END read_message]
    }

    fun popUpWindow(view: View) {
        Log.d(TAG,"masuk jozzz")
        val intent = Intent(this, AddTransaction::class.java)
        startActivity(intent)
    }
}



