package com.example.clementremond.touristr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

// For the email validation
import org.apache.http.NameValuePair;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


public class InscriptionActivity extends ActionBarActivity implements View.OnClickListener {

    // Elements of the view
    EditText mPrenom;
    EditText mNom;
    EditText mMail;
    EditText mPwd1;
    EditText mPwd2;
    Button mButtonValider;
    Button mButtonAnnuler;


    // Useful for the DBB
    private ProgressDialog pDialog;
    private final String LOG_MSG = "Log tracing";
    JsonParserClass jsonParse = new JsonParserClass();
    String successTag = null;
    private static final String connectingUrl = "http://10.0.2.2:81/touristr/create_user.php";
    private static final String TAG_SUCCESS = "success";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        // Linking the elements from the view
        mPrenom = (EditText)findViewById(R.id.editTextPrenom);
        mNom = (EditText)findViewById(R.id.editTextNom);
        mMail = (EditText)findViewById(R.id.editTextMail);
        mPwd1 = (EditText)findViewById(R.id.editTextPwd1);
        mPwd2 = (EditText)findViewById(R.id.editTextPwd2);
        mButtonValider = (Button)findViewById(R.id.buttonValider);
        mButtonAnnuler = (Button)findViewById(R.id.buttonAnnuler);
        // Make the buttons clickable
        mButtonAnnuler.setOnClickListener(this);
        mButtonValider.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inscription, menu);
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

        // Useful booleans
        boolean prenomIsGood = false;
        boolean nomIsGood = false;
        boolean mailIsGood = false;
        boolean pwdAreGood = false;
        
        // OnClick events
        switch (v.getId()){
            case R.id.buttonValider:
                /*
                    TEST DES CHAMPS
                 */

                // Vérification du champs prénom
                if (mPrenom.getText().toString().isEmpty()){
                    mPrenom.setError("Votre prénom est nécessaire");
                } else {
                    prenomIsGood = true;
                }
                // Vérification du champs nom
                if (mNom.getText().toString().isEmpty()){
                    mNom.setError("Votre nom est nécessaire");
                } else {
                    nomIsGood = true;
                }
                // Vérification du champs mail
                if (mMail.getText().toString().isEmpty()){
                    mMail.setError("Votre email est nécessaire");
                } else if (!isValidEmail(mMail.getText().toString())){
                    mMail.setError("Votre email est invalide");
                } else {
                    mailIsGood = true;
                }
                // Vérification des champs de mot de passe
                if (mPwd1.getText().toString().isEmpty()){
                    mPwd1.setError("Votre mot de passe est nécessaire");
                } else if(!isValidPassword(mPwd1.getText().toString())){
                    mPwd1.setError("Votre mot de passe est invalide");
                } else {
                    if (mPwd2.getText().toString().isEmpty()){
                        mPwd2.setError("Votre mot de passe est à nouveau nécessaire");
                    } else if(!isValidPassword(mPwd1.getText().toString())){
                        mPwd2.setError("Votre mot de passe est invalide");
                    } else if (!mPwd1.getText().toString().equals(mPwd2.getText().toString())){ // Si les deux mots de passe rentrés ne sont pas similaires
                        mPwd2.setError("Ce mot de passe ne correspond pas au premier");
                    } else {
                        pwdAreGood = true;
                    }
                }

                // Si tout es bon, on attaque le gros oeuvre
                if ((prenomIsGood == true) && (nomIsGood == true) && (mailIsGood == true) && (pwdAreGood == true)){
                    ValidateUser validateUser = new ValidateUser();
                    validateUser.execute();
                }
                break;
            case R.id.buttonAnnuler:
                // Go back to the LoginActivity
                finish();
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
            progressDialog = new ProgressDialog(InscriptionActivity.this);
            progressDialog.setMessage("Inscription en cours ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String prenom = mPrenom.getText().toString().trim();
            String nom = mNom.getText().toString().trim();
            String mail = mMail.getText().toString().trim();
            String password = mPwd1.getText().toString().trim();
            Log.d(LOG_MSG, "mail " + mail);
            List<NameValuePair> validateParams = new ArrayList<NameValuePair>();
            validateParams.add(new BasicNameValuePair("prenom", prenom));
            validateParams.add(new BasicNameValuePair("nom", nom));
            validateParams.add(new BasicNameValuePair("mail", mail));
            validateParams.add(new BasicNameValuePair("password", password));
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
                new AlertDialog.Builder(InscriptionActivity.this)
                        .setTitle("Inscription")
                        .setMessage("L'inscription a réussi")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_menu_info_details)
                        .show();
            } else {
                new AlertDialog.Builder(InscriptionActivity.this)
                        .setTitle("Connexion")
                        .setMessage("L'inscription a échoué")
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
