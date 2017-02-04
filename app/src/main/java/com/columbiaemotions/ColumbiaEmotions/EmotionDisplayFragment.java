package com.columbiaemotions.ColumbiaEmotions;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Random;

import static android.util.Log.i;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmotionDisplayFragment extends Fragment {

    String emo;
    ImageView imgView;
    TextView textView;
    TextView authorView;
    static final String[] happiness = new String[]{"The moments of happiness we enjoy take us by surprise. It is not that we seize them, but that they seize us.",
            "Be happy for this moment. This moment is your life.",
            "Today I choose life. Every morning when I wake up I can choose joy, happiness, negativity, pain... To feel the freedom that comes from being able to continue to make mistakes and choices - today I choose to feel life, not to deny my humanity but embrace it.",
            "The best way to pay for a lovely moment is to enjoy it.",
            "Let us never know what old age is. Let us know the happiness time brings, not count the years.",
            "Success is not the key to happiness. Happiness is the key to success. If you love what you are doing, you will be successful.",
            "Happiness radiates like the fragrance from a flower and draws all good things towards you.",
            "Happiness is a choice. You can choose to be happy. There's going to be stress in life, but it's your choice whether you let it affect you or not.",
            "Keep your face always toward the sunshine - and shadows will fall behind you."};
    static final String[] happinessAuthor = new String[]{"Ashley Montagu", "Omar Khayyam", "Kevyn Aucoin", "Richard Bach", "Ausonius", "Albert Schweitzer", "Maharishi Mahesh Yogi", "Valerie Bertinelli", "Walt Whitman"};

    static final String[] sadness = new String[]{"The moments of happiness we enjoy take us by surprise. It is not that we seize them, but that they seize us.",
            "Be happy for this moment. This moment is your life.",
            "Today I choose life. Every morning when I wake up I can choose joy, happiness, negativity, pain... To feel the freedom that comes from being able to continue to make mistakes and choices - today I choose to feel life, not to deny my humanity but embrace it.",
            "The best way to pay for a lovely moment is to enjoy it.",
            "Let us never know what old age is. Let us know the happiness time brings, not count the years.",
            "Success is not the key to happiness. Happiness is the key to success. If you love what you are doing, you will be successful.",
            "Happiness radiates like the fragrance from a flower and draws all good things towards you.",
            "Happiness is a choice. You can choose to be happy. There's going to be stress in life, but it's your choice whether you let it affect you or not.",
            "Keep your face always toward the sunshine - and shadows will fall behind you."};
    static final String[] sadnessAuthor = new String[]{"Pat Riley", "William James", "Jenn Proske", "Lyndon B. Johnson", "Les Brown", "Tena Desae", "Joseph Campbell", "Demi Lovato", "Dita Von Teese"};

    static final String[] anger = new String[]{"My friends, love is better than anger. Hope is better than fear. Optimism is better than despair. So let us be loving, hopeful and optimistic. And we'll change the world.",
            "Every day we have plenty of opportunities to get angry, stressed or offended. But what you're doing when you indulge these negative emotions is giving something outside yourself power over your happiness. You can choose to not let little things upset you.",
            "For every minute you remain angry, you give up sixty seconds of peace of mind.",
            "Anybody can become angry - that is easy, but to be angry with the right person and to the right degree and at the right time and for the right purpose, and in the right way - that is not within everybody's power and is not easy.",
            "It's so important to realize that every time you get upset, it drains your emotional energy. Losing your cool makes you tired. Getting angry a lot messes with your health.",
            "People won't have time for you if you are always angry or complaining.",
            "When you don't manage your life well, you become angry and frustrated as things don't go as intended, and our bad mood is a sign showing we were not able to resolve the conflict.",
            "There are two things a person should never be angry at, what they can help, and what they cannot.",
            "Demons manifest themselves in people in different ways. For instance, out of nowhere, somebody can become very angry for no reason. That's not just an emotion. That's a demon."};
    static final String[] angerAuthor = new String[]{"Jack Layton", "Joel Osteen", "Ralph Waldo Emerson", "Aristotle", "Joyce Meyer", "Stephen Hawking", "Jorge Bucay", "Plato", "Stephen Baldwin"};

    static final String[] disgust = new String[]{"I used to be disgusted; now I try to be amused.",
            "Sameness is the mother of disgust, variety the cure.",
            "If you cannot work with love but only with distaste, it is better that you should leave your work.",
            "My friends, love is better than anger. Hope is better than fear. Optimism is better than despair. So let us be loving, hopeful and optimistic. And we'll change the world.",
            "Every day we have plenty of opportunities to get angry, stressed or offended. But what you're doing when you indulge these negative emotions is giving something outside yourself power over your happiness. You can choose to not let little things upset you.",
            "I'm doing my best to be mindful about how I'm living: to be kind and patient, and not to impose a bad mood on somebody else. Being mindful is as good a way to be spiritual as anything else."};
    static final String[] disgustAuthor = new String[]{"Elvis Costello", "Petrarch", "Khalil Gibran", "Jack Layton", "Joel Osteen", "Deirdre O'Kane"};

    static final String[] fear = new String[]{"You gain strength, courage, and confidence by every experience in which you really stop to look fear in the face. You are able to say to yourself, 'I lived through this horror. I can take the next thing that comes along.'",
            "If you want to conquer fear, don't sit home and think about it. Go out and get busy.",
            "I've learned that fear limits you and your vision. It serves as blinders to what may be just a few steps down the road for you. The journey is valuable, but believing in your talents, your abilities, and your self-worth can empower you to walk down an even brighter path. Transforming fear into freedom - how great is that?",
            "Courage is resistance to fear, mastery of fear, not absence of fear.",
            "The fear of death follows from the fear of life. A man who lives fully is prepared to die at any time.",
            "Courage is knowing what not to fear.",
            "Love is the master key that opens the gates of happiness, of hatred, of jealousy, and, most easily of all, the gate of fear.",
            "Don't let fear or insecurity stop you from trying new things. Believe in yourself. Do what you love. And most importantly, be kind to others, even if you don't like them.",
            "The moment we begin to fear the opinions of others and hesitate to tell the truth that is in us, and from motives of policy are silent when we should speak, the divine floods of light and life no longer flow into our souls."};
    static final String[] fearAuthor = new String[]{"Eleanor Roosevelt", "Dale Carnegie", "Soledad O'Brien", "Mark Twain", "Mark Twain", "Plato", "Oliver Wendell Holmes, Sr.", "Stacy London", "Elizabeth Cady Stanton"};

    static final String[] neutral = new String[]{"Today I choose life. Every morning when I wake up I can choose joy, happiness, negativity, pain... To feel the freedom that comes from being able to continue to make mistakes and choices - today I choose to feel life, not to deny my humanity but embrace it.",
            "Do not take life too seriously. You will never get out of it alive.",
            "Nothing is impossible, the word itself says 'I'm possible'!",
            "Behind every great man is a woman rolling her eyes.",
            "There are only three things women need in life: food, water, and compliments.",
            "Roses are red, violets are blue, I'm schizophrenic, and so am I.",
            "I love deadlines. I like the whooshing sound they make as they fly by.",
            "Procrastination is the art of keeping up with yesterday.",
            "A woman's mind is cleaner than a man's: She changes it more often.",
            "I wear a necklace, cause I wanna know when I'm upside down.",
            "Don't give up on your dreams. Keep sleeping."};
    static final String[] neutralAuthor = new String[]{"Kevyn Aucoin", "Elbert Hubbard", "Audrey Hepburn", "Jim Carrey", "Chris Rock", "Oscar Levant", "Douglas Adams", "Don Marquis", "Oliver Herford", "Mitch Hedberg", "Einstein"};

    static final String[] surprise = new String[]{"The moments of happiness we enjoy take us by surprise. It is not that we seize them, but that they seize us.",
            "I'm writing a new love story, set in eastern North Carolina. Surprise, surprise, huh?",
            "May your coming year be filled with magic and dreams and good madness. I hope you read some fine books and kiss someone who thinks you're wonderful, and don't forget to make some art -- write or draw or build or sing or live as only you can. And I hope, somewhere in the next year, you surprise yourself.",
            "The world is like that - incomprehensible and full of surprises.",
            "Time changes everything except something within us which is always surprised by change.",
            "Expect the best, plan for the worst, and prepare to be surprised",
            "Searching is half the fun: life is much more manageable when thought of as a scavenger hunt as opposed to a surprise party.",
            "There is no surprise more magical than the surprise of being loved: It is God's finger on man's shoulder.",
            "The secret to humor is surprise",
            "The greatest thing you can do is surprise yourself."};
    static final String[] surpriseAuthor = new String[]{"Ashley Montagu", "Nicholas Sparks", "Neil Gaiman", "Jorge Amado", "Thomas Hardy", "Denis Waitley", "Jimmy Buffett", "Charles Morgan", "Aristotle", "Steve Martin"};

    static final String[] contempt = new String[]{"Any newspaper, from the first line to the last, is nothing but a web of horrors, I cannot understand how an innocent hand can touch a newspaper without convulsing in disgust.",
            "Preservation of one's own culture does not require contempt or disrespect for other cultures.",
            "Constant exposure to dangers will breed contempt for them.",
            "Between flattery and admiration there often flows a river of contempt.",
            "It is a peculiar sensation, this double-consciousness, this sense of always looking at one's self through the eyes of others, of measuring one's soul by the tape of a world that looks on in amused contempt and pity.",
            "The only cure for contempt is counter-contempt.",
            "My friends, love is better than anger. Hope is better than fear. Optimism is better than despair. So let us be loving, hopeful and optimistic. And we'll change the world.",
            "Every day we have plenty of opportunities to get angry, stressed or offended. But what you're doing when you indulge these negative emotions is giving something outside yourself power over your happiness. You can choose to not let little things upset you.",
            "Life is one big road with lots of signs. So when you riding through the ruts, don't complicate your mind. Flee from hate, mischief and jealousy. Don't bury your thoughts, put your vision to reality. Wake Up and Live!"};
    static final String[] contemptAuthor = new String[]{"Charles Baudelaire", "Cesar Chavez", "Lucius Annaeus Seneca", "W.E.B. Du Bois", "H.L. Mencken", "Jack Layton", "Joel Osteen", "Bob Marley"};


    public EmotionDisplayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_emotion_display, container, false);
        imgView = (ImageView) rootView.findViewById(R.id.img_emotion);
        textView = (TextView) rootView.findViewById(R.id.quote_emotion);
        authorView = (TextView) rootView.findViewById(R.id.quote_author);
        Typeface courgette = Typeface.createFromAsset(getActivity().getAssets(), "Pacifico.ttf");
        Typeface gloria = Typeface.createFromAsset(getActivity().getAssets(), "GloriaHallelujah.ttf");
        authorView.setTypeface(gloria);
        textView.setTypeface(courgette);
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

        Random random = new Random();
        /// set images and quotes
        if (emo.equals("anger")) {

            int num = random.nextInt(anger.length);

            textView.setText('\"' + anger[num] + '\"');
            authorView.setText("-" + angerAuthor[num] + "-");
            imgView.setImageResource(R.drawable.anger);
        } else if (emo.equals("disgust")) {
            int num = random.nextInt(disgust.length);

            textView.setText('\"' + disgust[num] + '\"');
            authorView.setText("-" + disgustAuthor[num] + "-");
            imgView.setImageResource(R.drawable.disgusted);
        } else if (emo.equals("fear")) {
            int num = random.nextInt(fear.length);

            textView.setText('\"' + fear[num] + '\"');
            authorView.setText("-" + fearAuthor[num] + "-");
            imgView.setImageResource(R.drawable.fear);
        } else if (emo.equals("neutral")) {
            int num = random.nextInt(neutral.length);

            textView.setText('\"' + neutral[num] + '\"');
            authorView.setText("-" + neutralAuthor[num] + "-");
            imgView.setImageResource(R.drawable.neutral);
        } else if (emo.equals("happiness")) {
            int num = random.nextInt(happiness.length);
            authorView.setText("-" + happinessAuthor[num] + "-");
            textView.setText('\"' + happiness[num] + '\"');
            imgView.setImageResource(R.drawable.happiness);
        } else if (emo.equals("sadness")) {
            int num = random.nextInt(sadness.length);

            textView.setText('\"' + sadness[num] + '\"');
            authorView.setText("-" + sadnessAuthor[num] + "-");
            imgView.setImageResource(R.drawable.sadness);
        } else if (emo.equals("surprise")) {
            int num = random.nextInt(surprise.length);

            textView.setText('\"' + surprise[num] + '\"');
            authorView.setText("-" + surpriseAuthor[num] + "-");
            imgView.setImageResource(R.drawable.surprise);
        } else if (emo.equals("contempt")) {
            int num = random.nextInt(contempt.length);

            textView.setText('\"' + contempt[num] + '\"');
            authorView.setText("-" + contemptAuthor[num] + "-");
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
