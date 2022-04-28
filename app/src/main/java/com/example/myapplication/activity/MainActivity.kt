package com.example.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.transition.Fade
import androidx.transition.TransitionSet
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.fragment.MainFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainFragment = MainFragment()
        val transitionSet = TransitionSet()
        transitionSet.addTransition(Fade())
        mainFragment.enterTransition = transitionSet
        mainFragment.exitTransition = transitionSet
        val transaction = supportFragmentManager.beginTransaction()
        transaction
            .replace(R.id.container_main_activity,mainFragment)
            .addToBackStack(null)
            .commit()
    }
}
