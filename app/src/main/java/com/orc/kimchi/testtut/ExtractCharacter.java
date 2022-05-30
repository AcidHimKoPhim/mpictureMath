package img.process;

import android.os.Environment;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.threshold;

/**
 * Created by KimChi on 12/24/2015.
 */
public class ExtractCharacter {

    public  List<float[][]> extract (Mat inputMat)
    {
        Mat mGray = new Mat();
        Mat mSource = new Mat();
        Mat mBinary = new Mat();

        Log.i("inra", "extract character: ");
        List<float[][]> imgList = new ArrayList<float[][]>();
        mSource = inputMat;

        if(mSource.rows()>90){

            Imgproc.resize(mSource, mSource, new Size(mSource.cols() * 90 / mSource.rows(), 90));
        }


        Log.i("in ra", "mSource.size = "+ mSource.size());
        Imgproc.cvtColor(mSource, mGray, Imgproc.COLOR_BGR2GRAY);
//        mGray.convertTo(mGray, -1, 1.5, 0);

        Imgproc.GaussianBlur(mGray, mGray, new Size(3, 3), 3);
        imwrite(Environment.getExternalStorageDirectory().getPath() + "/Temp/grayImg.jpg", mGray);
        Log.i("in ra", "mGray.size = " + mGray.size());
        threshold(mGray, mBinary, 110, 255, Imgproc.THRESH_BINARY);
        imwrite(Environment.getExternalStorageDirectory().getPath()+"/Temp/binaryImg.jpg", mBinary);
        Mat mElement = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3, 3), new Point(1,1));
        Mat mMorph = new Mat();
        Mat mHierachi = new Mat();
        Mat mHierachi2 = new Mat();

        Imgproc.erode(mBinary, mMorph, mElement);

        List<MatOfPoint> contours = new ArrayList<>();
        List<MatOfPoint> contours2 = new ArrayList<>();

        Imgproc.findContours(mBinary, contours, mHierachi, Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0,0));
        Imgproc.findContours(mMorph, contours2, mHierachi2, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE,
                new Point(0, 0));

        Rect r1, r2;
        r1 = new Rect();
        r2 = new Rect();
        List<Rect> result = new ArrayList<>();
        int size;
        if(contours.size() < contours2.size())
        {
            size = contours.size();
        }
        else
        {
            size = contours2.size();
        }

        for(int i = 0; i < size; i++)
        {
            r1 = Imgproc.boundingRect(contours.get(i));
            r2 = Imgproc.boundingRect(contours2.get(i));
            if(r1.area() > r2.area() && r1.x < r2.x) continue;
            if((double)r1.width/ r1.height > 0.1f && (double)r1.width/r1.height < 2.0f)
                result.add(r1);
        }
       List<Rect> result1 = SortRect(result);

        for (int i = 0; i < result1.size(); i++)
        {
            r1 = result1.get(i);
            Mat img = new Mat(mSource, r1);
            imwrite(Environment.getExternalStorageDirectory().getPath() + "/Temp/Extracted/" + i + ".jpg", img);
            Imgproc.resize(img, img, new Size(10, 15));

            threshold(img, img, 90, 1, Imgproc.THRESH_BINARY);
            float[][] ttt = new float[15][10];

            for(int row = 0; row < 15; row++) {
                for (int col = 0; col < 10; col++)
                {
                    ttt[row][col]= (float)img.get(row, col)[0];
                    if(ttt[row][col] == 0) ttt[row][col] = 1;
                    else ttt[row][col] = 0;
                }
            }

            imgList.add(ttt);

        }
        return  imgList;
    }


    public  List<float[][]> extract2 (Mat inputMat)
    {
        Mat mGray = new Mat();
        Mat mSource = new Mat();
        Mat mBinary = new Mat();

        Log.i("inra", "extract character: ");
        List<float[][]> imgList = new ArrayList<float[][]>();
        mSource = inputMat;

        if(mSource.rows()>90){

            Imgproc.resize(mSource, mSource, new Size(mSource.cols() * 90 / mSource.rows(), 90));
        }


        Log.i("in ra", "mSource.size = "+ mSource.size());
        Imgproc.cvtColor(mSource, mGray, Imgproc.COLOR_BGR2GRAY);
//        mGray.convertTo(mGray, -1, 1.5, 0);

        Imgproc.GaussianBlur(mGray, mGray, new Size(3, 3), 3);
        imwrite(Environment.getExternalStorageDirectory().getPath() + "/Temp/grayImg.jpg", mGray);
        Log.i("in ra", "mGray.size = " + mGray.size());
        threshold(mGray, mBinary, 130, 255, Imgproc.THRESH_BINARY);
        imwrite(Environment.getExternalStorageDirectory().getPath()+"/Temp/binaryImg.jpg", mBinary);
        Mat mElement = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3, 3), new Point(1,1));
        Mat mMorph = new Mat();
        Mat mHierachi = new Mat();
        Mat mHierachi2 = new Mat();

        Imgproc.erode(mBinary, mMorph, mElement);

        List<MatOfPoint> contours = new ArrayList<>();
        List<MatOfPoint> contours2 = new ArrayList<>();

        Imgproc.findContours(mBinary, contours, mHierachi, Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0,0));
        Imgproc.findContours(mMorph, contours2, mHierachi2, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE,
                new Point(0, 0));

        Rect r1, r2;
        r1 = new Rect();
        r2 = new Rect();
        List<Rect> result = new ArrayList<>();
        int size;
        if(contours.size() < contours2.size())
        {
            size = contours.size();
        }
        else
        {
            size = contours2.size();
        }

        for(int i = 0; i < size; i++)
        {
            r1 = Imgproc.boundingRect(contours.get(i));
            r2 = Imgproc.boundingRect(contours2.get(i));
            if(r1.area() > r2.area() && r1.x < r2.x) continue;
            if((double)r1.width/ r1.height > 0.1f && (double)r1.width/r1.height < 5.0f)
                result.add(r1);
        }
       List<Rect> result1 = SortRect(result);

        for (int i = 0; i < result1.size(); i++)
        {
            r1 = result1.get(i);
            Mat img = new Mat(mSource, r1);
            imwrite(Environment.getExternalStorageDirectory().getPath() + "/Temp/Extracted/" + i + ".jpg", img);
            Imgproc.resize(img, img, new Size(10, 15));

            threshold(img, img, 90, 1, Imgproc.THRESH_BINARY);
            float[][] ttt = new float[15][10];

            for(int row = 0; row < 15; row++) {
                for (int col = 0; col < 10; col++)
                {
                    ttt[row][col]= (float)img.get(row, col)[0];
                    if(ttt[row][col] == 0) ttt[row][col] = 1;
                    else ttt[row][col] = 0;
                }
            }

            imgList.add(ttt);

        }
        return  imgList;
    }


    public  Boolean GreaterThan(Rect r1, Rect r2) {
        int y = (r1.y + r1.height / 2);

        if (y >= r2.y && y <= (r2.y + r2.height)) {
            if (r1.x < r2.x) {
                return true;
            }
            return false;
        }
        if (y < r2.y) return true;
        return false;

    }

    public List<Rect> SortRect(List<Rect> L)
    {
        Log.i("in ra", "L.size() = "+ L.size());
        for(int i = 0 ; i < L.size(); i++)
        {
            for(int j = i; j < L.size(); j++)
            {
                if(GreaterThan(L.get(i), L.get(j)))
                {
                    Rect temp = L.get(i);
                    L.set(i, L.get(j));
                    L.set(j, temp);
                }
            }
        }
        L = reverseList(L);
        return L;
    }

    public List<Rect> reverseList(List<Rect> L)
    {
        List invertedList = new ArrayList();
        for (int i = L.size() - 1; i >= 0; i--) {
            invertedList.add(L.get(i));
        }
        return invertedList;
    }
}
