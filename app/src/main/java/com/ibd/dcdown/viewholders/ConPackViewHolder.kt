package com.ibd.dcdown.viewholders

import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ibd.dcdown.R
import com.ibd.dcdown.databinding.DialogDownloadBinding
import com.ibd.dcdown.databinding.RecyclerConpackBinding
import com.ibd.dcdown.tools.C
import com.ibd.dcdown.tools.ConPack
import com.ibd.dcdown.tools.SaveUtil
import com.ibd.dcdown.tools.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConPackViewHolder(private val binding: RecyclerConpackBinding, onClick: (@C.ConPackClickType Int) -> Unit) {

    fun bind(data: ConPack) {
        val context = binding.root.context
        with(binding) {
            ViewCompat.setTransitionName(
                binding.card,
                context.getString(R.string.main_detail_transition_name, data.idx)
            )
            val requestOptions =
                RequestOptions()
                    .dontAnimate()
                    .fitCenter()

            Glide.with(context)
                .load(data.img)
                .apply(requestOptions)
                .thumbnail(0.1f)
                .into(binding.packimg)
            binding.packname.text = data.name
            binding.packauthor.text = data.author
            binding.packdown.setOnClickListener {
                if (Utility.checkRWPermission(activity)) {
                    val binding =
                        DialogDownloadBinding.inflate(LayoutInflater.from(mContext))

                    val mDialog = AlertDialog.Builder(mContext)
                        .setView(binding.root)
                        .setTitle("저장 방식 선택")
                        .show()

                    binding.justDown.setOnClickListener {
                        Toast.makeText(mContext, "다운로드를 시작합니다.", Toast.LENGTH_SHORT)
                            .show()
                        CoroutineScope(Dispatchers.Default).launch {
                            val res = SaveUtil.saveConPack(
                                mContext,
                                item,
                                archive = false
                            )
                            launch(Dispatchers.Main) {
                                Toast.makeText(
                                    mContext,
                                    "다운로드가 완료되었습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                        mDialog.dismiss()
                    }
                    binding.archiveDown.setOnClickListener {
                        Toast.makeText(mContext, "다운로드를 시작합니다.", Toast.LENGTH_SHORT)
                            .show()
                        CoroutineScope(Dispatchers.Default).launch {
                            val res =
                                SaveUtil.saveConPack(mContext, item, archive = true)
                            launch(Dispatchers.Main) {
                                Toast.makeText(
                                    mContext,
                                    "다운로드가 완료되었습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        mDialog.dismiss()
                    }
                } else
                    Utility.requestRWPermission(activity)
            }
        }
    }
}