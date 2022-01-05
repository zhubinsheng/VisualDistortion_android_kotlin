package com.rino.visualdestortion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }
    private fun splashSetup(navController: NavController){
        CoroutineScope(Dispatchers.Default).launch{
            delay(3000)
            CoroutineScope(Dispatchers.Main).launch{
                navController.popBackStack()
                navController.navigate(R.id.shopFragment)
            }
        }
    }

}