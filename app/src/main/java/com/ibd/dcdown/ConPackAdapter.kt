package com.ibd.dcdown


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ibd.dcdown.databinding.RecyclerConpackBinding
import com.ibd.dcdown.databinding.RecyclerHeaderBinding
import com.ibd.dcdown.tools.C


class ConPackAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var packList: ArrayList<Any> = arrayListOf() // ConPack, String
    private lateinit var mContext: Context

    fun addData(data:ArrayList<ConPack>){
        packList.addAll(data)
        notifyDataSetChanged()
    }
    fun addData(data:ArrayList<ConPack>,header:String){
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

        return when(viewType){
            C.VIEW_TYPE_CONPACK -> ConPackViewHolder(
                RecyclerConpackBinding.inflate(
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
        if (position == packList.lastIndex){
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 100
            holder.itemView.layoutParams = params
        }else{
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 0
            holder.itemView.layoutParams = params
        }

        when (holder.itemViewType) {
            C.VIEW_TYPE_CONPACK -> {
                with(holder as ConPackViewHolder){
                    (packList[position] as ConPack).let { item ->
                        val requestOptions =
                            RequestOptions()
                                .dontAnimate()
                                .fitCenter()

                        Glide.with(mContext)
                            .load(item.img)
                            .apply(requestOptions)
                            .thumbnail(0.1f)
                            .into(binding.packimg)
                        binding.packname.text = item.name
                        binding.packauthor.text = item.author
                        binding.packdown.setOnClickListener {
                            // TODO: Download
                        }
                        binding.parent.setOnClickListener {
                            // TODO: Open
                        }
                    }
                }
            }
            else -> {
                with(holder as HeaderViewHolder){
                    (packList[position] as String).let { item ->
                        binding.header.text = item
                    }
                }
            }
        }


    }

    override fun getItemCount(): Int = packList.size

    override fun getItemViewType(position: Int): Int {
        return if (packList[position] is ConPack) C.VIEW_TYPE_CONPACK
        else C.VIEW_TYPE_HEADER
    }

    inner class ConPackViewHolder(val binding: RecyclerConpackBinding) : RecyclerView.ViewHolder(binding.root)
    inner class HeaderViewHolder(val binding: RecyclerHeaderBinding) : RecyclerView.ViewHolder(binding.root)
}