package com.ibd.dcdown.main

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewGroupCompat
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.transition.MaterialElevationScale
import com.ibd.dcdown.R
import com.ibd.dcdown.databinding.FragmentNewlistBinding
import com.ibd.dcdown.tools.Crawler
import com.ibd.dcdown.tools.HeaderItemDecoration
import com.ibd.dcdown.tools.Utility.doExitTranslation
import com.ibd.dcdown.tools.Utility.doTabInit
import com.ibd.dcdown.tools.Utility.stopExitTranslation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class newlistFragment : Fragment(), ConPackAdapter.ConPackListener {

    lateinit var binding : FragmentNewlistBinding
    lateinit var adapter: ConPackAdapter
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ConPackAdapter(this,activity as MainActivity)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewlistBinding.inflate(inflater, container,false)
        ViewGroupCompat.setTransitionGroup(binding.recycler,true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        stopExitTranslation()

        (activity as MainActivity).doTabInit()

        layoutManager = LinearLayoutManager(activity)

        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = layoutManager
        binding.recycler.addItemDecoration(
            HeaderItemDecoration(
                binding.recycler,
                { adapter.packList[it] is String},
                { Toast.makeText(activity ,adapter.packList[it] as String, Toast.LENGTH_SHORT).show()}
            )
        )

        if(adapter.packList.isEmpty()) addNewList(1)
        binding.recycler.addOnScrollListener( object:  RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!adapter.loading)
                    if (layoutManager.findLastCompletelyVisibleItemPosition() >= adapter.packList.size-1) {
                        addNewList(++adapter.idx)
                        adapter.loading=true

                    }
            }
        })
    }


    private fun addNewList(idx:Int) = CoroutineScope(Dispatchers.Default).launch {
        val activity = activity as MainActivity
        launch(Dispatchers.Main) {
            activity.binding.loadingBar.visibility = View.VISIBLE
        }
        adapter.loading=true
        val baseUrl = "https://dccon.dcinside.com/new/$idx"
        val data = Crawler.crawlPack(baseUrl)
        if(data!=null){
            launch(Dispatchers.Main) {
                if (idx == 1) adapter.addData(data, "최신 디시콘")
                else adapter.addData(data)
            }
        }
        adapter.loading=false
        launch(Dispatchers.Main) {
            activity.binding.loadingBar.visibility = View.GONE
        }
    }

    override fun onPackClick(card:View, image:View, index: String, src:String?) {
        doExitTranslation()
        val cardTrans = getString(R.string.detail_transition_name)
        val extras = FragmentNavigatorExtras(card to cardTrans)
        val directions = newlistFragmentDirections.actionNewToDetail(index,src ?: "")
        findNavController().navigate(directions,extras)
        val activity = activity as MainActivity
        activity.binding.bar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
        activity.binding.bar.performHide()
        activity.binding.bar.visibility = View.GONE
    }
}