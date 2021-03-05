package com.appmovil.tareagooglemaps;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.appmovil.tareagooglemaps.WebService.Asynchtask;
import com.appmovil.tareagooglemaps.WebService.WebService;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements  Asynchtask{

    public Vision ApiVision;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView imagentexto;
    String imageDetail;
    Intent intent;
    Bundle b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        intent = new Intent(MainActivity.this, MapaActivity.class);
        b = new Bundle();
        Vision.Builder visionBuilder = new Vision.Builder(new NetHttpTransport(),
                new AndroidJsonFactory(), null);
        visionBuilder.setVisionRequestInitializer(new VisionRequestInitializer("AIzaSyB5MkIB5lNnQH1kC1tZ3ATeEsv7z66moKs"));
        ApiVision = visionBuilder.build();
        imagentexto = (ImageView) findViewById(R.id.imagentexto);
        Button btnleer = (Button)findViewById(R.id.btnprocesar);
        Button btnselecc = (Button)findViewById(R.id.seleccionarimg);
        btnselecc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        btnleer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProcesarTexto();
            }
        });
    }
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            imagentexto.setImageURI(imageUri);
        }
    }
    public Image getImageToProcess(){
        ImageView imagen = (ImageView)findViewById(R.id.imagentexto);
        BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        bitmap = scaleBitmapDown(bitmap, 1200);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] imageInByte = stream.toByteArray();
        Image inputImage = new Image();
        inputImage.encodeContent(imageInByte);
        return inputImage;
    }

    public BatchAnnotateImagesRequest setBatchRequest(String TipoSolic, Image inputImage){
        Feature desiredFeature = new Feature();
        desiredFeature.setType(TipoSolic);
        AnnotateImageRequest request = new AnnotateImageRequest();
        request.setImage(inputImage);
        request.setFeatures(Arrays.asList(desiredFeature));
        BatchAnnotateImagesRequest batchRequest =  new BatchAnnotateImagesRequest();
        batchRequest.setRequests(Arrays.asList(request));
        return batchRequest;
    }

    public void ProcesarTexto(){
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    BatchAnnotateImagesRequest batchRequest = setBatchRequest("TEXT_DETECTION", getImageToProcess());
                    try {
                        Vision.Images.Annotate annotateRequest = ApiVision.images().annotate(batchRequest);
                        annotateRequest.setDisableGZipContent(true);
                        BatchAnnotateImagesResponse response = annotateRequest.execute();

                        final TextAnnotation text = response.getResponses().get(0).getFullTextAnnotation();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageDetail = text.getText();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("http://www.geognos.com/api/en/countries/info/all.json",
                datos, this, this);
        ws.execute("GET");
        }
        private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;
        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    @Override
    public void processFinish(String result) throws JSONException {
        try {
            String atributo = "";
            JSONObject JSONpais = new JSONObject(result);
            JSONObject results = JSONpais.getJSONObject("Results");
            JSONArray nombrespaises=results.names();
            for (int i = 0; i < nombrespaises.length(); i++) {
                String paises= nombrespaises.getString(i);
                JSONObject JSONPaises=results.getJSONObject(paises);
                String nombrePais=JSONPaises.getString("Name").toLowerCase();
                JSONObject countryCodes=JSONPaises.getJSONObject("CountryCodes");
                String iso2=countryCodes.getString("iso2");
                String pais = imageDetail.toLowerCase().replace("\n","");
                if (nombrePais.equals(pais)) {
                    b.putString("iso", iso2);
                    intent.putExtras(b);
                    startActivity(intent);
                    break;
                }
            }
        } catch (Exception e) {

        }
    }
}
