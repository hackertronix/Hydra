package io.execube.monotype.deimos.about

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.View
import android.widget.ScrollView
import com.binaryfork.spanny.Spanny
import com.facebook.shimmer.ShimmerFrameLayout
import io.execube.monotype.deimos.R
import io.execube.monotype.deimos.Utils.getLinearOutSlowInInterpolator
import kotlinx.android.synthetic.main.activity_about.book_now
import kotlinx.android.synthetic.main.activity_about.container
import kotlinx.android.synthetic.main.activity_about.links_tv
import kotlinx.android.synthetic.main.activity_about.repo_tv
import kotlinx.android.synthetic.main.activity_about.shimmer_view
import kotlinx.android.synthetic.main.activity_about.sponsors_rv

class AboutActivity : AppCompatActivity() {

  private lateinit var listOfDrawableIds: Array<Int>
  lateinit var adapter: SponsorsAdapter
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_about)
    container.fullScroll(View.FOCUS_UP)
    animateCategory()
    setupShimmer()
    setupArrayOfDrawableIds()
    setupRecyclerView()
    val spannedText= Spanny("Twitter",URLSpan("https://twitter.com/hackertronix"))
        .append(" | ")
        .append("Website",URLSpan("https://hackertronix.com"))

    val githubLink= Spanny("The source code will be available on ")
        .append("Github",URLSpan("https://github.com/hackertronix"))


    repo_tv.text = githubLink
    links_tv.text = spannedText
    links_tv.movementMethod = LinkMovementMethod.getInstance()
    repo_tv.movementMethod = LinkMovementMethod.getInstance()


    book_now.setOnClickListener {
      val uri = Uri.parse("https://bit.ly/rnsitsupersonic")
      val intent = Intent(Intent.ACTION_VIEW,uri)
      startActivity(intent)
    }
  }

  private fun animateCategory() {
    val container = container as ScrollView

    // fade in and space out the title.  Animating the letterSpacing performs horribly so
    // fake it by setting the desired letterSpacing then animating the scaleX
    container.alpha = 0f
    container.translationY = 90f

    container.animate()
        .alpha(1f)
        .setStartDelay(200)
        .translationY(0f)
        .setDuration(700L)
        .setInterpolator(getLinearOutSlowInInterpolator(this))

        .start()
  }
  private fun setupShimmer() {
    shimmer_view.angle = ShimmerFrameLayout.MaskAngle.CW_0
    shimmer_view.tilt = 0.0f
    shimmer_view.intensity = 0.0f
    shimmer_view.baseAlpha = 0.4f
    shimmer_view.duration = 1000
    shimmer_view.startShimmerAnimation()

  }

  private fun setupRecyclerView() {
    val linearLayoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)
    adapter = SponsorsAdapter(listOfDrawableIds)
    sponsors_rv.layoutManager = linearLayoutManager
    sponsors_rv.adapter = adapter
  }

  private fun setupArrayOfDrawableIds() {
    listOfDrawableIds = arrayOf(R.drawable.a,
        R.drawable.b,
        R.drawable.c,
        R.drawable.d,
        R.drawable.e,
        R.drawable.f,
        R.drawable.g,
        R.drawable.h,
        R.drawable.i,
        R.drawable.j,
        R.drawable.k,
        R.drawable.l,
        R.drawable.m,
        R.drawable.n,
        R.drawable.o,
        R.drawable.p,
        R.drawable.q,
        R.drawable.r)
  }
}
