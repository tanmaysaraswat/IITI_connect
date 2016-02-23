package com.kalyan0510.root.iiticonnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    Uri selectedImageUri;
    String selectedImagePath;
    ImageView iv;
    TextView un , fn , ln ,Ml,St;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        iv = ((ImageView)findViewById(R.id.imgdp));
        un= (TextView)findViewById(R.id.usrnameTv);
        fn = (TextView)findViewById(R.id.fnameTv);
        ln = (TextView)findViewById(R.id.lnameTv);
        Ml =(TextView)findViewById(R.id.mailTv);
        St = (TextView)findViewById(R.id.statusTv);


        findViewById(R.id.editbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 456);
            }
        });
        findViewById(R.id.changepass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),ChangePwdActivity.class);
                startActivity(i);
            }
        });
        getdp(getSharedPreferences(Utilities.SharesPresfKeys.key, Context.MODE_PRIVATE).getInt("reg_id", 0));
        new getusertask().execute(getSharedPreferences(Utilities.SharesPresfKeys.key, Context.MODE_PRIVATE).getInt("reg_id", 0));
    }
    public  void getdp(int reg_id) {

        new getdptask().execute(reg_id);
    }
    public void setprofilepic(int regid, Uri selectedimgURI){
        new setprofilepictask().execute(selectedimgURI);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == 456) {
                selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                if (selectedImagePath == null)
                  Toast.makeText(getApplicationContext(),"path null",Toast.LENGTH_LONG).show();
                if (selectedImagePath == null)
                    return;
                //Toast.makeText(getApplicationContext(), selectedImagePath, Toast.LENGTH_LONG).show();
                iv.setImageURI(selectedImageUri);
                setprofilepic(getSharedPreferences(Utilities.SharesPresfKeys.key, Context.MODE_PRIVATE).
                        getInt(Utilities.SharesPresfKeys.regid, 0), selectedImageUri);
            }
        }
    }
    public String getPath(Uri uri){
        if(uri==null)
            return null;
        return uri.getPath();
    }
    class getdptask extends AsyncTask<Integer,String ,String> {
        String result;
        @Override
        protected String doInBackground(Integer... params) {

            try {
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                SoapObject request = new SoapObject(Utilities.connection.NAMESPACE, Utilities.connection.method_names.getdp);
                request.addProperty("reg_id", params[0]);
                //request.addProperty("password", params[1]);
                envelope.bodyOut = request;
                HttpTransportSE transport = new HttpTransportSE(Utilities.connection.url+Utilities.connection.x+Utilities.connection.exs);
                try {
                    transport.call(Utilities.connection.NAMESPACE + Utilities.connection.SOAP_PREFIX + Utilities.connection.method_names.getdp, envelope);
                } catch (IOException e) {
                    e.printStackTrace();
                    return e.getMessage();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
                result=envelope.getResponse().toString();
                if (envelope.bodyIn != null) {
                    SoapPrimitive resultSOAP = (SoapPrimitive) ((SoapObject) envelope.bodyIn).getProperty(0);
                    result=resultSOAP.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s==null){
                Toast.makeText(getApplicationContext(), "NULL s", Toast.LENGTH_SHORT).show();
                return;
            }
            byte[] array = Base64.decode(s.getBytes(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
            iv.setImageBitmap(bitmap);
            super.onPostExecute(s);
        }
    }
    /////////////////////////////GET USER///////////////////////////////////

     class getusertask extends AsyncTask<Integer, String,String>{
        String result;
        protected String doInBackground(Integer... params) {
            try {
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                SoapObject request = new SoapObject(Utilities.connection.NAMESPACE,"getuser");
                request.addProperty("Reg_id", params[0]);
                envelope.bodyOut = request;
                HttpTransportSE transport = new HttpTransportSE(Utilities.connection.url+Utilities.connection.x+Utilities.connection.exs);
                try {
                    transport.call(Utilities.connection.NAMESPACE + Utilities.connection.SOAP_PREFIX +"getuser", envelope);
                } catch (IOException e) {
                    e.printStackTrace();
                    return e.getMessage();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
                result=envelope.getResponse().toString();
                if (envelope.bodyIn != null) {
                    SoapPrimitive resultSOAP = (SoapPrimitive) ((SoapObject) envelope.bodyIn).getProperty(0);
                    result=resultSOAP.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
            }
            return result;
        }


        @Override
        protected void onPostExecute(String  s) {
            User u = new Gson().fromJson(s,User.class);
            //Toast.makeText(getApplicationContext(), u.getReg_id()+u.getUsername()+u.getMail(), Toast.LENGTH_SHORT).show();
            un.setText(u.getUsername());
            fn.setText(u.getFirst_name());
            ln.setText(u.getLast_name());
            St.setText(u.getStatus());
            Ml.setText(u.getMail());
        }
    }


    class setprofilepictask extends AsyncTask<Uri,String,String> {
         String result;

         @Override
         protected String doInBackground(Uri... params) {

             Bitmap bitmap = null;
             try {
                 bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), params[0]);
                 bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                 ByteArrayOutputStream stream = new ByteArrayOutputStream();
                 bitmap.compress(Bitmap.CompressFormat.PNG, 45, stream);
                 final byte[] array = stream.toByteArray();
                 String Imagestr = Base64.encodeToString(array, Base64.DEFAULT);
                 //converted to byte array and have to send it
                 SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                 SoapObject request = new SoapObject(Utilities.connection.NAMESPACE, Utilities.connection.method_names.changedp);

                 int x = getApplicationContext().getSharedPreferences(Utilities.SharesPresfKeys.key, Context.MODE_PRIVATE).getInt("reg_id", 0);
                 request.addProperty("Reg_id", x);
                 request.addProperty("x", Imagestr);

                 envelope.bodyOut = request;
                 envelope.setOutputSoapObject(request);
                 HttpTransportSE transport = new HttpTransportSE(Utilities.connection.url + Utilities.connection.x + Utilities.connection.exs);
                 try {

                     transport.call(Utilities.connection.NAMESPACE + Utilities.connection.SOAP_PREFIX + Utilities.connection.method_names.changedp, envelope);
                 } catch (IOException e) {
                     e.printStackTrace();
                     return e.getMessage() + "xxxxxx";
                 } catch (XmlPullParserException e) {
                     e.printStackTrace();
                     return e.getMessage() + "yyyyyy";
                 }
                 result = envelope.getResponse().toString();
                 if (envelope.bodyIn != null) {
                     SoapPrimitive resultSOAP = (SoapPrimitive) ((SoapObject) envelope.bodyIn).getProperty(0);
                     result = resultSOAP.toString();
                 }
                 return result;
             } catch (IOException e) {
                 e.printStackTrace();
                 return e.getMessage() + "zzzzzzz";
             } catch (Exception e) {
                 e.printStackTrace();
                 result = e.getMessage() + "wwwwwww";
             }
             return "no result sorry";

         }

         @Override
         protected void onPostExecute(String s) {
             super.onPostExecute(s);
             getdp(getSharedPreferences(Utilities.SharesPresfKeys.key, Context.MODE_PRIVATE).getInt("reg_id",0));
         }
     }
}