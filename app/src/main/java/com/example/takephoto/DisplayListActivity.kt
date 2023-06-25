package com.example.takephoto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.takephoto.databinding.ActivityDisplayBinding
import com.example.takephoto.model.StyleRambutAdapter
import com.example.takephoto.model.StyleRambutViewModel

import com.example.takephoto.databinding.ActivityDisplayListBinding

class DisplayListActivity : AppCompatActivity() {

    private var _binding: ActivityDisplayListBinding? = null
    private val binding get() = _binding!!

    lateinit var styleRambutSVM : StyleRambutViewModel
    lateinit var styleRambutAdapter : StyleRambutAdapter

    private lateinit var styleRambutRecycleView : RecyclerView

    private val STORAGE_PERMISSION_CODE = 103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        _binding = ActivityDisplayListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        styleRambutSVM = ViewModelProvider(this).get(StyleRambutViewModel::class.java)
        
        var layoutManager = LinearLayoutManager(this)
        styleRambutRecycleView = binding.styleRambutListView
        styleRambutRecycleView.layoutManager = layoutManager
        styleRambutRecycleView.setHasFixedSize(true)
        
        styleRambutSVM.list_style.observe(this, ){
            Log.e("test", "di panggil")
            styleRambutRecycleView.adapter = StyleRambutAdapter(it)
        }
        
        binding.btnAddStyle.setOnClickListener {
            AddStyleFragment().show(supportFragmentManager, "newStyleTag")
        }

        if (!checkPermission()) {
            requestPermission()
        }
    }

    private fun checkPermission() : Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        }
        else{
            val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try{
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
            }
            catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            }
        }
        else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE)
        }
    }

}