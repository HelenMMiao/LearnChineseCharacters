//btnGenerate.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View view) {
//
//        FileInputStream fis = null;
//
//        //读取Json文件并解析
//        try {
//        fis = openFileInput(FILE_NAME);
//        InputStreamReader isr = new InputStreamReader(fis);
//        BufferedReader br = new BufferedReader(isr);
//        StringBuilder sb = new StringBuilder();
//        String text, chars = null;
//
//        while ((text = br.readLine()) != null){
//        sb.append(text).append("\n");
//        }
//
//        JSONObject jsonObject = new JSONObject(sb.toString());
//        JSONArray jsonArray = jsonObject.getJSONArray("allChars");
//
//        //解析Json文件为Array
//        for (int i=0; i<jsonArray.length(); i++){
//        JSONObject object = jsonArray.getJSONObject(i);
//
//        switch (rgChoice.getCheckedRadioButtonId()){
//        case R.id.rbDate:
//        String inputDate = edtDate.getText().toString();
//        if (inputDate.equals(object.getString("date"))){
//        chars = object.getString("zi");
//        }
//        break;
//default:
//
//        break;
//        }
//
//        chars = object.getString("zi");
//        edtDate.setText(chars);
//        }
//        //edtDate.setText(sb.toString());
//        } catch (FileNotFoundException e) {
//        e.printStackTrace();
//        } catch (IOException e) {
//        e.printStackTrace();
//        } catch (JSONException e) {
//        e.printStackTrace();
//        } finally {
//        if (fis != null){
//        try {
//        fis.close();
//        } catch (IOException e) {
//        e.printStackTrace();
//        }
//        }
//        }
//
//        }
//        });