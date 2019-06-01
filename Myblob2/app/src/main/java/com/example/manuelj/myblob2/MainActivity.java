package com.example.manuelj.myblob2;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;

public class MainActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";

    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private ColorBlobDetector    mDetector;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;

    int pv = 400;
    int rng = 100;
    float pw = 125f;
    int rng2 = 50;
    float pw2 = 50f;
    int rng3 = 30;
    float pw3 = 25f;
    float ntr = 127.5f;
    float spd = 95f;
    float spd2 = 95f;
    float rspeed = 165f;

    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    private BluetoothAdapter bTAdapter;
    private BluetoothDevice mydevice;
    private ConnectThread ct;
    private int iii=0;

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setMaxFrameSize(1280,720);


        //bluetooth

        bTAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bTAdapter.getBondedDevices();

        for (BluetoothDevice x:
                pairedDevices) {
            String addr = x.getAddress();
            if(addr.equals("8C:DE:52:43:55:1D"))
                mydevice = x;


        }


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        ct = new ConnectThread(this);
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        try {
            ct.connect(mydevice, MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //end bluetooth

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
        setCol();
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public void setCol(){
        //(220.0, 203.0, 16.0, 255.0)
        //(38.609375, 236.109375, 220.453125, 0.0)


        mBlobColorHsv.val[0] = 40;
        mBlobColorHsv.val[1] = 255;
        mBlobColorHsv.val[2] = 255;
        mBlobColorHsv.val[3] = 0.0;
        mDetector.setHsvColor(mBlobColorHsv);
        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE, 0, 0, Imgproc.INTER_LINEAR_EXACT);
        mIsColorSelected = true;

    }

    public boolean onTouch(View v, MotionEvent event) {

        if(true)
            return false;

        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        Log.i(TAG, "Touched hsv color: (" + mBlobColorHsv.val[0] + ", " + mBlobColorHsv.val[1] +
                ", " + mBlobColorHsv.val[2] + ", " + mBlobColorHsv.val[3] + ")");

        mDetector.setHsvColor(mBlobColorHsv);

        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE, 0, 0, Imgproc.INTER_LINEAR_EXACT);

        mIsColorSelected = true;

        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false; // don't need subsequent touch events
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        //mRgba = skinDetection(mRgba);
        if (mIsColorSelected) {
            mDetector.process(mRgba);
            List<MatOfPoint> contours = mDetector.getContours();
            //Log.e(TAG, "Contours count: " + contours.size());
            Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

//            Mat colorLabel = mRgba.submat(4, 68, 4, 68);
//            colorLabel.setTo(mBlobColorRgba);
//
//            Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
//            mSpectrum.copyTo(spectrumLabel);

            double[] res = findBall(contours);
            double bsize = res[0];
            double xx = res[1];
            double yy = res[2];



            if(bsize > 0) {

                if (true) {

                    if(bsize > 120){
                        ct.move(rspeed, ntr);
                        iii = 0;
                    }
                    else if(bsize > 80 && bsize < 130){
                        ct.move(ntr, ntr);
                        iii = 0;
                    }
                    else if(xx < pv - rng) {
                        ct.move(spd, ntr - pw);
                        iii = 0;
                    }
                    else if (xx > pv + rng) {
                        ct.move(spd, ntr + pw);
                        iii = 0;
                    }
                    else if(xx < pv - rng2) {
                        ct.move(spd, ntr - pw2);
                        iii = 0;
                    }
                    else if (xx > pv + rng2) {
                        ct.move(spd, ntr + pw2);
                        iii = 0;
                    }
                    else if(xx < pv - rng3) {
                        ct.move(spd, ntr - pw3);
                        iii = 0;
                    }
                    else if (xx > pv + rng3) {
                        ct.move(spd, ntr + pw3);
                        iii = 0;
                    }
                    else if(bsize < 50 && bsize > 7){
                        ct.move(spd, ntr);
                        iii = 0;
                    }
                    else if(bsize < 70 && bsize > 7){
                        ct.move(spd2, ntr);
                        iii = 0;
                    }
                    else
                        iii += 1;
                }

            }
            else
                iii += 1;

            if(iii > 5)
            {
                iii = 0;
                ct.move(ntr,ntr);
//                if((int)Math.random()*10 == 3)
//                    ct.move(90,0);
//                else if((int)Math.random()*10 == 3)
//                    ct.move(90,200);
            }

            if(bsize > 0)
                Imgproc.putText(mRgba, String.valueOf(bsize)+"("+String.valueOf(xx)+","+String.valueOf(yy)+")", new Point(10, 50), 3, 1, new Scalar(255, 0, 0, 255), 2);
        }

        return mRgba;
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

    public double[] findBall(List<MatOfPoint> contours){
        double xmin,xmax,ymin,ymax;
        double maxd = 0;
        double[] reslt = {0,0,0};
        for (MatOfPoint pts:
             contours) {
            List<Point> ptslist = pts.toList();
            double[] res = findMM(ptslist);
            xmin = res[0];
            xmax = res[1];
            ymin = res[2];
            ymax = res[3];

            double xd = xmax - xmin;
            double yd = ymax - ymin;
            if( Math.abs(xd-yd)/(xd+yd) < 0.2 ) {
                double dst = Math.max(xd, yd);
                if(dst > maxd){
                    maxd = dst;
                    reslt[0] = maxd;
                    reslt[1] = (xmax+xmin)/2;
                    reslt[2] = (ymax+ymin)/2;
                    }
            }
        }

        return reslt;
    }

    public double[] findMM(List<Point> ptslist){
        double xmin,xmax,ymin,ymax;
        xmin = 10000; ymin=10000;
        xmax = 0; ymax = 0;

        for (Point x:
             ptslist) {
            if(x.x < xmin)
                xmin = x.x;
            if(x.x > xmax)
                xmax = x.x;
            if(x.y < ymin)
                ymin = x.y;
            if(x.y > ymax)
                ymax = x.y;
        }
        return new double[]{xmin, xmax, ymin, ymax};
    }

    public Mat skinDetection(Mat src) {


        //49.41° 82.26% 48.63%
        //58.37° 63.36% 90.98%
        //118 G: 101 B: 21
        //222 G: 219 B: 75
        Scalar lower = new Scalar(15, 100, 50);
        Scalar upper = new Scalar(55, 255, 255);

        Mat hsvFrame = new Mat(src.rows(), src.cols(), CvType.CV_8U, new Scalar(3));
        Imgproc.cvtColor(src, hsvFrame, Imgproc.COLOR_RGB2HSV, 3);

        Mat skinMask = new Mat(hsvFrame.rows(), hsvFrame.cols(), CvType.CV_8U, new Scalar(3));
        Core.inRange(hsvFrame, lower, upper, skinMask);

        final Size kernelSize = new Size(11, 11);
        final Point anchor = new Point(-1, -1);
        final int iterations = 2;

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, kernelSize);
        Imgproc.erode(skinMask, skinMask, kernel, anchor, iterations);
        Imgproc.dilate(skinMask, skinMask, kernel, anchor, iterations);

        final Size ksize = new Size(3, 3);

        Mat skin = new Mat(skinMask.rows(), skinMask.cols(), CvType.CV_8U, new Scalar(3));
        Imgproc.GaussianBlur(skinMask, skinMask, ksize, 0);
        Core.bitwise_and(src, src, skin, skinMask);

        return skin;
    }
}
