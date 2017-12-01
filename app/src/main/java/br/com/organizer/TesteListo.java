package br.com.organizer;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.Surface;
import android.view.TextureView;

import java.io.File;
import java.util.ArrayList;

public class TesteListo extends Activity{

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        new ArrayList<Surface>(2);
        File teste = new File(Environment.getExternalStorageDirectory() + "/pic.jpg");

    }
}
