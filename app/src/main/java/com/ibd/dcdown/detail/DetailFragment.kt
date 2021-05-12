package com.ibd.dcdown.detail

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.transition.MaterialContainerTransform
import com.ibd.dcdown.R
import com.ibd.dcdown.databinding.FragmentDetailBinding
import com.ibd.dcdown.tools.Crawler
import com.ibd.dcdown.tools.Utility.themeColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DetailFragment : Fragment() {
    private val args: DetailFragmentArgs by navArgs()
    var adapter = DetailAdapter()
    lateinit var layoutManager: GridLayoutManager
    private lateinit var binding: FragmentDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = 400
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(view,resources.getString(R.string.detail_transition_name))
        layoutManager = GridLayoutManager(activity,3)
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = layoutManager

        val requestOptions =
            RequestOptions()
                .fitCenter()
        Glide.with(requireActivity())
            .load(args.image)
            .apply(requestOptions)
            .thumbnail(0.1f)
            .into(binding.packimg)

        CoroutineScope(Dispatchers.Default).launch {
            val data = Crawler.crawlCon(args.index)
            launch(Dispatchers.Main) {
                adapter.addData(data.data)
                binding.header.text = data.name

            }
        }
    }

}