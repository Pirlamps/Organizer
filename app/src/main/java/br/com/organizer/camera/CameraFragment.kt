package br.com.organizer.camera

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.Fragment
import android.view.*
import android.view.TextureView.SurfaceTextureListener
import br.com.organizer.R
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.*

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
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private val file: File? = null
    private var cameraDevice: CameraDevice? = null
    private lateinit var cameraPreview: TextureView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_camera, container, false)

        setupCamera()

        return view
    }

    fun setupCamera() {

        cameraPreview = camera_preview
        val cameraButton = camera_button

        cameraPreview.surfaceTextureListener = textureListener
        cameraButton.setOnClickListener({
            tekePicture()
        })

    }

    private fun openCamera() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createCameraPreview() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun tekePicture() {
        cameraDevice?.let {
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
                if (it.isEmpty()) {
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

            val file = File(Environment.getExternalStorageDirectory().toString() + "/pic.jpg")

            val readerListener = object : ImageReader.OnImageAvailableListener {
                override fun onImageAvailable(p0: ImageReader?) {
                    var image: Image? = null
                    try {
                        image = reader.acquireLatestImage()
                        val buffer = image.planes[0].buffer
                        val bytes = ByteArray(buffer.capacity())
                        save(bytes)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        image?.close()
                    }
                }

                private fun save(bytes: ByteArray) {
                    var output:OutputStream? = null
                    try{
                        output = FileOutputStream(file)
                        output.write(bytes)
                    } finally {
                        output?.close()
                    }
                }
            }

            reader.setOnImageAvailableListener(readerListener,backgroundHandler)

        }



    }




    fun configViews() {

        textureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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


}