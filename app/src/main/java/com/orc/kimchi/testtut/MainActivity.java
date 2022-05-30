package cs.com.imageexpressioncalculation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cs.ann.NeuralNetworks;
import cs.helper.FileDialog;
import img.process.ExtractCharacter;

import static org.opencv.core.Core.flip;
import static org.opencv.core.Core.transpose;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

public class MainActivity extends Activity {

    private Camera mCamera = null;
    private CameraView mCameraView = null;
    FrameLayout camera_view;
    LinearLayout linearLayout;
    View customView;Button capture;

    NeuralNetworks nn = new NeuralNetworks();
    ExtractCharacter ex = new ExtractCharacter();
    Button btnShowCam = null;
    Button btnProcess = null;
    Button btnChoose = null;
    FileDialog fileDialog = null;
    ImageView imageView = null;
    String re = "";
    ExpressionValueCalc  exValue = new ExpressionValueCalc();


    private boolean cameraStatus = true;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("Info: ", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try{
            InputStream inputStream  = getAssets().open("neuralnetwork.ann");
            nn.load(inputStream);
            mCamera = Camera.open();
        }catch (Exception e){

        }

        if(mCamera != null) {
            mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
            camera_view = (FrameLayout)findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }

        capture = (Button)findViewById(R.id.captureBut);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCamera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);
                capture.setVisibility(View.INVISIBLE);
            }
        });

        btnProcess = (Button)findViewById(R.id.buttonProcess);
        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    EditText mEdit = (EditText)findViewById(R.id.predictTextFeild);
                    String temp = mEdit.getText().toString();
                    List<String> Postfix = exValue.InfixToPostfix(temp);

                    double result = exValue.PostfixCalculate(Postfix);

                    TextView res = (TextView) findViewById(R.id.resultTextField);
                    res.setText(String.valueOf(result));

                }catch(Exception e){
                    TextView res = (TextView) findViewById(R.id.resultTextField);
                    res.setText("Đã có lỗi");
                }
            }
        });
        btnChoose = (Button) findViewById(R.id.buttonChooser);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File mPath = new File(Environment.getExternalStorageDirectory() + "//Temp//Test//");
                fileDialog = new FileDialog(MainActivity.this, mPath);
                fileDialog.setFileEndsWith(".txt");
                fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                    public void fileSelected(File file) {
                        Log.d(getClass().getName(), "selected file " + file.toString());
                        Mat myMat = imread(file.toString());

                        Log.d(getClass().getName(), "size " + myMat.size().toString());
                        process(myMat, 1);
                    }
                });
                fileDialog.showDialog();
            }
        });

        btnShowCam=(Button)findViewById(R.id.button);
        btnShowCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(cameraStatus == true){
//                    btnShowCam.setText("Xử lý");
                    linearLayout = (LinearLayout)findViewById(R.id.processView);
                    linearLayout.setVisibility(View.INVISIBLE);
                    camera_view = (FrameLayout)findViewById(R.id.camera_view);
                    camera_view.setVisibility(View.VISIBLE);
                customView = (View) findViewById(R.id.myRectangleView);
                customView.setVisibility(View.VISIBLE);
                capture.setVisibility(View.VISIBLE);
//                    cameraStatus = false;
//                }else{
//
//                    btnShowCam.setText("Chụp ảnh");
//                    cameraStatus = true;
//                }
            }
        });


    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("Warrning", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("Info", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback(){

        @Override
        public void onShutter() {
            // TODO Auto-generated method stub

        }
    };

    Camera.PictureCallback myPictureCallback_RAW = new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub

        }
    };

    Camera.PictureCallback myPictureCallback_JPG = new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] arg0, Camera mCamera) {
            // TODO Auto-generated method stub
            Bitmap bitmapPicture
                    = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
            OutputStream stream = null;
            try {
                stream = new FileOutputStream("/sdcard/test.jpg");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmapPicture.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            mCamera.startPreview();

            Mat myMat = new Mat();
            Utils.bitmapToMat(bitmapPicture, myMat);
            process(myMat, 0);
        }
    };

    private void process (Mat myMat, int type){
        if(type == 0){

            Rect rect = new Rect(800,0,300, myMat.height());
            Mat img = new Mat(myMat, rect);
            myMat = img;

            transpose(myMat, myMat);
            flip(myMat, myMat, 1); //transpose+flip(1)=CW
        }

        imageView = (ImageView)findViewById(R.id.captureView);
        Bitmap.Config conf = Bitmap.Config.RGB_565;
        Bitmap im = Bitmap.createBitmap(myMat.width(),myMat.height(), conf);
        Utils.matToBitmap(myMat, im);
        imageView.setImageBitmap(im);
        Date dt = new Date();
        int hours = dt.getHours();
        int minutes = dt.getMinutes();
        int seconds = dt.getSeconds();
//        imwrite(Environment.getExternalStorageDirectory().getPath() + "/training/" + String.valueOf(hours)+ "-"+String.valueOf(minutes)+"-"+String.valueOf(seconds) +".jpg", myMat);
        imwrite(Environment.getExternalStorageDirectory().getPath() + "/Temp/afterCrop.jpg", myMat);
        Log.i("Size: ", myMat.size().toString());


//       myMat = imread(Environment.getExternalStorageDirectory().getPath() + "/tahoma3.jpg");
        char[] a = {'0','1','2','3','4','5','6','7','8','9','+','-','*','/','(',')'};
        List<int[]> Label = new ArrayList<>();


        List<float[][]> L = new ArrayList<>();


        if(type == 0){
            L = ex.extract(myMat);
        }else if(type == 1){
            L = ex.extract2(myMat);
        }
        try {
            re = nn.predict(L);

            TextView editText = (TextView) findViewById(R.id.predictTextFeild);
            editText.setText(re);


            camera_view = (FrameLayout)findViewById(R.id.camera_view);
            camera_view.setVisibility(View.INVISIBLE);
            linearLayout = (LinearLayout)findViewById(R.id.processView);;
            linearLayout.setVisibility(View.VISIBLE);

            customView = (View) findViewById(R.id.myRectangleView);
            customView.setVisibility(View.INVISIBLE);

        } catch (UnsupportedEncodingException e) {
            Log.i("iii", "loiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
        }
    }

}
