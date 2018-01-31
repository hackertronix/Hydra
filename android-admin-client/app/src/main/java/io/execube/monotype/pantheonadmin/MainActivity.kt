package io.execube.monotype.pantheonadmin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener{
    private var googleApiClient: GoogleApiClient? = null
    private var gso: GoogleSignInOptions? = null
    private var mAuth: FirebaseAuth? = null
    private val RC_SIGN_IN: Int = 9001
    private val TAG = "TAG"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prepareSignin()

        sign_in_button.setOnClickListener {

            signIn()
        }
    }


    private fun signIn() {
        val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(intent, RC_SIGN_IN)
    }


    private fun prepareSignin() {

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso!!)
                .build()

        mAuth = FirebaseAuth.getInstance()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account)
            } else {
                val alertDialog = AlertDialog.Builder(this)

                alertDialog.setTitle("Signin Failed")
                        .setMessage("There was an error while signing you in")
                        .setPositiveButton("GOT IT", { dialog, which -> finish() })
                        .show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + account?.getId())

        val credential = GoogleAuthProvider.getCredential(account?.getIdToken(), null)
        mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        Toast.makeText(this, "YAY", Toast.LENGTH_SHORT).show()
                        val user = mAuth!!.getCurrentUser()
                        startActivity(Intent(this, HomeActivity::class.java))

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }

                })

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

        Log.d(TAG, "Signin Failed")
    }
}
