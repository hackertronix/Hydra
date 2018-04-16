package io.execube.monotype.deimos.sign_in

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.graphics.drawable.Animatable2Compat
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.WindowManager
import android.widget.ImageView
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
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.common.HomeActivity
import io.execube.monotype.deimos.model.Admin
import kotlinx.android.synthetic.main.activity_main.header
import kotlinx.android.synthetic.main.activity_main.header2
import kotlinx.android.synthetic.main.activity_main.header3
import kotlinx.android.synthetic.main.activity_main.reveal_view
import kotlinx.android.synthetic.main.activity_main.sign_in_button
import kotlinx.android.synthetic.main.activity_main.sign_in_progressbar

class SignInActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
  private var googleApiClient: GoogleApiClient? = null
  private var gso: GoogleSignInOptions? = null
  private var mAuth: FirebaseAuth? = null
  private val RC_SIGN_IN: Int = 9001
  lateinit var signinViewModel: SigninViewModel
  private val TAG = "TAG"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    signinViewModel = ViewModelProviders.of(this)
        .get(SigninViewModel::class.java)
    setFullscreenLayout()
    animateHeader()
    prepareSignin()

    sign_in_button.setOnClickListener {

      toggleViews(true)
      signIn()
    }
  }

  private fun toggleViews(isSigningIn: Boolean) {
    if (isSigningIn) {
      sign_in_progressbar.visibility = View.VISIBLE
      sign_in_button.visibility = View.GONE
    } else {
      sign_in_progressbar.visibility = View.GONE
      sign_in_button.visibility = View.VISIBLE
    }

  }

  fun setFullscreenLayout() {
    // window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

  }

  private fun animateHeader() {
    if (header is ImageView) {
      val avdTop = AnimatedVectorDrawableCompat.create(
          applicationContext, R.drawable.avd_signin_header_top
      )

      val avd = AnimatedVectorDrawableCompat.create(
          applicationContext, R.drawable.avd_signin_header
      )

      (header as ImageView).setImageDrawable(avdTop)
      (header2 as ImageView).setImageDrawable(avd)
      (header3 as ImageView).setImageDrawable(avd)
      avdTop!!.start()
      avd!!.start()


      avd.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
          super.onAnimationEnd(drawable)
          avd.start()
        }
      })

      avdTop.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
          super.onAnimationEnd(drawable)
          avdTop.start()
        }
      })

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

  override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
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

        toggleViews(false)
      }
    }
  }

  private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {

    Log.d(TAG, "firebaseAuthWithGoogle:" + account?.id)

    val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
    mAuth?.signInWithCredential(credential)
        ?.addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
          if (task.isSuccessful) {

            val currentUser = Admin(task.result.user.email.toString())
            // Sign in success, update UI with the signed-in user's information
            Log.d(TAG, "signInWithCredential:success")
            signinViewModel.getEvents()
                .observe(this, Observer { adminsList ->
                  if (adminsList != null && adminsList.contains(currentUser)) {
                    doReveal()
                  } else {
                   showError()
                  }
                })

          } else {
            // If sign in fails, display a message to the user.
            Log.w(TAG, "signInWithCredential:failure", task.exception)
            Toast.makeText(
                this, "Authentication failed.",
                Toast.LENGTH_SHORT
            )
                .show()
            toggleViews(false)
          }

        })

  }

  private fun showError() {
    AlertDialog.Builder(this)
        .setTitle("Whoa there chief!")
        .setMessage(
            "This app is only for the event management team. You are unauthorized to log in"
        )
        .setPositiveButton("OK", { dialog, _ ->
          FirebaseAuth.getInstance().signOut()
          sign_in_progressbar.visibility =View.INVISIBLE
          sign_in_button.visibility = View.VISIBLE
          dialog.dismiss()
        })
        .show()
  }

  private fun startHomeActivity() {
    startActivity(Intent(this@SignInActivity, HomeActivity::class.java))
    finish()

  }

  private fun doReveal() {
    reveal_view.visibility = View.VISIBLE
    val cx = reveal_view.width
    val cy = reveal_view.height

    val progressBarWidth: Int = sign_in_progressbar.width / 2
    val startX: Int = (progressBarWidth + sign_in_progressbar.x).toInt()
    val startY: Int = (progressBarWidth + sign_in_progressbar.y).toInt()

    val finalRadius = Math.max(cx, cy) * 1.2f

    val revealAnimator = ViewAnimationUtils.createCircularReveal(
        reveal_view, startX, startY, progressBarWidth.toFloat(), finalRadius
    )

    revealAnimator.setDuration(550)
        .addListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            startActivity(Intent(this@SignInActivity, HomeActivity::class.java))
            finish()
          }
        })
    revealAnimator.start()
  }

  override fun onConnectionFailed(p0: ConnectionResult) {

    Log.d(TAG, "Signin Failed")
  }
}
