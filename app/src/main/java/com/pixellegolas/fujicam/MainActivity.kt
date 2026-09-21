package com.pixellegolas.fujicam
import android.graphics.*
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.pixellegolas.fujicam.databinding.ActivityMainBinding
import java.util.concurrent.Executors
import kotlin.random.Random
import android.content.ContentValues
import android.provider.MediaStore
import androidx.recyclerview.widget.GridLayoutManager
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
class MainActivity:AppCompatActivity(){
 private lateinit var b:ActivityMainBinding
 private var cap:ImageCapture?=null
 private val exec=Executors.newSingleThreadExecutor()
 private var cur=FilmRecipe.REGGIES_PORTRA
 private var last:Bitmap?=null
 private var live=false
 private val rnd=Random(System.nanoTime())
 override fun onCreate(s:Bundle?){
  super.onCreate(s)
  b=ActivityMainBinding.inflate(layoutInflater)
  setContentView(b.root)
  b.shutter?.setOnClickListener{take()}
  b.retakeBtn?.setOnClickListener{b.resultOverlay?.visibility=View.GONE}
  b.saveBtn?.setOnClickListener{save()}
  b.galleryBtn?.setOnClickListener{gallery()}
  b.liveCheck?.setOnCheckedChangeListener{_,c->live=c}
  b.infoNumber?.text="#01"
  b.infoName?.text="REGGIES"
  b.infoSim?.text="CLASSIC"
  start()
 }
 private fun start(){
  val f=ProcessCameraProvider.getInstance(this)
  f.addListener({
   val p=f.get()
   val prev=Preview.Builder().build().also{it.setSurfaceProvider(b.viewFinder.surfaceProvider)}
   cap=ImageCapture.Builder().build()
   val a=ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
   a.setAnalyzer(exec){img->img.close()}
   p.unbindAll()
   p.bindToLifecycle(this,androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA,prev,cap,a)
  },ContextCompat.getMainExecutor(this))
 }
 private fun take(){}
 private fun save(){}
 private fun gallery(){}
 private fun filter(src:Bitmap,withGlow:Boolean):Bitmap{return src}
}
