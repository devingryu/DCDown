package com.ibd.dcdown.main

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.ibd.dcdown.tools.ConPack
import com.ibd.dcdown.R
import com.ibd.dcdown.databinding.FragmentSearchBinding
import com.ibd.dcdown.tools.Crawler
import com.ibd.dcdown.tools.Utility.doExitTranslation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragment : Fragment(), ConPackAdapter.ConPackListener{

    lateinit var binding: FragmentSearchBinding
    lateinit var adapter: ConPackAdapter
    lateinit var layoutManager: LinearLayoutManager
    private var searchWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ConPackAdapter(this,activity as MainActivity)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity
        activity.binding.searchBar.visibility = View.VISIBLE
        activity.binding.mainMenu.visibility = View.GONE
        activity.binding.bar.performShow()
        activity.isSearch = true
        activity.binding.fab.hide()


        layoutManager = LinearLayoutManager(activity)

        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = layoutManager
        binding.backButton.setOnClickListener {
            activity.onBackPressed()
        }

        binding.recycler.addOnScrollListener( object:  RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!adapter.loading && !adapter.isEnded)
                    if (layoutManager.findLastCompletelyVisibleItemPosition() >= adapter.packList.size-1) {
                        onSearch(null,true)
                        adapter.loading=true
                    }
            }
        })
    }
    fun onSearch(query: String?,isHot:Boolean) = CoroutineScope(Dispatchers.Default).launch {
        val activity = activity as MainActivity
        launch(Dispatchers.Main) {
            activity.binding.loadingBar.visibility = View.VISIBLE
        }

        val hot = if(isHot) "hot" else "new"
        val res: ArrayList<ConPack>?
        if (query == null) {
            val index = ++adapter.idx
            res = Crawler.crawlPack("https://dccon.dcinside.com/$hot/$index/title/$searchWord")
        }
        else{
            launch(Dispatchers.Main) {
                adapter.reset(null)
            }
            searchWord = query
            adapter.idx = 1
            res = Crawler.crawlPack("https://dccon.dcinside.com/$hot/1/title/$searchWord")
        }

        launch(Dispatchers.Main) {
            activity.binding.loadingBar.visibility = View.GONE
            adapter.loading=false
        }
        if(res!=null)
            launch(Dispatchers.Main) {
                if(res.isNotEmpty())
                    adapter.addData(res)
                else {
                    adapter.isEnded = true
                    adapter.notifyDataSetChanged()
                }
            }
    }

    override fun onPackClick(card: View, image: View, index: String, src: String?) {
        doExitTranslation()
        val cardTrans = getString(R.string.detail_transition_name)
        val extras = FragmentNavigatorExtras(card to cardTrans)
        val directions = SearchFragmentDirections.actionGlobalDetailFragment(index,src ?: "")
        findNavController().navigate(directions,extras)
        val activity = activity as MainActivity
        activity.binding.bar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
        activity.binding.bar.performHide()
        activity.binding.bar.visibility = View.GONE
    }
}