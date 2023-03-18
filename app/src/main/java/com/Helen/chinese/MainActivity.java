package com.Helen.chinese;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String FILE_NAME = "chinese.txt";
    private Button btnAddWords;
    private TextView txtFirst, txtSecond, txtThird, txtFourth, txtFifth;
    private TextView txtDate;
    private RadioGroup rgChoice;
    InputMethodManager imm;

    //OnCreate 方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        btnAddWords = findViewById(R.id.btnAddWords);
        txtDate = findViewById(R.id.txtInfo);
        rgChoice = findViewById(R.id.rgChoice);
        txtFirst = findViewById(R.id.txtFirst);
        txtSecond = findViewById(R.id.txtSecond);
        txtThird = findViewById(R.id.txtThird);
        txtFourth = findViewById(R.id.txtFourth);
        txtFifth = findViewById(R.id.txtFifth);


        //将Json文件写入手机存储, IF语句确保关闭app不会restore数据
        boolean fileExist = false;
        for (String file : getApplicationContext().fileList()){
            if(file.equals(FILE_NAME)){
                fileExist = true;
            }
        }
        if (!fileExist){
            CreateDataDuringInstall(this);
        }
//        CreateDataDuringInstall(this);



        //复习学过的生字
        rgChoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                try {
                    JSONArray jsonArray = ReadJsonFile(openFileInput(FILE_NAME));
                    String wordsToShow;
                    switch (i){
                        case R.id.rbDate:
                            txtDate.setText("请选择日期");
                            txtDate.setEnabled(true);
                            ShowWords("?????");
                            break;
                        case R.id.rbRandom:
                            txtDate.setText("复习学过的字");
                            txtDate.setEnabled(false);
                            wordsToShow = GetRandomWords(jsonArray);
                            ShowWords(wordsToShow);
                            break;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }                
            }
        });

        //选中日期，并显示当天的生字
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String dd,mm;
                        month = month +1;
                        String wordsToShow = "";
                        boolean isFound = false;

                        if (month<10){
                            mm = "0"+month;
                        }else {
                            mm = Integer.toString(month);
                        }
                        if (day < 10){
                            dd = "0"+day;
                        }else {
                            dd = Integer.toString(day);
                        }
                        String date = dd + "/" + mm + "/"+year;
                        txtDate.setText(date);
                        try {
                            JSONArray jsonArray = ReadJsonFile(openFileInput(FILE_NAME));
                            for (int i=0; i<jsonArray.length(); i++) {
                                if (date.equals(jsonArray.getJSONObject(i).getString("date"))){
                                    isFound = true;
                                    wordsToShow = jsonArray.getJSONObject(i).getString("zi");
                                }
                            }

                            //如果当天学过生字，则显示。否则提醒没有生字。
                            if (isFound){
                                ShowWords(wordsToShow);
                            }else {
                                ShowWords("?????");
                                Toast.makeText(MainActivity.this, "这天没有生字哟", Toast.LENGTH_SHORT).show();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day);

                //限定日期可选择的范围
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                Calendar beginning = Calendar.getInstance();
                calendar.clear();
                calendar.set(2022,3, 19);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        //新建页面用来添加新学的字
        btnAddWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                bottomSheetDialog.setContentView(R.layout.layout_input_dialog);
                bottomSheetDialog.setCanceledOnTouchOutside(true);

                EditText edtInput = bottomSheetDialog.findViewById(R.id.edtInput);
                Button btnSave = bottomSheetDialog.findViewById(R.id.btnSave);

                //Save button将输入的5个假如Json Array。如果已经存在，则Update。
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edtInput.getText().toString().length() == 5) {
                            //获取当前的日期，并按照格式转换为字符串
                            String todayDate = GetTodayDate();
                            try {
                                JSONArray jArray = ReadJsonFile(openFileInput(FILE_NAME));
                                JSONObject jObject = new JSONObject();
                                for(int i =0; i< jArray.length(); i++){
                                    if (todayDate.equals(jArray.getJSONObject(i).getString("date"))){
                                        jArray.remove(i);
                                    }
                                }

                                jObject.put("date", todayDate);
                                jObject.put("zi", edtInput.getText().toString());
                                jArray.put(jObject);

                                WriteJsonArray(MainActivity.this, jArray);
                                Toast.makeText(v.getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                            } catch (FileNotFoundException | JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(v.getContext(), "生字不是5个哟", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                bottomSheetDialog.show();
            }
        });

        //Debug button 的功能
        Button btnDebug = findViewById(R.id.btnDebug);
        btnDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] fileList = getApplicationContext().fileList();
                for (String s : fileList
                ) {
                    System.out.println(s);

                }


            }
        });


    }

    private String GetTodayDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dd,mm;
        month = month +1;
        if (month<10){
            mm = "0"+month;
        }else {
            mm = Integer.toString(month);
        }
        if (day < 10){
            dd = "0"+day;
        }else {
            dd = Integer.toString(day);
        }
        String date = dd + "/" + mm + "/"+year;
        return date;
    }

    //随机获取5个学过的字字
    private String GetRandomWords(JSONArray jsonArray) {
        StringBuilder sb = new StringBuilder();
        int len = jsonArray.length()*5;
        int[] randomNumbers = {len, len, len, len, len};
        boolean isDuplicated = false;
        
        Random random = new Random();
        //获取5个随机数
        int count = 0;
        while (count < 5) {
            int randomNo = random.nextInt(len);
            for (int r: randomNumbers
            ) {
                if (r ==randomNo){
                    isDuplicated = true;
                    break;
                }
            }
            if(!isDuplicated){
                randomNumbers[count] = randomNo;
                count++;
                try {
                    sb.append(jsonArray.getJSONObject(randomNo / 5).getString("zi").charAt(randomNo % 5));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            isDuplicated = false;
        }
        return sb.toString();
    }


    //将5个字在手机上显示
    public void ShowWords (String chars){
        txtFirst.setText(new Character(chars.charAt(0)).toString());
        txtSecond.setText(new Character(chars.charAt(1)).toString());
        txtThird.setText(new Character(chars.charAt(2)).toString());
        txtFourth.setText(new Character(chars.charAt(3)).toString());
        txtFifth.setText(new Character(chars.charAt(4)).toString());
        imm.hideSoftInputFromWindow(txtDate.getApplicationWindowToken(), 0);
    }

    //将JsonArray写入手机文件，以Json格式存储
    public static void WriteJsonArray(Context context, JSONArray jsonArray){
        //Access to asset resource
        JsonWriter jsonWriter= null;
        OutputStreamWriter outStreamWriter = null;
        try {
            //openFileOutput is Android method to write to phone.
            outStreamWriter = new OutputStreamWriter(context.openFileOutput(FILE_NAME, MODE_PRIVATE), "UTF-8");
            jsonWriter = new JsonWriter(outStreamWriter);

            jsonWriter.beginArray();
            for (int i =0; i<jsonArray.length(); i++){
                JSONObject jo = jsonArray.getJSONObject(i);
                jsonWriter.beginObject();
                jsonWriter.name("date").value(jo.getString("date"));
                jsonWriter.name("zi").value(jo.getString("zi"));
                jsonWriter.endObject();
            }
            jsonWriter.endArray();

            jsonWriter.flush();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally{
            //Always close the stream
            if (jsonWriter !=null){
                try {
                    jsonWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outStreamWriter !=null){
                try {
                    outStreamWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //读取指定位置的Json文件
    public static JSONArray ReadJsonFile(InputStream inputStream){
        JSONArray jsonArray = null;
        try {
            int byteSize = inputStream.available();
            byte[] bytes = new byte[byteSize];
            inputStream.read(bytes);
            inputStream.close();

            String str = new String(bytes);
            jsonArray = new JSONArray(str);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    //Read data from Json file and save it into data > data > app > files. Cannot view it unless phone is rooted.
    public static void CreateDataDuringInstall(Context context){
        try {
            InputStream inputStream = context.getAssets().open("Chars.json");
            JSONArray jsonArray = ReadJsonFile(inputStream);
            WriteJsonArray(context,jsonArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}