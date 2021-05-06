package com.ibd.dcdown

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibd.dcdown.databinding.FragmentNewlistBinding
import com.ibd.dcdown.tools.Crawler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class newlistFragment : Fragment() {

    lateinit var binding : FragmentNewlistBinding
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
        binding = FragmentNewlistBinding.inflate(inflater, container,false)
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
        addNewList(1)
    }


    private fun addNewList(idx:Int) = CoroutineScope(Dispatchers.Default).launch {
        val baseUrl = "https://dccon.dcinside.com/new/$idx"
        val data = Crawler.crawlPack(baseUrl)
        if(data!=null){
            launch(Dispatchers.Main) {
                if (idx == 1) adapter.addData(data, "최신 디시콘")
                else adapter.addData(data)
            }
        }
    }
}