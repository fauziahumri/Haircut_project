package com.example.takephoto

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.takephoto.databinding.ActivityMainBinding

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import android.Manifest

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val CHANNEL_ID = "ch_notif"
    private val notificationId = 100

    private val REQ_CAM = 101
    private var dataGambar: Bitmap? = null

    private var saved_image_url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()

        binding.ImgPict.setOnClickListener{
            openCamera()
        }

        binding.BtnSubmit.setOnClickListener{
            if(saved_image_url != "") {
                val intentDisplay = Intent(this, DisplayActivity::class.java)

                val style : String = binding.TxtName.text.toString()
                val harga : String = binding.TxtHarga.text.toString()
                val lama_potong : String = binding.TxtLamaPotong.text.toString()

                intentDisplay.putExtra("style", style)
                intentDisplay.putExtra("harga", harga)
                intentDisplay.putExtra("lama_potong", lama_potong)
                intentDisplay.putExtra("saved_image_url", saved_image_url)

                startActivity(intentDisplay)
            }
        }
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Registrasi Notification"
            val descriptionText = "Data Telah Tersimpan"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply{
                description = descriptionText
            }

            val notificationManager: NotificationManager = this.getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(notif_msg: String){
        val intent = Intent(this, MainActivity:: class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent,0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notification_24)
            .setContentTitle("Foto")
            .setContentText("${notif_msg} is saved")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

       if(ActivityCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
           with(NotificationManagerCompat.from(this)) {
               notify(notificationId, builder.build( ))
           }
       }
    }

    fun saveMediaToStorage(bitmap: Bitmap) : String {
        val filename = "${System.currentTimeMillis()}.jpg"

        var fos: OutputStream? = null

        var image_save = " "

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

            this.contentResolver?.also { resolver ->

                val contentValues = ContentValues().apply{

                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                fos = imageUri?.let { resolver.openOutputStream(it)}

                image_save = "${Environment.DIRECTORY_PICTURES}/${filename}"
            }
        }
        else{
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use{
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return image_save
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            this.packageManager?.let{
                intent?.resolveActivity(it).also{
                    startActivityForResult(intent, REQ_CAM)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CAM && resultCode == RESULT_OK){
            dataGambar = data?.extras?.get("data") as Bitmap
            val image_save_uri: String = saveMediaToStorage(dataGambar!!)
            saved_image_url = image_save_uri
            sendNotification(image_save_uri)
            binding.ImgPict.setImageBitmap(dataGambar)
        }
    }
}