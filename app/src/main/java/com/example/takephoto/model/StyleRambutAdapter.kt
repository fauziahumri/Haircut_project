package com.example.takephoto.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.takephoto.R
import com.google.android.material.imageview.ShapeableImageView
import java.io.File

class StyleRambutAdapter (private val styleRambutList: ArrayList<StyleRambut>) : RecyclerView.Adapter<StyleRambutAdapter.StyleRambutViewHolder>(){

    class StyleRambutViewHolder (stylerambutitemView: View): RecyclerView.ViewHolder(stylerambutitemView){
        val style_image : ShapeableImageView = stylerambutitemView.findViewById(R.id.style_list_image)
        val style_nama : TextView = stylerambutitemView.findViewById(R.id.style_list_nama)
        val style_harga : TextView = stylerambutitemView.findViewById(R.id.style_list_harga)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleRambutViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.style_rambut_list_item, parent, false)
        return StyleRambutViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return styleRambutList.size
    }

    override fun onBindViewHolder(holder: StyleRambutViewHolder, position: Int) {
        val currentItem = styleRambutList[position]
        val foto_dir = currentItem.foto_dir.toString()

        val imgFile = File("${Environment.getExternalStorageDirectory()}/${foto_dir}")
        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

        holder.style_image.setImageBitmap(myBitmap)
        holder.style_nama.text = currentItem.style
        holder.style_harga.text = currentItem.harga.toString()
    }
}