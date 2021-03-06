package com.aglframework.smzh.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.aglframework.smzh.AGLView;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("deprecation")
public class AGLCamera1 {

    private Camera camera;
    private int cameraId;
    private AGLView aglView;
    private int previewWidth;
    private int previewHeight;


    public AGLCamera1(AGLView aglView, int width, int height) {
        this.aglView = aglView;
        this.previewWidth = width;
        this.previewHeight = height;
        if (Camera.getNumberOfCameras() > 1) {
            cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }

    public AGLCamera1(AGLView aglView) {
        this.aglView = aglView;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void open() {
        if (camera == null) {
            camera = Camera.open(cameraId);
            Camera.Parameters parameters = camera.getParameters();

            if (previewWidth != 0 && previewHeight != 0) {
                List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
                int width = previewHeight;
                int height = previewWidth;
                if (sizeList.size() > 1) {
                    Iterator<Camera.Size> iterator = sizeList.iterator();
                    while (iterator.hasNext()) {
                        Camera.Size cur = iterator.next();
                        if (cur.width >= width && cur.height >= height) {
                            width = cur.width;
                            height = cur.height;
                            break;
                        }
                    }
                }
                parameters.setPreviewSize(width, height);
            }
            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            camera.setParameters(parameters);
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {

                }
            });
        }
        aglView.setRendererSource(new CuteRendererSourceCamera1(this, new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                aglView.requestRender();
            }
        }));
    }

    public void close() {
        camera.stopPreview();
        camera.release();
        camera = null;
        aglView.clear();

    }

    public void switchCamera() {
        cameraId = (cameraId + 1) % 2;
        close();
        open();
    }


    public void startPreview(SurfaceTexture surfaceTexture) {
        try {
            camera.setPreviewTexture(surfaceTexture);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Camera.Parameters getParameter() {
        return camera.getParameters();
    }


    public int getCameraId() {
        return cameraId;
    }
}
