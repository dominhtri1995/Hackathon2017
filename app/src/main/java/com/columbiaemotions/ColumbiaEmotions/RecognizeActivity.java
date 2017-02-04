
package com.columbiaemotions.ColumbiaEmotions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;

import com.columbiaemotions.ColumbiaEmotions.helper.ImageHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecognizeActivity extends ActionBarActivity {

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    // The button to select an image
    private Button mButtonSelectImage;

    // The URI of the image selected to detect.
    private Uri mImageUri;

    // The image selected to detect.
    private Bitmap mBitmap;

    // The edit to show status and result.
    private TextView mEditText;

    private EmotionServiceClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);
        getSupportActionBar().setTitle("How are you feeling?");

        mButtonSelectImage = (Button) findViewById(R.id.buttonSelectImage);

        mButtonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(RecognizeActivity.this, com.columbiaemotions.ColumbiaEmotions.helper.SelectImageActivity.class);
                startActivityForResult(intent, REQUEST_SELECT_IMAGE);
            }
        });
        if (checkEmotions() == 0) {
            mButtonSelectImage.setText("Why are you upset?");
            final TextView mContactPhone = (TextView) findViewById(R.id.contact_phone);
            TextView mContact = (TextView) findViewById(R.id.contact);
            ImageView mPhone = (ImageView) findViewById(R.id.phone_icon);
            mContactPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    String tel = mContactPhone.getText().toString().replaceAll("\\-", "");
                    intent.setData(Uri.parse("tel:" + tel));
                    startActivity(intent);
                }
            });

            Typeface gloria = Typeface.createFromAsset(getAssets(), "GloriaHallelujah.ttf");
            mContactPhone.setTypeface(gloria);
            mContact.setTypeface(gloria);
            mContact.setVisibility(View.VISIBLE);
            mContactPhone.setVisibility(View.VISIBLE);
            mPhone.setVisibility(View.VISIBLE);

        } else {
            mButtonSelectImage.setText("Today's good day, isn\'t it?");
        }


        if (client == null) {
            client = new EmotionServiceRestClient(getString(R.string.subscription_key));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void doRecognize() {
        mButtonSelectImage.setEnabled(false);

        // Do emotion detection using auto-detected faces.
        try {
            new doRequest().execute();
        } catch (Exception e) {
            mEditText.append("Error encountered. Exception is: " + e.toString());
        }


    }

    // Called when image selection is done.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("RecognizeActivity", "onActivityResult");
        switch (requestCode) {
            case REQUEST_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();

                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen.

                        // Add detection log.
                        Log.d("RecognizeActivity", "Image: " + mImageUri + " resized to " + mBitmap.getWidth()
                                + "x" + mBitmap.getHeight());

                        doRecognize();
                    }
                }
                break;
            default:
                break;
        }
    }


    private List<RecognizeResult> processWithAutoFaceDetection() throws EmotionServiceException, IOException {
        Log.d("emotion", "Start emotion detection with auto-face detection");

        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        long startTime = System.currentTimeMillis();

        List<RecognizeResult> result = null;
        //
        // Detect emotion by auto-detecting faces in the image.
        //
        result = this.client.recognizeImage(inputStream);

        String json = gson.toJson(result);
        Log.d("result", json);

        Log.d("emotion", String.format("Detection done. Elapsed time: %d ms", (System.currentTimeMillis() - startTime)));

        return result;
    }


    private class doRequest extends AsyncTask<String, String, List<RecognizeResult>> {
        // Store error message
        private Exception e = null;

        public doRequest() {

        }

        @Override
        protected List<RecognizeResult> doInBackground(String... args) {
            try {
                return processWithAutoFaceDetection();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<RecognizeResult> result) {
            super.onPostExecute(result);
            Fragment emotionDisplay = new EmotionDisplayFragment();
            ScrollView scroll = (ScrollView) findViewById(R.id.scroll);
            scroll.setVisibility(View.INVISIBLE);
            String emotion = "";
            for (RecognizeResult r : result) {
                ArrayList<Double> scores = new ArrayList<Double>();
                String[] names = new String[]{"anger", "disgust", "fear", "happiness", "neutral",
                        "sadness", "surprise", "contempt"};
                scores.add(r.scores.anger);
                scores.add(r.scores.disgust);
                scores.add(r.scores.fear);
                scores.add(r.scores.happiness);
                scores.add(r.scores.neutral);
                scores.add(r.scores.sadness);
                scores.add(r.scores.surprise);
                scores.add(r.scores.contempt);
                double max = -1;
                for (double score : scores) {
                    if (score > max)
                        max = score;
                }
                emotion = names[scores.indexOf((max))];
            }

            EventBus.getDefault().postSticky(new MessageEvent(emotion));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.analyze_img, emotionDisplay)
                    .commit();
            getSupportActionBar().hide();

        }
    }


    public int checkEmotions() {
        // check emo
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RecognizeActivity.this);
        int happiness = preferences.getInt("happiness", 0);
        int anger = preferences.getInt("anger", 0);
        int sadness = preferences.getInt("sadness", 0);

        double total = 0;
        String[] emotions = new String[]{"anger", "disgust", "fear", "happiness", "neutral",
                "sadness", "surprise", "contempt"};
        for (int i = 0; i < emotions.length; i++) {
            total += preferences.getInt(emotions[i], 0);
        }

        double CUTOFF = 0.5;
        if (total != 0) {
            if (anger + sadness / total > CUTOFF) {
                return 0;
            } else {
                return 1;
            }
        }

        return 1;

    }

    public class MessageEvent {
        public final String emotion;

        public MessageEvent(String emo) {
            emotion = emo;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
