package cs.ann;

/**
 * Created by KimChi on 12/22/2015.
 */

import android.renderscript.Sampler;
import android.util.Log;


import static java.lang.Math.exp;
import static java.lang.Math.pow;
import java.io.*;
import java.math.BigInteger;
import java.util.*;
import cs.ann.Value;
public class Math {
    public static int rand(int min, int max){
        try{
            Random rand = new Random();
            int range = max - min;
            int num = min + rand.nextInt(range);
            return num;
        }catch(Exception e){
            return -1;
        }
    }

    public static float sigmoid( float f_net){
        return (float)((2 / (1 + exp(-1 * Value.SLOPE * f_net))) - 1);
    }

    public static float sigmoid_derivative(float r){
        return (float)(0.5F * (1 - pow(r, 2)));
    }

    public static int threshold(float val){
        if (val < 0.9)
            return 0;
        else
            return 1;
    }

    //binary to character
    public static char parseStr(int[] value)throws UnsupportedEncodingException{
        int temp = 0;
        for (int i = 15; i >=0; i--){
            temp += value[i] * (int)pow(2, 15-i);
        }
        return (char) (temp);


    }
    //character to binary

    public static int[] parseBinary(char value){
        int s = (int)value;
        int[] arr = new int[16];
        int j = 15;
        String binary = Integer.toBinaryString(s);
        for (int i=binary.length()-1; i>=0; i--){
            arr[j] = Integer.parseInt(String.valueOf(binary.charAt(i)));
            j--;
        }
        return arr;
    }
}
