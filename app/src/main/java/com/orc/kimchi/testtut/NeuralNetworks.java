package cs.ann;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.*;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

/**
 * Created by KimChi on 12/22/2015.
 */
public class NeuralNetworks {

    public float[][][] weight = new float[Value.NUMBER_LAYERS][Value.MAX_LAYER][Value.MAX_LAYER];


    int[] layers = new int[Value.NUMBER_LAYERS];

    Random rand = new Random();

    /// <summary>
    /// Mảng chứ giá trị đầu vào của ký tự đang được nhận dạng
    /// </summary>
    float[] current_input = new float[Value.NUMBER_NODES_INPUT];

    /// <summary>
    /// Mảng chứa các bit đầu ra mong muốn của mạng
    /// </summary>
    /// <summary>
    ///  Giá trị đầu ra sau khi tính toán của mỗi neural
    /// </summary>
    float[][] node_output = new float[Value.NUMBER_LAYERS][Value.MAX_LAYER];

    /// <summary>
    /// Mảng 2 chiều chứa giá trị lỗi ở từng neural
    /// </summary>
    float[][] error = new float[Value.NUMBER_LAYERS][Value.MAX_LAYER];

    /// <summary>
    /// Khởi tạo các trọng số ngẫu nhiên trước khi training.
    /// </summary>
    public void InitWeights() {
        Random rand = new Random();
        for (int i = 1; i < Value.NUMBER_LAYERS; i++) {
            for (int j = 0; j < layers[i]; j++) {
                for (int k = 0; k < layers[i - 1]; k++) {
                    weight[i][j][k] = Math.rand(-Value.WEIGHT_BIAS, Value.WEIGHT_BIAS);
                }
            }
        }
    }

    /// <summary>
    /// Tính toán giá trị output của mỗi neural
    /// </summary>
    public void CalcOutputs() {
        float f_net;
        int num_weights;
        for (int i = 0; i < Value.NUMBER_LAYERS; i++) {
            for (int j = 0; j < layers[i]; j++) {
                f_net = 0.0F;

                // Các biến cờ để xác định.
                if (i == 0) {
                    num_weights = 1;
                } else {
                    num_weights = layers[i - 1];
                }

                // Nếu là layer input thì giá trị tổng chính bằng đầu vào
                // Nếu là layer layer khác thì tính theo công thức
                for (int k = 0; k < num_weights; k++) {
                    if (i == 0) {
                        f_net = current_input[j];
                    } else {
                        f_net = f_net + node_output[i - 1][k] * weight[i][j][k];
                    }
                }
                node_output[i][j] = Math.sigmoid(f_net);
            }
        }
    }

    /// <summary>
    /// Cập nhật lại trọng số trong quá trình training
    /// </summary>
    public void CalcWeights() {
        for (int i = 1; i < Value.NUMBER_LAYERS; i++) {
            for (int j = 0; j < layers[i]; j++) {
                for (int k = 0; k < layers[i - 1]; k++) {
                    weight[i][j][k] = (float) (weight[i][j][k] + Value.LEARNING_RATE * error[i][j] * node_output[i - 1][k]);
                }
            }
        }
    }

    /// <summary>
    /// Tính toán lỗi
    /// </summary>
    public void CalcErrors(int[] dataTrainingOut) {
        float sum = 0.0F;

        // Tính lỗi tại các neural ở lớp output
        for (int i = 0; i < Value.NUMBER_NODES_OUTPUT; i++) {
            error[Value.NUMBER_LAYERS - 1][i] =
                    (float) ((dataTrainingOut[i] - node_output[Value.NUMBER_LAYERS - 1][i])
                            * Math.sigmoid_derivative(node_output[Value.NUMBER_LAYERS - 1][i]));
        }

        // Tính lỗi tại các neural trong lớp ẩn
        for (int i = Value.NUMBER_LAYERS - 2; i >= 0; i--) {
            for (int j = 0; j < layers[i]; j++) {
                sum = 0.0F;
                for (int k = 0; k < layers[i + 1]; k++) {
                    sum = sum + error[i + 1][k] * weight[i + 1][k][j];
                }
                error[i][j] = (float) (Math.sigmoid_derivative(node_output[i][j]) * sum);
            }
        }
    }

    /// <summary>
    /// Tính giá trị lỗi trung bình
    /// </summary>
    /// <returns></returns>
    public float get_average_error() {
        float average_error = 0.0F;
        for (int i = 0; i < Value.NUMBER_NODES_OUTPUT; i++) {
            average_error = average_error + error[Value.NUMBER_LAYERS - 1][i];
        }
        average_error = average_error / Value.NUMBER_NODES_OUTPUT;
        return abs(average_error);
    }

    /// <summary>
    /// Hàm load ma trận trong số weight một mạng neural đã được training trước đó
    /// </summary>
//    /// <param name="path"></param>
    public void load(InputStream  inputStream) {
        String line = "";
//        File f = new File(path);
        char[] weight_char = new char[20];
        String weight_text = "";
        int title_length, weight_length;
        StringBuilder text = new StringBuilder();

        layers[0] = Value.NUMBER_NODES_INPUT;
        layers[Value.NUMBER_LAYERS - 1] = Value.NUMBER_NODES_OUTPUT;
        for (int i = 1; i < Value.NUMBER_LAYERS - 1; i++) {
            layers[i] = Value.MAX_LAYER;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            for (int i = 1; i < Value.NUMBER_LAYERS; i++) {
                for (int j = 0; j < layers[i]; j++) {
                    for (int k = 0; k < layers[i - 1]; k++) {
                        weight_text = "";
                        line = br.readLine();
                        title_length = ("Weight[" + i + " , " + j + " , " + k + "] = ").length(); // lay do dai chuoi "Weight[i , j , k] ="
                        weight_length = line.length() - title_length;  // Lay do dai nguyen chuoi do, VD: "Weight[1 , 0 , 0] = -14.39005"
                        weight_text = line.substring(title_length, line.length());
                        weight[i][j][k] = Float.parseFloat(line.substring(title_length, line.length()));		// chuyen chuoi vua lay o tren ve dang float
                    }
                }
            }
            br.close();
        } catch (Exception  io) {
        }
    }

    /// <summary>
    /// Hàm nhận dạng, chọn từng ma trận ký tự trong dãy, tính toán đầu ra, chuyển về dưới dạng character
    /// </summary>
    public String predict(List<float[][]> datasetBinary) throws UnsupportedEncodingException {
        /// <summary>
        /// Mảng 1 chiều chứa các bit đầu ra của mạng.
        /// </summary>
        int[] dataPredictOutput = new int[Value.NUMBER_NODES_OUTPUT];

        String predict = "";

        // Lặp NumberOfCharacter lần
        for (int k = 0; k < datasetBinary.size(); k++) {
            // Lấy ma trận của ký tự thứ k trong hình ảnh
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 10; j++) {
                    current_input[j * 15 + i] = datasetBinary.get(k)[i][j];
                }
            }

            // Tính đầu ra đối với ký tự đó
            CalcOutputs();

            // Đổi giá trị đầu ra thành các bit 0 1
            for (int i = 0; i < Value.NUMBER_NODES_OUTPUT; i++) {
                dataPredictOutput[i] = Math.threshold(node_output[Value.NUMBER_LAYERS - 1][i]);

            }
            // Lưu vào string ký tự đó
            predict += Math.parseStr(dataPredictOutput) + " ";

//                System.out.print(dataPredictOutput[0]);
        }
        return  predict;
    }

    /// <summary>
    /// Hàm training mạng neural
    /// </summary>
    /// <param name="path">Đường dẫn tới folder chứa tập huấn luyện </param>
    public void training(List<float[][]> datasetBinary, List<int[]> datasetLabel) {

        int[] dataTrainingOut = new int[Value.NUMBER_NODES_OUTPUT];

        // Cài đặt các giá trị cho layer
        layers[0] = Value.NUMBER_NODES_INPUT;
        layers[Value.NUMBER_LAYERS - 1] = Value.NUMBER_NODES_OUTPUT;
        for (int i = 1; i < Value.NUMBER_LAYERS - 1; i++) {
            layers[i] = Value.MAX_LAYER;
        }

        // Khởi tạo trong số ban đầu cho mảng
        InitWeights();

        int set_number;
        float average_error = 0.0F;
        try {
            //Lặp cho tới khi đạt tới số lần dạy. Nếu giá trị lỗi trung bình nhỏ hơn ngưỡng lỗi thì thoát vòng lặp
            for (int epoch = 0; epoch <= Value.EPOCHS; epoch++) {
                average_error = 0.0F;
                for (int k = 0; k < datasetBinary.size(); k++) {
                    // Lấy ký tự bất kỳ trong chuỗi ký tự đó
                    set_number = Math.rand(0, datasetBinary.size());

                    // Lấy ma trận nhị phân của ký tự bất kỳ đó
                    for (int i = 0; i < 15; i++) {
                        for (int j = 0; j < 10; j++) {
                            current_input[j * 15 + i] = datasetBinary.get(set_number)[i][j];
                        }
                    }

                    // Lấy đầu ra mong muốn của ký tự đó
                    for (int i = 0; i < Value.NUMBER_NODES_OUTPUT; i++) {
                        dataTrainingOut[i] = datasetLabel.get(set_number)[i];
                    }

                    // Các công việc tính toán
                    CalcOutputs();
                    CalcErrors(dataTrainingOut);
                    CalcWeights();

                    // tính lại giá trị lỗi trung bình
                    average_error = average_error + get_average_error();
                }
                average_error = average_error / datasetBinary.size();

                // Nếu giá trị lỗi trung bình nhỏ hơn ngưỡng lỗi thì thoát vòng lặp
                if (average_error < Value.ERR_THRESHOLD) {
                    epoch = Value.EPOCHS + 1;
                }
            }
        } catch (Exception e) {
        }
    }

    public void save(String fileName) throws FileNotFoundException, UnsupportedEncodingException{
        PrintWriter writer = new PrintWriter(System.getProperty("user.dir") + "//network//"+fileName + ".ann", "UTF-8");
        for (int i = 1; i < Value.NUMBER_LAYERS; i++)
            for (int j = 0; j < layers[i]; j++)
                for (int k = 0; k < layers[i - 1]; k++)
                {
                    writer.println("Weight[" + i + " , " + j + " , " + k + "] = " + weight[i][j][k]);

                }
        writer.close();
    }

}
