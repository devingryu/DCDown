package com.ibd.dcdown.detail


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ibd.dcdown.tools.ConData
import com.ibd.dcdown.databinding.RecyclerConimgBinding
import com.ibd.dcdown.databinding.RecyclerHeaderBinding
import com.ibd.dcdown.tools.C


class DetailAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val columns = 3
    private var width = -1

    var packList: ArrayList<Any> = arrayListOf() // ConPack, String
    private lateinit var mContext: Context


    fun addData(data:ArrayList<ConData>){
        packList.addAll(data)
        notifyDataSetChanged()
    }
    fun addData(data:ArrayList<ConData>, header:String){
        packList.add(header)
        packList.addAll(data)
        notifyDataSetChanged()
    }

    fun reset(data:ArrayList<Any>?){
        packList.clear()
        if(data!=null)
            packList.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        width = parent.width
        return when(viewType){
            C.VIEW_TYPE_CONIMG -> ConImgViewHolder(
                RecyclerConimgBinding.inflate(
                    LayoutInflater.from(mContext),
                    parent,
                    false
                )
            )
            else -> HeaderViewHolder(
                RecyclerHeaderBinding.inflate(
                    LayoutInflater.from(mContext),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder.itemViewType){
            C.VIEW_TYPE_CONIMG -> {
                with(holder as DetailAdapter.ConImgViewHolder){
                    (packList[position] as ConData).let { item ->
                        binding.con.layoutParams.width = width/columns
                        binding.con.layoutParams.height = width/columns
                        binding.checkBox.isChecked = item.selected
                        val requestOptions =
                            RequestOptions()
                                .fitCenter()

                        Glide.with(mContext)
                            .load("https://dcimg5.dcinside.com/dccon.php?no=${item.uri}")
                            .apply(requestOptions)
                            .thumbnail(0.1f)
                            .into(binding.con)
                        binding.con.setOnClickListener {
                            item.selected = !item.selected
                            binding.checkBox.isChecked = item.selected
                        }
                    }
                }
            }
            C.VIEW_TYPE_HEADER -> {
                with(holder as DetailAdapter.HeaderViewHolder) {
                    (packList[position] as String).let { item ->
                        binding.header.text = item
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int = packList.size

    override fun getItemViewType(position: Int): Int {
        return if (packList[position] is ConData) C.VIEW_TYPE_CONIMG
        else C.VIEW_TYPE_HEADER
    }

    inner class ConImgViewHolder(val binding: RecyclerConimgBinding) : RecyclerView.ViewHolder(binding.root)
    inner class HeaderViewHolder(val binding: RecyclerHeaderBinding) : RecyclerView.ViewHolder(binding.root)
}