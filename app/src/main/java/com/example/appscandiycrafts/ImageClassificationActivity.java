package com.example.appscandiycrafts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appscandiycrafts.ml.LiteModelMobilenetV2100224Uint81;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ImageClassificationActivity extends AppCompatActivity {
    private ImageView imgView;
    private Button select, capture, predict, toShowProjectButton;
    private TextView tv;
    private Bitmap img;
    List<String> classLabels = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_clasification);

        imgView = findViewById(R.id.imageView);
        select=findViewById(R.id.button);
        capture=findViewById(R.id.button3);
        predict=findViewById(R.id.button2);
        tv=findViewById(R.id.textView);
        toShowProjectButton=findViewById(R.id.toShowProjectButton);

        // Load class labels from the text file
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.labels);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                classLabels.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception if the file cannot be loaded
        }

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);

            }
        });

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,12);
            }
        });

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LiteModelMobilenetV2100224Uint81 model = LiteModelMobilenetV2100224Uint81.newInstance(ImageClassificationActivity.this);

                    if (img == null) {
                        // Handle the case when the image is not selected or captured
                        Toast.makeText(ImageClassificationActivity.this, "Please select or capture an image", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                    img = Bitmap.createScaledBitmap(img, 224, 224, true);
                    inputFeature0.loadBuffer(TensorImage.fromBitmap(img).getBuffer());

                    // Runs model inference and gets result.
                    LiteModelMobilenetV2100224Uint81.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    // Get the predicted index
                    int predictedIndex = getPredictedIndex(outputFeature0.getFloatArray());

                    // Get the corresponding class label
                    String predictedLabel = classLabels.get(predictedIndex);

                    tv.setText(predictedLabel);

                    // Show the "toShowProjectButton" unconditionally
                    toShowProjectButton.setVisibility(View.VISIBLE);

                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ImageClassificationActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*toShowProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start the ShowProjectList activity
                Intent intent = new Intent(ImageClassificationActivity.this, ShowProjectList.class);
                intent.putExtra("predictedLabel", tv.getText().toString());
                startActivity(intent);
            }
        });*/

        toShowProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String predictedLabel = tv.getText().toString();

                // Perform Firestore query
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference projectsRef = db.collection("projects");

                Query query = projectsRef.whereEqualTo("mainItem", predictedLabel);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // At least one document with a matching mainItem exists
                                Intent intent = new Intent(ImageClassificationActivity.this, ShowProjectList.class);
                                intent.putExtra("predictedLabel", predictedLabel);
                                startActivity(intent);
                            } else {
                                // No matching documents found
                                Toast.makeText(ImageClassificationActivity.this, "No project available for current item", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Error retrieving documents
                            Toast.makeText(ImageClassificationActivity.this, "Error retrieving projects", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });




    }

    // Method to find the index of the highest predicted value
    private int getPredictedIndex(float[] predictions) {
        int maxIndex = 0;
        float maxConfidence = predictions[0];
        for (int i = 1; i < predictions.length; i++) {
            if (predictions[i] > maxConfidence) {
                maxIndex = i;
                maxConfidence = predictions[i];
            }
        }
        return maxIndex;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imgView.setImageBitmap(img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == 12 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            img = imageBitmap;
            imgView.setImageBitmap(imageBitmap);
        }

    }
}