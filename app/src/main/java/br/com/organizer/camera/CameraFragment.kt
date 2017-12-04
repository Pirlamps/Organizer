package br.com.organizer.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import android.util.Size
import android.view.*
import android.view.TextureView.SurfaceTextureListener
import android.widget.Button
import br.com.organizer.R
import java.io.*
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

    private lateinit var textureListener: SurfaceTextureListener
    private lateinit var stateCallback: CameraDevice.StateCallback
    private lateinit var captureCallback: CameraCaptureSession.CaptureCallback
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var captureRequest: CaptureRequest
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var cameraPreview: TextureView
    private var cameraId: String? = null
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private var imageDimension: Size? = null
    private var cameraDevice: CameraDevice? = null
    private val file: File? = null

    private val REQUEST_CAMERA_PERMISSION = 200


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_camera, container, false)

        configViews()
        setupCamera(view!!)

        return view
    }

    fun setupCamera(view: View) {

        cameraPreview = view.findViewById(R.id.camera_preview)
        val cameraButton = view.findViewById<Button>(R.id.camera_button)

        cameraPreview.surfaceTextureListener = textureListener
        cameraButton.setOnClickListener({
            tekePicture()
        })

    }

    private fun openCamera() {
        val manager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = manager.cameraIdList[0]
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            imageDimension = map.getOutputSizes(SurfaceTexture::class.java)[0]
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION)
                return
            }
            manager.openCamera(cameraId, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    private fun createCameraPreview() {
        try {
            val texture = cameraPreview.surfaceTexture
            texture.setDefaultBufferSize(imageDimension!!.width, imageDimension!!.height)
            val surface = Surface(texture)
            captureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)!!
            captureRequestBuilder.addTarget(surface)
            cameraDevice?.createCaptureSession(arrayListOf(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(captureSession: CameraCaptureSession?) {
                    cameraDevice?.let {
                        cameraCaptureSession = captureSession!!
                        updatePreview()

                    }
                }

                override fun onConfigureFailed(p0: CameraCaptureSession?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            }, null)

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun updatePreview() {

        cameraDevice?.let {
            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
            try {
                cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }

        }
    }

    private fun tekePicture() {
//        cameraDevice?.let {
//            print("cameraDevice is null")
//            return
//        }

        if (cameraDevice == null) {
            print("cameraDevice is null")
            return
        }


        val manager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            val characteristics = manager.getCameraCharacteristics(cameraDevice!!.id)
            var jpegSize = characteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)?.getOutputSizes(ImageFormat.JPEG)

            var width = 640
            var height = 480

            jpegSize?.let {
                if (it.isNotEmpty()) {
                    width = it[0].width
                    height = it[0].height
                }

            }

            val reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
            val outputSurfaces = ArrayList<Surface>(2)

            outputSurfaces.add(reader.surface)
            outputSurfaces.add(Surface(cameraPreview.surfaceTexture))

            val captureBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder?.addTarget(reader.surface)
            captureBuilder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

            val path = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Organizer")
            path.mkdirs()
            val file = File(path,"OrganizerPic1.jpg")
            Log.d("TESTE", "salvando em " + Environment.DIRECTORY_DCIM + "/pic.jpg")
            val readerListener = object : ImageReader.OnImageAvailableListener {
                override fun onImageAvailable(imageReader: ImageReader?) {
                    var image: Image? = null
                    try {
                        image = imageReader!!.acquireLatestImage()
                        val buffer = image.planes[0].buffer.rewind()
                        val bytes = ByteArray(buffer.remaining())
                        save(bytes)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        image?.close()
                    }
                }

                private fun save(bytes: ByteArray){
                    var output: OutputStream? = null
                    try {

                        output = FileOutputStream(file)
                        output.write(bytes)
                    } catch (e: Exception) {
                        Log.d("TESTE", "Deu ruim ;(")
                        e.printStackTrace()
                    } finally {
                        output?.close()
                    }
                }
            }

            reader.setOnImageAvailableListener(readerListener, backgroundHandler)
            val captureListener = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(session: CameraCaptureSession?, request: CaptureRequest?, result: TotalCaptureResult?) {
                    super.onCaptureCompleted(session, request, result)
                    print("Saved: " + file)
                    createCameraPreview()

                }
            }

            cameraDevice?.createCaptureSession(outputSurfaces, object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession?) {
                    try {
                        session?.capture(captureBuilder?.build(), captureListener, backgroundHandler)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }

                override fun onConfigureFailed(p0: CameraCaptureSession?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            }, backgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }


    }


    fun configViews() {

        textureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?) = false

            override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
                openCamera()
            }

        }

        stateCallback = object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice?) {
                cameraDevice = camera
                createCameraPreview()
            }

            override fun onDisconnected(p0: CameraDevice?) {
                cameraDevice?.close()
            }

            override fun onError(p0: CameraDevice?, p1: Int) {
                cameraDevice?.close()
                cameraDevice = null
            }
        }

        captureCallback = object : CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(session: CameraCaptureSession?, request: CaptureRequest?, result: TotalCaptureResult?) {
                super.onCaptureCompleted(session, request, result)
                print("Saved: " + file)
                createCameraPreview()
            }
        }
    }


    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("Camera Background")
        backgroundThread?.start()
        backgroundHandler = Handler(backgroundThread?.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            activity.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (cameraPreview.isAvailable) {
            openCamera()
        } else {
            cameraPreview.surfaceTextureListener = textureListener
        }
    }

    override fun onPause() {
        stopBackgroundThread()
        super.onPause()
    }


}