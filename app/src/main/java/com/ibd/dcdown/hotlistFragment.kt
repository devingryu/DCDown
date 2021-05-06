package com.ibd.dcdown

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibd.dcdown.databinding.FragmentHotlistBinding
import com.ibd.dcdown.tools.Crawler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class hotlistFragment : Fragment() {

    private lateinit var binding: FragmentHotlistBinding
    lateinit var adapter: ConPackAdapter
    lateinit var layoutManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ConPackAdapter()
        layoutManager = LinearLayoutManager(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHotlistBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = layoutManager
        binding.recycler.addItemDecoration(
            HeaderItemDecoration(
                binding.recycler,
                { adapter.packList[it] is String},
                { Toast.makeText(activity ,adapter.packList[it] as String, Toast.LENGTH_SHORT).show()}
            )
        )
        addHotList(1)
    }
    private fun addHotList(idx:Int) = CoroutineScope(Dispatchers.Default).launch {
        val baseUrl = "https://dccon.dcinside.com/hot/$idx"
        val data = Crawler.crawlPack(baseUrl)
        if(data!=null){
            launch(Dispatchers.Main) {
                if (idx == 1) adapter.addData(data, "인기 디시콘")
                else adapter.addData(data)
            }
        }
    }
}