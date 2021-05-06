package com.ibd.dcdown

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibd.dcdown.databinding.ActivityMainBinding
import kotlin.reflect.KClass


class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    var adapter: ConPackAdapter = ConPackAdapter()
    lateinit var layoutManager: LinearLayoutManager
    var mode = 0
    var savedFragment : ArrayList<Pair<Int,ArrayList<ConPack>>?> = arrayListOf(null,null,null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.activity = this
        layoutManager = LinearLayoutManager(this)

    }
    fun changeTab(view: View){
        val n = when(view.id){
            R.id.menu_hot->0
            else -> 1
        }
        val menu = arrayListOf(binding.menuHot,binding.menuNew,binding.menuRecent,binding.menuUser)
        menu.forEachIndexed { index, imageButton ->
            imageButton.colorFilter = null
            imageButton.setColorFilter(
                resources.getColor(
                    if(index==n) R.color.point else R.color.secondary
                )
            )
        }
        findNavController(R.id.nav_host_fragment).navigate(when(n){
                0->hotlistFragmentDirections.actionGlobalHotlistFragment()
                else->newlistFragmentDirections.actionGlobalNewlistFragment()
            }
        )



    }
    fun getCurrentFragment() : Fragment?{
        val navHostFragment : Fragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }
}