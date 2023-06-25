package com.example.takephoto

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint.Style
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.takephoto.databinding.FragmentAddStyleBinding
import com.example.takephoto.model.StyleRambut
import com.example.takephoto.model.StyleRambutViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import android.Manifest

class AddStyleFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddStyleBinding? = null
    private val binding get() = _binding!!

    private val REQ_CAM = 100
    private var dataGambar : Bitmap? = null
    private var saved_image_url: String = ""

    private lateinit var styleRambutSVM: StyleRambutViewModel

    private val STORAGE_PERMISSION_CODE = 103


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        styleRambutSVM = ViewModelProvider(activity).get(StyleRambutViewModel::class.java)

        binding.BottomBtnCamera.setOnClickListener{
            openCamera()
        }

        binding.BottomBtnAddStyle.setOnClickListener{
            if(saved_image_url != ""){
                addStyle()
            }
        }

    }

    private fun openCamera(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            this.activity?.packageManager?.let {
                intent?.resolveActivity(it).also {
                    startActivityForResult(intent, REQ_CAM)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentAddStyleBinding.inflate(inflater, container  , false)
        return binding.root
    }

    private fun addStyle(){
        val nama_style = binding.BottomTxtNama.text.toString()
        val harga = binding.BottomTxtHarga.text.toString()
        val lama_potong = binding.BottomTxtLama.text.toString()

        val style_list = styleRambutSVM.list_style.value
        style_list?.add(StyleRambut(nama_style, harga.toDouble(), lama_potong.toInt(), saved_image_url))
        styleRambutSVM.list_style.value = style_list
        dismiss()
    }

    fun saveMediaToStorage(bitmap : Bitmap): String {
        val filename = "${System.currentTimeMillis()}.jpg"

        var fos : OutputStream? = null

        var image_save = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity?.contentResolver?.also { resolver ->

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)

                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                fos = imageUri?.let{resolver.openOutputStream(it)}

                image_save = "${Environment.DIRECTORY_PICTURES}/${filename}"
            }
        }
        else{
            val permission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE)
            }

            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)

            image_save = "${Environment.DIRECTORY_PICTURES}/${filename}"
        }
        fos?.use{
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return image_save
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQ_CAM && resultCode == AppCompatActivity.RESULT_OK) {
            dataGambar = data?.extras?.get("data") as Bitmap
            val image_save_uri: String = saveMediaToStorage(dataGambar!!)
            saved_image_url = image_save_uri
        }
    }
}