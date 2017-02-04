package com.microsoft.projectoxford.ColumbiaEmotions;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static android.util.Log.i;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmotionDisplayFragment extends Fragment {

    String emo;
    ImageView imgView;
    //String[] emotions = new String[]{"anger", "disgust", "fear", "happiness", "neutral",
    //        "sadness", "surprise", "contempt"};

    public EmotionDisplayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_emotion_display, container, false);
        imgView = (ImageView) rootView.findViewById(R.id.img_emotion);
        Button doneButton = (Button) rootView.findViewById(R.id.done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        return rootView;
    }

    @Subscribe(sticky = true)
    public void onEvent(RecognizeActivity.MessageEvent event) {
        emo = event.emotion;

        // shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(emo, preferences.getInt(emo, 0) + 1);
        editor.commit();


        /// set images and quotes
        if (emo.equals("anger"))
        {
            imgView.setImageResource(R.drawable.anger);
        }
        else if (emo.equals("disgust"))
        {
            imgView.setImageResource(R.drawable.disgusted);
        }
        else if (emo.equals("fear"))
        {
            imgView.setImageResource(R.drawable.fear);
        }
        else if (emo.equals("neutral"))
        {
            imgView.setImageResource(R.drawable.neutral);
        }
        else if (emo.equals("happiness"))
        {
            imgView.setImageResource(R.drawable.happiness);
        }
        else if (emo.equals("sadness"))
        {
            imgView.setImageResource(R.drawable.sadness);
        }
        else if (emo.equals("surprise"))
        {
            imgView.setImageResource(R.drawable.surprise);
        }
        else if (emo.equals("contempt"))
        {
            imgView.setImageResource(R.drawable.contempt);
        }
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
