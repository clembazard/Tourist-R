package com.example.clementremond.touristr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
// For the email validation
import org.apache.http.NameValuePair;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// For the DBB & Json
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.clementremond.touristr.JsonParserClass;
// For the DialogInterface
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    // Useful elements for the code
    EditText login;
    EditText password;
    Button connectionButton;
    Button inscriptionButton;

    // Useful for the DBB
    private ProgressDialog pDialog;
    private final String LOG_MSG = "Log tracing";
    JsonParserClass jsonParse = new JsonParserClass();
    String successTag = null;
    private static final String connectingUrl = "http://10.0.2.2:81/touristr/login.php";
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Looking for the view's elements
        login = (EditText)findViewById(R.id.editTextEmail);
        password = (EditText)findViewById(R.id.editTextPwd);
        connectionButton = (Button)findViewById(R.id.buttonConnexion);
        inscriptionButton = (Button)findViewById(R.id.buttonInscription);
        // Making the button clickable
        connectionButton.setOnClickListener(this);
        inscriptionButton.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 4) {
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        // Getting text values from the view
        boolean emailIsGood = false;
        boolean pwdIsGood = false;

        // OnCLick events
        switch (v.getId()){
            // If the connection button is clicked
            case R.id.buttonConnexion:
                String email = login.getText().toString();
                if (!isValidEmail(email)) {
                    login.setError("Adresse email invalide !");
                } else {
                    emailIsGood = true;
                }

                String pass = password.getText().toString();
                if (!isValidPassword(pass)) {
                    password.setError("Mot de passe invalide !");
                } else {
                    pwdIsGood = true;
                }

                // Try to connect
                if (emailIsGood == true && pwdIsGood == true){
                    ValidateUser validateUser = new ValidateUser();
                    validateUser.execute();
                }

                break;
            // If the inscription button is clicked
            case R.id.buttonInscription:
                Intent i = new Intent(this, InscriptionActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
    // User valdation from the BDD
    public class ValidateUser extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        JSONObject jObject;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Connexion en cours ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String username = login.getText().toString().trim();
            String pwd = password.getText().toString().trim();
            Log.d(LOG_MSG, "user name " + username);
            List<NameValuePair> validateParams = new ArrayList<NameValuePair>();
            validateParams.add(new BasicNameValuePair("username", username));
            validateParams.add(new BasicNameValuePair("password", pwd));
            System.out.println("before calling json object");
            jObject = jsonParse.makeHttpRequest(connectingUrl, "POST",
                    validateParams);
            try {
                successTag = jObject.getString(TAG_SUCCESS);
                Log.d("Jeson result ", successTag);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return successTag;
        }
        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.d("Checking the result a post execute", result);
            if (successTag.equalsIgnoreCase("1")) {
                //Affichage de la réponse
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Connexion")
                        .setMessage("La connexion a réussi")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_menu_info_details)
                        .show();
            } else {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Connexion")
                        .setMessage("La connexion a échoué")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_menu_info_details)
                        .show();
            }
        }
    }
}
