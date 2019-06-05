package com.example.manuelj.myblob2;

import java.io.IOException;
import java.util.List;
import java.util.Random;
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
    private static final String  TAG              = "CONNECTTHREAD";

    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private ColorBlobDetector    mDetector;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;

    int sw = 1280;
    int sh = 720;
    int pv = sw/2;

    int rng = 100;
    float pw = 125f;
    int rng2 = 50;
    float pw2 = 50f;
    int rng3 = 30;
    float pw3 = 25f;
    float ntr = 127.5f;
    float spd = 100f;
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
    private Random rnd;

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
        mOpenCvCameraView.setMaxFrameSize(sw,sh);


        //bluetooth

        bTAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bTAdapter.getBondedDevices();

        for (BluetoothDevice x:
                pairedDevices) {
            String addr = x.getAddress();
            if(addr.equals("8C:DE:52:43:55:1D"))
                mydevice = x;


        }

        rnd = new Random();


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
        mBlobColorHsv.val[0] = 39;
        mBlobColorHsv.val[1] = 255;
        mBlobColorHsv.val[2] = 255;
        mBlobColorHsv.val[3] = 0.0;
        mDetector.setHsvColor(mBlobColorHsv);
        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE, 0, 0, Imgproc.INTER_LINEAR_EXACT);
        mIsColorSelected = true;

    }

    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();
        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;
        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;
        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;
        Rect touchedRect = new Rect();
        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;
        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;
        Mat touchedRegionRgba = mRgba.submat(touchedRect);
        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);
        Scalar tcl = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < tcl.val.length; i++)
            tcl.val[i] /= pointCount;
        Log.i(TAG, "Touched hsv color: (" + tcl.val[0] + ", " + tcl.val[1] +
                ", " + tcl.val[2] + ", " + tcl.val[3] + ")");
        return false;
    }

    public int getdist(double bsize){
        return (int)(7300/bsize);
    }

    public int getAngl(double xx, double dist){
        xx = xx-pv;
        double ff = 730;
        double cc = 10;
        double bb = dist;
        double ang = Math.asin( xx / Math.sqrt(xx*xx + ff*ff) );
        ang = -ang;
        double alpha = (Math.PI/2)-ang;
        double aa = Math.sqrt( bb*bb + cc*cc - 2*bb*cc*Math.cos(alpha) );
        double beta= bb*alpha/aa;
        ang=beta-Math.PI/2;

        ang = Math.toDegrees(ang);
        return (int)ang;
    }

    public void move(float pt, float yw, int dur){
        iii = dur;
        ct.move(pt, yw);

    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        if (mIsColorSelected) {
            mDetector.process(mRgba);
            List<MatOfPoint> contours = mDetector.getContours();
            Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);


            double[] res = findBall(contours);
            double bsize = res[0];
            double xx = res[1];
            double yy = res[2];

            int dist = getdist(bsize);
            int angl = getAngl(xx,dist);
            int absangl = Math.abs(angl);

            if(bsize > 20){
                if(iii == 0) {
                    if (dist < 40) {
                        move(rspeed, ntr, 2);
                    }
                    else if(dist <80){
                        move(ntr,ntr,0);
                    }
                    else if(absangl > 2 ){
                        move(spd, ntr + pw * (angl/20.0f),absangl+10 );

                    }
                    else if(dist > 80){
                        move(spd,ntr,3);
                    }
                }
            }
            else
                iii = 2;


            if(iii == 5)
                move(spd,ntr,5);
            if(iii > 0)
                iii -= 1;
            if(iii == 0 && rnd.nextInt(30)==2 )
                move(ntr,ntr,0);




//            if(bsize > 0) {
//
//                if (true) {
//
//                    if(bsize > 120){
//                        ct.move(rspeed, ntr);
//                        iii = 0;
//                    }
//                    else if(bsize > 80 && bsize < 130){
//                        ct.move(ntr, ntr);
//                        iii = 0;
//                    }
//                    else if(xx < pv - rng) {
//                        ct.move(spd, ntr - pw);
//                        iii = 0;
//                    }
//                    else if (xx > pv + rng) {
//                        ct.move(spd, ntr + pw);
//                        iii = 0;
//                    }
//                    else if(xx < pv - rng2) {
//                        ct.move(spd, ntr - pw2);
//                        iii = 0;
//                    }
//                    else if (xx > pv + rng2) {
//                        ct.move(spd, ntr + pw2);
//                        iii = 0;
//                    }
//                    else if(xx < pv - rng3) {
//                        ct.move(spd, ntr - pw3);
//                        iii = 0;
//                    }
//                    else if (xx > pv + rng3) {
//                        ct.move(spd, ntr + pw3);
//                        iii = 0;
//                    }
//                    else if(bsize < 50 && bsize > 7){
//                        ct.move(spd, ntr);
//                        iii = 0;
//                    }
//                    else if(bsize < 70 && bsize > 7){
//                        ct.move(spd2, ntr);
//                        iii = 0;
//                    }
//                    else
//                        iii += 1;
//                }
//
//            }
//            else
//                iii += 1;
//
//            if(iii > 5)
//            {
//                iii = 0;
//                ct.move(ntr,ntr);
////                if((int)Math.random()*10 == 3)
////                    ct.move(90,0);
////                else if((int)Math.random()*10 == 3)
////                    ct.move(90,200);
//            }

            if(bsize > 0)
                Imgproc.putText(mRgba, String.valueOf(bsize)+"("+String.valueOf(xx)+","+String.valueOf(yy)+") D:"+String.valueOf(dist) + " A:"+String.valueOf(angl), new Point(10, 50), 3, 1, new Scalar(255, 0, 0, 255), 2);
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


}
