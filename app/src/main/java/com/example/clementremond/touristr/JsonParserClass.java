package com.example.clementremond.touristr;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.UnsupportedEncodingException;
        import java.util.List;
        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.json.JSONException;
        import org.json.JSONObject;
        import android.util.Log;

public class JsonParserClass {
    DefaultHttpClient httpClient;
    HttpPost httpPost;
    HttpResponse httpResponse;
    HttpEntity httpEntity;
    static InputStream inputStream = null;
    static JSONObject jsonObject = null;
    static String jsonString = "";
    public JSONObject makeHttpRequest(String url, String method,
                                      List<NameValuePair> params)
    {
        try
        {
            if(method == "POST")
            {
                //getting default http client to make request
                httpClient = new DefaultHttpClient();
                //creating http post request to the given url
                httpPost = new HttpPost(url);
                //setting inputs of android as html form inputs
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                //execute http post request throuh response object with httpclient object
                httpResponse = httpClient.execute(httpPost);
                //getting entity object from response object
                httpEntity = httpResponse.getEntity();
                //getting contents of httpentity into inputstream
                inputStream = httpEntity.getContent();
            }
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(e.toString());
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(e.toString());
        }
        //GETTING BUFFERREADER TO READ INPUT STREAM
        try {
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = bufferReader.readLine()) != null) {
                sb.append(line +"\n");
            }
            inputStream.close();
            jsonString = sb.toString();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Buffer Error", "Error converting result " + e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //parse the string to json object
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("JSON Parser", "Error parsing data [" + e.getMessage()+"] "+ jsonString);
        }
        return jsonObject;
    }
}