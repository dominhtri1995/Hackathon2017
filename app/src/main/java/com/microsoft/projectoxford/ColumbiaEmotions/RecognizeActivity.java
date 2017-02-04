
package com.microsoft.projectoxford.ColumbiaEmotions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import com.microsoft.projectoxford.emotion.contract.FaceRectangle;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;
import com.microsoft.projectoxford.ColumbiaEmotions.helper.ImageHelper;

import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

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
        if (checkEmotions() == 0)
        {
            mButtonSelectImage.setText("Why are you upset?");
        }
        else
        {
            mButtonSelectImage.setText("Today's good day, isn\'t it?");
        }

        if (client == null) {
            client = new EmotionServiceRestClient(getString(R.string.subscription_key));
        }


        mEditText = (TextView) findViewById(R.id.editTextResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_recognize, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void doRecognize() {
        mButtonSelectImage.setEnabled(false);

        // Do emotion detection using auto-detected faces.
        try {
            new doRequest(false).execute();
        } catch (Exception e) {
            mEditText.append("Error encountered. Exception is: " + e.toString());
        }

        String faceSubscriptionKey = getString(R.string.faceSubscription_key);
        if (faceSubscriptionKey.equalsIgnoreCase("Please_add_the_face_subscription_key_here")) {
//            mEditText.append("\n\nThere is no face subscription key in res/values/strings.xml. Skip the sample for detecting emotions using face rectangles\n");
        } else {
            // Do emotion detection using face rectangles provided by Face API.
            try {
                new doRequest(true).execute();
            } catch (Exception e) {
                mEditText.append("Error encountered. Exception is: " + e.toString());
            }
        }
    }

    // Called when the "Select Image" button is clicked.
    public void selectImage(View view) {
        mEditText.setText("");

        Intent intent;
        intent = new Intent(RecognizeActivity.this, com.microsoft.projectoxford.ColumbiaEmotions.helper.SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
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
                        ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
                        imageView.setImageBitmap(mBitmap);

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
        // -----------------------------------------------------------------------
        // KEY SAMPLE CODE STARTS HERE
        // -----------------------------------------------------------------------

        List<RecognizeResult> result = null;
        //
        // Detect emotion by auto-detecting faces in the image.
        //
        result = this.client.recognizeImage(inputStream);

        String json = gson.toJson(result);
        Log.d("result", json);

        Log.d("emotion", String.format("Detection done. Elapsed time: %d ms", (System.currentTimeMillis() - startTime)));
        // -----------------------------------------------------------------------
        // KEY SAMPLE CODE ENDS HERE
        // -----------------------------------------------------------------------
        return result;
    }

    private List<RecognizeResult> processWithFaceRectangles() throws EmotionServiceException, com.microsoft.projectoxford.face.rest.ClientException, IOException {
        Log.d("emotion", "Do emotion detection with known face rectangles");
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        long timeMark = System.currentTimeMillis();
        Log.d("emotion", "Start face detection using Face API");
        FaceRectangle[] faceRectangles = null;
        String faceSubscriptionKey = getString(R.string.faceSubscription_key);
        FaceServiceRestClient faceClient = new FaceServiceRestClient(faceSubscriptionKey);
        Face faces[] = faceClient.detect(inputStream, false, false, null);
        Log.d("emotion", String.format("Face detection is done. Elapsed time: %d ms", (System.currentTimeMillis() - timeMark)));

        if (faces != null) {
            faceRectangles = new FaceRectangle[faces.length];

            for (int i = 0; i < faceRectangles.length; i++) {
                // Face API and Emotion API have different FaceRectangle definition. Do the conversion.
                com.microsoft.projectoxford.face.contract.FaceRectangle rect = faces[i].faceRectangle;
                faceRectangles[i] = new com.microsoft.projectoxford.emotion.contract.FaceRectangle(rect.left, rect.top, rect.width, rect.height);
            }
        }

        List<RecognizeResult> result = null;
        if (faceRectangles != null) {
            inputStream.reset();

            timeMark = System.currentTimeMillis();
            Log.d("emotion", "Start emotion detection using Emotion API");
            // -----------------------------------------------------------------------
            // KEY SAMPLE CODE STARTS HERE
            // -----------------------------------------------------------------------
            result = this.client.recognizeImage(inputStream, faceRectangles);

            String json = gson.toJson(result);
            Log.d("result", json);
            // -----------------------------------------------------------------------
            // KEY SAMPLE CODE ENDS HERE
            // -----------------------------------------------------------------------
            Log.d("emotion", String.format("Emotion detection is done. Elapsed time: %d ms", (System.currentTimeMillis() - timeMark)));
        }
        return result;
    }

    private class doRequest extends AsyncTask<String, String, List<RecognizeResult>> {
        // Store error message
        private Exception e = null;
        private boolean useFaceRectangles = false;

        public doRequest(boolean useFaceRectangles) {
            this.useFaceRectangles = useFaceRectangles;
        }

        @Override
        protected List<RecognizeResult> doInBackground(String... args) {
            if (this.useFaceRectangles == false) {
                try {
                    return processWithAutoFaceDetection();
                } catch (Exception e) {
                    this.e = e;    // Store error
                }
            } else {
                try {
                    return processWithFaceRectangles();
                } catch (Exception e) {
                    this.e = e;    // Store error
                }
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
            // Display based on error existence

//            if (this.useFaceRectangles == false) {
////                mEditText.append("\n\nRecognizing emotions with auto-detected face rectangles...\n");
//            } else {
////                mEditText.append("\n\nRecognizing emotions with existing face rectangles from Face API...\n");
//            }
//            if (e != null) {
//                mEditText.setText("Error: " + e.getMessage());
//                this.e = null;
//            } else {
//                if (result.size() == 0) {
//                    mEditText.append("No emotion detected :(");
//                } else {
//                    Integer count = 0;
//                    // Covert bitmap to a mutable bitmap by copying it
//                    Bitmap bitmapCopy = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
//                    Canvas faceCanvas = new Canvas(bitmapCopy);
//                    faceCanvas.drawBitmap(mBitmap, 0, 0, null);
//                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//                    paint.setStyle(Paint.Style.STROKE);
//                    paint.setStrokeWidth(5);
//                    paint.setColor(Color.RED);
//
//                    mEditText.setVisibility(View.VISIBLE);

//                        mEditText.append("You are "+position);
////                        mEditText.append(String.format("\t anger: %1$.5f\n", r.scores.anger));
////                        mEditText.append(String.format("\t contempt: %1$.5f\n", r.scores.contempt));
////                        mEditText.append(String.format("\t disgust: %1$.5f\n", r.scores.disgust));
////                        mEditText.append(String.format("\t fear: %1$.5f\n", r.scores.fear));
////                        mEditText.append(String.format("\t happiness: %1$.5f\n", r.scores.happiness));
////                        mEditText.append(String.format("\t neutral: %1$.5f\n", r.scores.neutral));
////                        mEditText.append(String.format("\t sadness: %1$.5f\n", r.scores.sadness));
////                        mEditText.append(String.format("\t surprise: %1$.5f\n", r.scores.surprise));
////                        mEditText.append(String.format("\t face rectangle: %d, %d, %d, %d", r.faceRectangle.left, r.faceRectangle.top, r.faceRectangle.width, r.faceRectangle.height));
//                        faceCanvas.drawRect(r.faceRectangle.left,
//                                r.faceRectangle.top,
//                                r.faceRectangle.left + r.faceRectangle.width,
//                                r.faceRectangle.top + r.faceRectangle.height,
//                                paint);
//                        count++;
//                    }
//                    ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
//                    imageView.setVisibility(View.VISIBLE);
//                    imageView.setImageDrawable(new BitmapDrawable(getResources(), mBitmap));
//                }
//               // mEditText.setSelection(0);
//            }
//
//            mButtonSelectImage.setEnabled(true);
        }
    }


    public int checkEmotions()
    {
        // check emo
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RecognizeActivity.this);
        int happiness = preferences.getInt("happiness", 0);
        int anger = preferences.getInt("anger", 0);
        int sadness = preferences.getInt("sadness", 0);

        double total = 0;
        String[] emotions = new String[]{"anger", "disgust", "fear", "happiness", "neutral",
                "sadness", "surprise", "contempt"};
        for (int i = 0; i < emotions.length; i++)
        {
            total += preferences.getInt(emotions[i], 0);
        }

        double CUTOFF = 0.5;
        if (total != 0)
        {
            if (anger + sadness/ total > CUTOFF)
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }

        return 1;

    }
    public class MessageEvent {
        public final String emotion;

        public MessageEvent(String emo)
        {
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