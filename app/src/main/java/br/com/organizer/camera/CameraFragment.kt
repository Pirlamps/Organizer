package br.com.organizer.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import br.com.organizer.R
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.Loggers.*
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.parameter.selector.FocusModeSelectors.*
import io.fotoapparat.parameter.selector.LensPositionSelectors.back
import io.fotoapparat.parameter.selector.Selectors.firstAvailable
import io.fotoapparat.parameter.selector.SizeSelectors.biggestSize
import io.fotoapparat.view.CameraView
import java.io.File
import java.util.*

class CameraFragment : Fragment() {

    companion object {

        fun newInstance(): CameraFragment {
            val args = Bundle()
            val fragment = CameraFragment()

            fragment.arguments = args

            return fragment
        }
    }

    private lateinit var cameraPreview: CameraView
    private lateinit var fotoApparat: Fotoapparat
    private val REQUEST_CAMERA_PERMISSION = 200

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_camera, container, false)

        setupCamera(view!!)
        requestPermissionsForCamera()
        configFotoApparat()


        return view
    }

    private fun requestPermissionsForCamera() {
        // Add permission for camera and let user grant the permission
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION)
            return
        }
    }

    private fun setupCamera(view: View) {

        cameraPreview = view.findViewById(R.id.camera_preview)
        val cameraButton = view.findViewById<Button>(R.id.camera_button)

        cameraButton.setOnClickListener({
            takePicture()
        })

    }

    private fun configFotoApparat() {
        fotoApparat = Fotoapparat
                .with(activity)
                .into(cameraPreview)
                .previewScaleType(ScaleType.CENTER_CROP)
                .photoSize(biggestSize())
                .lensPosition(back())
                .focusMode(firstAvailable(
                        continuousFocus(),
                        autoFocus(),
                        fixed()))
//                .flash(firstAvailable(
//                        autoRedEye(),
//                        autoFlash(),
//                        torch()
//                ))
                .logger(loggers(
                        logcat(),
                        fileLogger(activity)
                ))
                .build()
    }

    private fun takePicture() {
        val photoResult = fotoApparat.takePicture()
        photoResult.saveToFile(getFilePath())
    }

    private fun getFilePath(): File {
        val path = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Organizer")
        path.mkdirs()
        return File(path, "OrganizerPic" + Date().time + ".jpg")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            activity.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        fotoApparat.start()
    }

    override fun onStop() {
        super.onStop()
        fotoApparat.stop()
    }
}