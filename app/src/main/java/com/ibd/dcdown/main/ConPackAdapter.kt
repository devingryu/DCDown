package com.ibd.dcdown.main


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.Util
import com.ibd.dcdown.R
import com.ibd.dcdown.databinding.DialogDownloadBinding
import com.ibd.dcdown.databinding.RecyclerConpackBinding
import com.ibd.dcdown.databinding.RecyclerEndedBinding
import com.ibd.dcdown.databinding.RecyclerHeaderBinding
import com.ibd.dcdown.tools.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ConPackAdapter(private val listener: ConPackListener,val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ConPackListener {
        fun onPackClick(card: View,image:View, index: String, src:String?)
    }

    var packList: ArrayList<Any> = arrayListOf() // ConPack, String
    private lateinit var mContext: Context
    var loading = false
    var isEnded = false
    var idx = 1

    fun addData(data:ArrayList<ConPack>){
        packList.addAll(data)
        notifyDataSetChanged()
    }
    fun addData(data:ArrayList<ConPack>, header:String){
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
            C.VIEW_TYPE_ENDED -> EndedViewHolder(
                RecyclerEndedBinding.inflate(
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

            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = if(position == packList.lastIndex && !isEnded) 100 else 0
            holder.itemView.layoutParams = params

        if(position<packList.size){
            when (holder.itemViewType) {
                C.VIEW_TYPE_CONPACK -> {
                    with(holder as ConPackViewHolder){
                        (packList[position] as ConPack).let { item ->
                            ViewCompat.setTransitionName( binding.card , mContext.getString(R.string.main_detail_transition_name,item.idx))
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
                                if(Utility.checkRWPermission(activity)) {
                                    val binding =
                                        DialogDownloadBinding.inflate(LayoutInflater.from(mContext))

                                    val mDialog = AlertDialog.Builder(mContext)
                                        .setView(binding.root)
                                        .setTitle("저장 방식 선택")
                                        .show()

                                    binding.justDown.setOnClickListener {
                                        Toast.makeText(mContext, "다운로드를 시작합니다.", Toast.LENGTH_SHORT).show()
                                        CoroutineScope(Dispatchers.Default).launch {
                                            val res = SaveUtil.saveConPack(mContext, item, archive = false)
                                            launch(Dispatchers.Main) { Toast.makeText(mContext,"다운로드가 완료되었습니다.",Toast.LENGTH_SHORT).show() }

                                        }
                                        mDialog.dismiss()
                                    }
                                    binding.archiveDown.setOnClickListener {
                                        Toast.makeText(mContext, "다운로드를 시작합니다.", Toast.LENGTH_SHORT).show()
                                        CoroutineScope(Dispatchers.Default).launch {
                                            val res = SaveUtil.saveConPack(mContext, item, archive = true)
                                            launch(Dispatchers.Main) { Toast.makeText(mContext,"다운로드가 완료되었습니다.",Toast.LENGTH_SHORT).show() }
                                        }
                                        mDialog.dismiss()
                                    }
                                } else
                                    Utility.requestRWPermission(activity)
                            }
                            binding.parent.setOnClickListener {
                                listener.onPackClick(binding.card,binding.packimg,item.idx,item.img)
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
    }

    override fun getItemCount(): Int {
        return if (!isEnded)
            packList.size
        else
            packList.size+1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= packList.size) C.VIEW_TYPE_ENDED
        else if (packList[position] is ConPack) C.VIEW_TYPE_CONPACK
        else C.VIEW_TYPE_HEADER
    }

    inner class ConPackViewHolder(val binding: RecyclerConpackBinding) : RecyclerView.ViewHolder(binding.root)
    inner class HeaderViewHolder(val binding: RecyclerHeaderBinding) : RecyclerView.ViewHolder(binding.root)
    inner class EndedViewHolder(val binding: RecyclerEndedBinding) : RecyclerView.ViewHolder(binding.root)
}