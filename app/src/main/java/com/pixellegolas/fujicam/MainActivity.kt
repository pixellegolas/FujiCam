package com.pixellegolas.fujicam

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pixellegolas.fujicam.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService

    data class FujiRecipe(val id: String, val name: String, val sim: String, val grain: String, val wb: String)
    private val recipes = listOf(
        FujiRecipe("cc", "Classic Chrome", "Classic Chrome", "Weak", "Auto"),
        FujiRecipe("ccn", "CCN Natural", "Classic Chrome", "Off", "5200K"),
        FujiRecipe("nc", "Nostalgic Neg", "Nostalgic Neg.", "Weak", "Auto"),
        FujiRecipe("nh", "Natural Vivid", "ETERNA Vivid", "Off", "Auto"),
        FujiRecipe("acros", "ACROS+R", "ACROS+R", "Strong", " - "),
        FujiRecipe("portra", "Portra 400", "PRO Neg Hi", "Weak", "5600K"),
        FujiRecipe("kodak", "Kodak Gold", "Classic Chrome", "Strong", "5500K")
    )
    private var currentRecipe = recipes[1]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cameraExecutor = Executors.newSingleThreadExecutor()
        if (allPermissionsGranted()) startCamera() else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)
        
        // v12.1 fix: safe calls for nullable binding
        binding.shutter?.setOnClickListener { takePhoto() }
        binding.recipeDropdown?.setOnClickListener { cycleRecipe() }
        binding.galleryCount?.setOnClickListener {
            Toast.makeText(this, "Gallery: ${currentRecipe.name}", Toast.LENGTH_SHORT).show()
        }
        binding.galleryBtn?.setOnClickListener {
            Toast.makeText(this, "Gallery: ${currentRecipe.name}", Toast.LENGTH_SHORT).show()
        }
        binding.postProcessBtn?.setOnClickListener {
            Toast.makeText(this, "Post: ${currentRecipe.name}", Toast.LENGTH_SHORT).show()
        }
        updateUI()
    }

    private fun updateUI() {
        binding.recipeDropdown?.text = "${currentRecipe.name} ∨"
        binding.topLeftChip?.text = "● ${currentRecipe.sim.uppercase()}  |  ${currentRecipe.grain.uppercase()} GRAIN"
        binding.exposureInfo?.text = "f/2.0  1/250s  A-ISO ${(100..1600).random()}"
        binding.awbInfo?.text = "${currentRecipe.wb}  ◫ Porträtt"
        binding.recipeLabel?.text = "RECIPE  ${currentRecipe.id.uppercase()}"
        binding.aspectInfo?.text = "2:3 AUTO"
        binding.evLabel?.text = "HIST  EV 0.0"
    }

    private fun cycleRecipe() {
        val idx = recipes.indexOf(currentRecipe)
        currentRecipe = recipes[(idx+1) % recipes.size]
        updateUI()
        Toast.makeText(this, "Recipe: ${currentRecipe.name}", Toast.LENGTH_SHORT).show()
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            val provider = providerFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(binding.viewFinder.surfaceProvider) }
            imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build()
            val selector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                provider.unbindAll()
                camera = provider.bindToLifecycle(this, selector, preview, imageCapture)
            } catch(e: Exception) { e.printStackTrace() }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val ic = imageCapture ?: return
        binding.shutter?.animate()?.scaleX(0.85f)?.scaleY(0.85f)?.setDuration(80)?.withEndAction {
            binding.shutter?.animate()?.scaleX(1f)?.scaleY(1f)?.setDuration(80)?.start()
        }?.start()
        binding.histogramView?.bump()
        Toast.makeText(this, "Shot • ${currentRecipe.name}", Toast.LENGTH_SHORT).show()
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    override fun onRequestPermissionsResult(rc: Int, perms: Array<String>, res: IntArray) {
        super.onRequestPermissionsResult(rc, perms, res)
        if (rc==10 && allPermissionsGranted()) startCamera()
    }
    override fun onDestroy() { super.onDestroy(); cameraExecutor.shutdown() }
}