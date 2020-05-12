package com.jianjun.dotindicator

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.jianjun.dotindicator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
        initViewPager2()
        initViewPager()
    }

    private fun initViewPager() {
        binding.viewpager.adapter = object : PagerAdapter() {
            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view == `object`
            }

            override fun getCount(): Int {
                return 5
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val view = TextView(container.context)
                view.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                view.setBackgroundColor(Color.BLACK)
                view.setTextColor(Color.WHITE)
                view.gravity = Gravity.CENTER
                view.textSize = 50f
                view.text = position.toString()
                container.addView(view, 0)
                return view
            }

            override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
                (view as View?)?.let {
                    container.removeView(it)
                }
            }
        }
        binding.indicator.attach(binding.viewpager, object : DotIndicator.AbsIndicatorAdapter() {
            override fun createItemView(pos: Int): View {
                val textView = TextView(this@MainActivity)
                textView.setTextColor(Color.WHITE)
                textView.text = pos.toString()
                textView.gravity = Gravity.CENTER
                textView.textSize = 16f
                val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
                textView.layoutParams = params
                return textView
            }

            override fun onPagerChanged(pos: Int, view: View, isSelected: Boolean) {
                val textView = view as TextView
                textView.setTextColor(if (isSelected) Color.WHITE else Color.GRAY)
            }

        })
    }

    private fun initViewPager2() {
        binding.viewpager2.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val view = TextView(parent.context)
                view.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                view.setBackgroundColor(Color.BLACK)
                view.setTextColor(Color.WHITE)
                view.gravity = Gravity.CENTER
                view.textSize = 50f
                return ViewHolder(view)
            }

            override fun getItemCount(): Int = 5

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as TextView).text = "$position"
            }

            inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            }
        }
        binding.indicator2.attach(binding.viewpager2, object : DotIndicator.AbsIndicatorAdapter() {
            override fun createItemView(pos: Int): View {
                val textView = TextView(this@MainActivity)
                textView.setTextColor(Color.WHITE)
                textView.text = pos.toString()
                textView.gravity = Gravity.CENTER
                textView.textSize = 16f
                val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
                textView.layoutParams = params
                return textView
            }

            override fun onPagerChanged(pos: Int, view: View, isSelected: Boolean) {
                val textView = view as TextView
                textView.setTextColor(if (isSelected) Color.WHITE else Color.GRAY)
            }

        })

    }
}
