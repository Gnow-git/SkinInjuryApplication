package com.example.skininjuryapplication.tflite;

import static org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod.NEAREST_NEIGHBOR;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;
import android.util.Size;

import androidx.annotation.Nullable;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.model.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassifierWithModel {
    private static final String MODEL_NAME="skinInjury.tflite"; //텐서플로 라이트 모델
    private static final String LABEL_NAME="labels.txt";    //라벨

    Context context;    //텐서플로 라이트 모델 전달을 위한 앱 컨텍스트
    Model model;    //딥러닝 모델이 직접 수행하는 동작을 한데 모아 객체화한 클래스
    Interpreter interpreter;// 데이터를 입력하고 추론 결과를 받는 클래스
    TensorImage inputImage; //모델에 입력할 이미지 담기, 이미지를 바로 모델에 바로 입력되는 ByteBuffer 포맷으로 변환 가능
    int modelInputWidth, modelInputHeight, modelInputChannel;   //모델의 채널, 높이, 너비 장버
    TensorBuffer outputBuffer;  //텐서를 다루는 버퍼 클래스, 출력 버퍼이다.
    private List<String> labels;    //라벨명을 읽어올 자료 구조

    public ClassifierWithModel(Context context) {
        this.context = context;
    }

    public void init() throws IOException{ //TFLite 모델을 Asset 파일에서 로드
        /******
         * //참고용 주석
         //ByteBuffer로 이미지 읽기
         //텐서플로 라이트 모델이 ByteBuffer 읽혀지기 때문
         ByteBuffer model = FileUtil.loadMappedFile(context, MODEL_NAME); //loadModelFile()도 있지만 이거는 자동으로 Asset을 추적해준다.
         // 이미지 크기를 모델의 입력 크기에 맞춘다
         model.order(ByteOrder.nativeOrder()); // 시스템의 byteOrder 값과 동일하게 설정
         interpreter = new Interpreter(model);
         *///
        //tflite 파일 로드부터 추론까지 모두 수행 가능
        model = Model.createModel(context, MODEL_NAME);
        initModelShape();
        labels = FileUtil.loadLabels(context, LABEL_NAME);  // 라벨 불러오기
    }

    private void initModelShape() { //TensorImage 생성성
        //다차원 배열을 담는 자료구조, numpy와 비슷하다.
        Tensor inputTensor = model.getInputTensor(0); //인터프리터로 텐서 불러오기, model 객체에 다 들어있음
        int [] shape = inputTensor.shape(); // 텐서의 모양 정보 불러오기
        modelInputChannel = shape[0];
        modelInputWidth = shape[1];
        modelInputHeight = shape[2];

        inputImage = new TensorImage(inputTensor.dataType());   //모델과 동일한 데이터 타입으로 전달

        Tensor outputTensor = model.getOutputTensor(0);   //출력 값을 담은 TensorBuffer

        //새로운 텐서 버처 생성
        // createFixedSize()는 텐서의 형태와 데이터 타입을 전달받아 메모리 공간을 계산하여 할당
        outputBuffer = TensorBuffer.createFixedSize(outputTensor.shape(), outputTensor.dataType());

    }

    //TensorImage에 bitmap 이미지 입력 및 이미지 전처리 로직 정의
    private TensorImage loadImage(final Bitmap bitmap){
        inputImage.load(bitmap);    //Tensor 이미지 호출

        ImageProcessor imageProcessor =  //Builder 패턴을 이용해 처리 로직 추가
                new ImageProcessor.Builder()
                        .add(new ResizeOp(modelInputWidth, modelInputHeight, NEAREST_NEIGHBOR))//이미지 변경 연산, 모델 입력 크기의 가로세로와 동일하게 변경
                        .add(new NormalizeOp(0.0f, 255.0f))  //이미지 정규화 0-255 -> 0-1
                        .build();    //호출하여 ImageProcessor 생성

        return imageProcessor.process(inputImage);   //process 함수로 ImageProcessor 적용된 TensorImage 획득
    }

    //Bitmap을 ARGB_8888로 변환
    private Bitmap convertBitmapToARGB8888(Bitmap bitmap) {
        //tensorImage 클래스가 ARGB을 사용하는 Bitmap만을 입력받는다. (물론 다른 것도 있긴 하다.)
        return bitmap.copy(Bitmap.Config.ARGB_8888,true);
    }

    //Bitmap의 Conffig를 확인하여 반환
    private TensorImage loadImage(final Bitmap bitmap, int sensorOrientation) {
        //getConfig()를 호출하여 Bitmap이 ARGB_8888을 사용하는지 확인
        if(bitmap.getConfig() != Bitmap.Config.ARGB_8888) { //아니라면 convertBitmapToARGB8888() 호출
            inputImage.load(convertBitmapToARGB8888(bitmap));
        } else {//맞으면 바로 로드
            inputImage.load(bitmap);
        }

        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        int numRotation = sensorOrientation / 90;

        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                .add(new ResizeOp(modelInputWidth, modelInputHeight, NEAREST_NEIGHBOR))
                .add(new Rot90Op(numRotation))
                .add(new NormalizeOp(0.0f, 255.0f))
                .build();

        return imageProcessor.process(inputImage);
    }

    //이미지 전처리 수행 함수 호출
    public Pair<String, Float> classify(Bitmap image) {

        inputImage = loadImage(image);  //ImageProcessor가 적용된 이미지 획득
        /****
         * 참고용
         interpreter.run(inputImage.getBuffer(), outputBuffer.getBuffer().rewind()); //입력 이미지를 받아 rewind로 되감기하여 출력
         Map<String, Float> output = new TensorLabel(labels, outputBuffer).getMapWithFloatValue();   //Map<<String, Float> 형태로 결과물 반환
         ***/
        Object [] inputs = new Object[]{inputImage.getBuffer()};    //입력 이미지 받기
        Map<Integer, Object> outputs = new HashMap();   //??
        outputs.put(0, outputBuffer.getBuffer().rewind());  //출력 이미지로 반환

        model.run(inputs, outputs);
        //<Key, Value>로 String은 클래스명이고, Value는 Float는 모델의 추론 결과
        Map<String, Float> output =
                new TensorLabel(labels, outputBuffer).getMapWithFloatValue();
        return argmax(output);
    }

    //자원 해제제
    public void finish() {
        if (model != null)
            model.close();
    }

    //Map<String, Float> 자료 구조를 처리, 추론 결과 해석, 자원 해제
    private Pair<String, Float> argmax(Map<String, Float> map) {
        String maxKey= "";  //가장 큰 값들
        float maxVal = -1;

        //클래스의 수 세기?
        for(Map.Entry<String, Float> entry : map.entrySet()) {
            float f = entry.getValue();
            if(f > maxVal) {
                maxKey = entry.getKey();
                maxVal = f;
            }
        }

        return new Pair<>(maxKey, maxVal);
    }

}
