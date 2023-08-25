package com.ibd.dcdown.main

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibd.dcdown.R
import com.ibd.dcdown.databinding.ActivityMainBinding
import com.ibd.dcdown.tools.ConPack
import com.ibd.dcdown.tools.Extensions.repeatOnStarted
import com.ibd.dcdown.tools.Utility.hideKeyboard
import com.ibd.dcdown.viewmodels.main.MainViewModel
import com.ibd.dcdown.viewmodels.main.MainViewModel.E
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var layoutManager: LinearLayoutManager
    var isSearch = false
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        layoutManager = LinearLayoutManager(this)

        initObservers()

        binding.fab.setOnClickListener {
            if (!isSearch)
                findNavController(R.id.nav_host_fragment).navigate(SearchFragmentDirections.actionGlobalSearchFragment())
        }
        binding.searchText.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                doSearch(v.text.toString())
                hideKeyboard()
                true
            } else
            false
        }
        binding.searchButton.setOnClickListener {
            doSearch(binding.searchText.text.toString())
            hideKeyboard()
        }
    }
    fun changeTab(view: View){
        val n = when(view.id){
            R.id.back_button ->0
            else -> 1
        }
        val menu = arrayListOf(binding.menuHot,binding.menuNew,binding.menuRecent,binding.menuSettings)
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
    private fun getCurrentFragment() : Fragment?{
        val navHostFragment : Fragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }
    private fun doSearch(it: String){
        if(it!=""){
            Toast.makeText(this,"검색어: $it",Toast.LENGTH_SHORT).show()
            (getCurrentFragment()!! as SearchFragment).onSearch(binding.searchText.text.toString(),true)
        }
        else
            Toast.makeText(this,"검색어를 입력하세요!",Toast.LENGTH_SHORT).show()
    }

    private fun initObservers() {
        repeatOnStarted {
            launch {
                viewModel.eventChannel.collect {
                    when(it) {
                        is E.Toast -> {
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                        }
                    }
                }
            }
            launch {
                viewModel.hotList.collect {

                }
            }
        }
    }

    private val context: Context get() = this
}