package sysmobile.usthb.realityaugmentedusthb

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.google.ar.core.ArCoreApk


class MainActivity : AppCompatActivity() {
    lateinit var buttonAr : Button;
    lateinit var mCodeScanner : CodeScanner
    lateinit var scannerView: CodeScannerView
    var permissions = arrayOf(
        Manifest.permission.CAMERA
    )
    var PERM_CODE = 11



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scannerView = findViewById(R.id.scanner_view)
        buttonAr = findViewById(R.id.button2)

        checkPermissions();
        mCodeScanner = CodeScanner(this, scannerView)
        mCodeScanner.decodeCallback = DecodeCallback { result ->
            runOnUiThread {
                Toast.makeText(this@MainActivity, result.text, Toast.LENGTH_SHORT).show()
                val url =  result.getText()

                if (URLUtil.isValidUrl(url)) {
                    onClick(url)
                }
            }
        }
        scannerView.setOnClickListener { mCodeScanner.startPreview() }

        buttonAr.setOnClickListener(View.OnClickListener {
            val availability = ArCoreApk.getInstance().checkAvailability(this)
            if (availability.isSupported()) {
                Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "no", Toast.LENGTH_SHORT).show()
            }
        })
    }

        fun onClick(url: String) {
            val sceneViewerIntent = Intent(Intent.ACTION_VIEW)
            val intentUri: Uri =
                Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                    .appendQueryParameter(
                        "file",
                        //"https://raw.githubusercontent.com/YasmineeBa/Traitement-et-Analyse-d-image/main/usthb_infofac.gltf"
                         url
                    )
                    .appendQueryParameter("mode", "ar_only")
                    .appendQueryParameter("title", "Model 3D")
                    .build()
            sceneViewerIntent.data= intentUri
            sceneViewerIntent.setPackage("com.google.ar.core")
            startActivity(sceneViewerIntent)
        }
    override fun onResume() {
        super.onResume()
        requastCamera()
    }

    private fun requastCamera() {
        if (checkPermissions()) {
            mCodeScanner.startPreview()
        }
    }

    override fun onPause() {
        mCodeScanner.releaseResources()
        super.onPause()
    }


@Override
    private fun checkPermissions(): Boolean {
        val listofpermission: MutableList<String> = ArrayList()
        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listofpermission.add(perm)
            }
        }
        if (!listofpermission.isEmpty()) {
            ActivityCompat.requestPermissions(this, listofpermission.toTypedArray(), PERM_CODE)
            return false
        }
        return true
    }

}


