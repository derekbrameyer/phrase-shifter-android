package com.doomonafireball;

import com.doomonafireball.model.Phrase;
import com.doomonafireball.util.SharedPrefsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {

    private Button addBTN;
    private Button shuffleBTN;
    private Button switchBTN;

    private PhraseAdapter mPhraseAdapter;
    private ListView mListView;

    private Context mContext;

    private SharedPrefsUtil sharedPrefsUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mContext = this;

        SharedPrefsUtil.initialize(this);
        sharedPrefsUtil = SharedPrefsUtil.getInstance();

        addBTN = (Button) findViewById(R.id.BTN_add);
        shuffleBTN = (Button) findViewById(R.id.BTN_shuffle);
        switchBTN = (Button) findViewById(R.id.BTN_switch);
        addBTN.setOnClickListener(addClickListener);
        shuffleBTN.setOnClickListener(shuffleClickListener);
        switchBTN.setOnClickListener(switchClickListener);

        mListView = (ListView) findViewById(R.id.LV_list);
        ArrayList<Phrase> dataPhrases = getPhrases();
        ArrayList<Boolean> dataBools = new ArrayList<Boolean>();
        for (int i = 0; i < dataPhrases.size(); i++) {
            dataBools.add(false);
        }
        mPhraseAdapter = new PhraseAdapter(this, dataPhrases, dataBools);
        mListView.setAdapter(mPhraseAdapter);
        mListView.setOnItemClickListener(listItemClickListener);
        mListView.setOnItemLongClickListener(listItemLongClickListener);

        switchBTN.setText(mPhraseAdapter.getDisplayText());
    }

    private ArrayList<Phrase> getPhrases() {
        ArrayList<Phrase> phrases = new ArrayList<Phrase>();
        try {
            String phrasesJsonString = sharedPrefsUtil.getPhrasesAsJson();
            JSONArray phrasesJsonArray = new JSONArray(phrasesJsonString);
            for (int i = 0; i < phrasesJsonArray.length(); i++) {
                JSONObject o = new JSONObject((String) phrasesJsonArray.get(i));
                Phrase p = new Phrase(o);
                phrases.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return phrases;
    }

    private void setPhrases(ArrayList<Phrase> phrases) {
        JSONArray phrasesJsonArray = new JSONArray();
        for (int i = 0; i < phrases.size(); i++) {
            phrasesJsonArray.put(phrases.get(i).toJson());
        }
        String phrasesJsonString = phrasesJsonArray.toString();
        sharedPrefsUtil.setPhrasesAsJson(phrasesJsonString);
    }

    private AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            // Toggle visibility of pronunciation and translation
            mPhraseAdapter.toggleVisibility(position);
            mPhraseAdapter.notifyDataSetChanged();
        }
    };

    private AdapterView.OnItemLongClickListener listItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            // Delete object
            final int mPosition = position;
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setTitle("Delete entry?");
            alert.setMessage("Do you want to delete this entry?");
            final AlertDialog dialog = alert.create();
            alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ArrayList<Phrase> currPhrases = mPhraseAdapter.mPhrases;
                    ArrayList<Boolean> currBools = mPhraseAdapter.mVisibilities;
                    currPhrases.remove(mPosition);
                    currBools.remove(mPosition);
                    setPhrases(currPhrases);
                    mPhraseAdapter.setPhrases(currPhrases);
                    mPhraseAdapter.setVisibilities(currBools);
                    mPhraseAdapter.notifyDataSetChanged();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                }
            });

            alert.show();
            return false;
        }
    };

    private View.OnClickListener switchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*mPhraseAdapter.toggleDisplay();
            mPhraseAdapter.notifyDataSetChanged();
            switchBTN.setText(mPhraseAdapter.getDisplayText());    */
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Some text");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Some other text");

            startActivity(Intent.createChooser(shareIntent, "Title for chooser"));
        }
    };

    private View.OnClickListener addClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View alertView = inflater.inflate(R.layout.phrase_input_dialog, null);

            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setTitle("Input phrase");
            alert.setView(alertView);
            final AlertDialog dialog = alert.create();

            final EditText phraseET = (EditText) alertView.findViewById(R.id.ET_phrase);
            final EditText pronunciationET = (EditText) alertView.findViewById(R.id.ET_pronunciation);
            final EditText translationET = (EditText) alertView.findViewById(R.id.ET_translation);

            alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String phrase = phraseET.getText().toString();
                    String pronunciation = pronunciationET.getText().toString();
                    String translation = translationET.getText().toString();
                    Phrase p = new Phrase(phrase, pronunciation, translation);
                    ArrayList<Phrase> currPhrases = mPhraseAdapter.mPhrases;
                    ArrayList<Boolean> currBools = mPhraseAdapter.mVisibilities;
                    currPhrases.add(p);
                    currBools.add(false);
                    setPhrases(currPhrases);
                    mPhraseAdapter.setPhrases(currPhrases);
                    mPhraseAdapter.setVisibilities(currBools);
                    mPhraseAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                }
            });

            alert.show();
        }
    };

    private View.OnClickListener shuffleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<Phrase> newPhrases = getPhrases();
            ArrayList<Boolean> newBools = new ArrayList<Boolean>();
            for (int i = 0; i < newPhrases.size(); i++) {
                newBools.add(false);
            }
            Collections.shuffle(newPhrases);
            mPhraseAdapter.setPhrases(newPhrases);
            mPhraseAdapter.setVisibilities(newBools);
            mPhraseAdapter.notifyDataSetChanged();
        }
    };

    private class PhraseAdapter extends BaseAdapter {

        public static final int DISPLAY_PHRASE = 31337;
        public static final int DISPLAY_PRONUNCIATION = 31338;
        public static final int DISPLAY_TRANSLATION = 31339;

        ArrayList<Phrase> mPhrases;
        ArrayList<Boolean> mVisibilities;
        Context mContext;
        private final LayoutInflater inflater;
        private int mDisplay;

        private class ViewHolder {

            public TextView largeTV;
            public TextView med1TV;
            public TextView med2TV;
        }

        PhraseAdapter(Context context, ArrayList<Phrase> phrases, ArrayList<Boolean> visibilities) {
            super();
            this.mDisplay = DISPLAY_PHRASE;
            this.mContext = context;
            if (phrases == null) {
                this.mPhrases = new ArrayList<Phrase>();
            } else {
                this.mPhrases = phrases;
            }
            if (visibilities == null) {
                this.mVisibilities = new ArrayList<Boolean>();
            } else {
                this.mVisibilities = visibilities;
            }
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void toggleDisplay() {
            switch (this.mDisplay) {
                case DISPLAY_PHRASE:
                    this.mDisplay = DISPLAY_PRONUNCIATION;
                    break;
                case DISPLAY_PRONUNCIATION:
                    this.mDisplay = DISPLAY_TRANSLATION;
                    break;
                case DISPLAY_TRANSLATION:
                    this.mDisplay = DISPLAY_PHRASE;
                    break;
                default:
                    this.mDisplay = DISPLAY_PHRASE;
                    break;
            }
        }

        public String getDisplayText() {
            switch (this.mDisplay) {
                case DISPLAY_PHRASE:
                    return "Phrase";
                case DISPLAY_PRONUNCIATION:
                    return "Pronunciation";
                case DISPLAY_TRANSLATION:
                    return "Translation";
                default:
                    return "Phrase";
            }
        }

        public int getDisplay() {
            return this.mDisplay;
        }

        public void toggleVisibility(int position) {
            Boolean b = mVisibilities.get(position);
            mVisibilities.set(position, !b);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Phrase phrase = mPhrases.get(position);
            Boolean b = mVisibilities.get(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.main_list_item, null);
                TextView phraseTV = (TextView) convertView.findViewById(R.id.TV_big);
                TextView pronunciationTV = (TextView) convertView.findViewById(R.id.TV_med1);
                TextView translationTV = (TextView) convertView.findViewById(R.id.TV_med2);
                holder = new ViewHolder();
                holder.largeTV = phraseTV;
                holder.med1TV = pronunciationTV;
                holder.med2TV = translationTV;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            switch (mDisplay) {
                case DISPLAY_PHRASE:
                    holder.largeTV.setText(phrase.phrase);
                    holder.med1TV.setText(phrase.pronunciation);
                    holder.med2TV.setText(phrase.translation);
                    break;
                case DISPLAY_PRONUNCIATION:
                    holder.largeTV.setText(phrase.pronunciation);
                    holder.med1TV.setText(phrase.phrase);
                    holder.med2TV.setText(phrase.translation);
                    break;
                case DISPLAY_TRANSLATION:
                    holder.largeTV.setText(phrase.translation);
                    holder.med1TV.setText(phrase.phrase);
                    holder.med2TV.setText(phrase.pronunciation);
                    break;
                default:
                    holder.largeTV.setText(phrase.phrase);
                    holder.med1TV.setText(phrase.pronunciation);
                    holder.med2TV.setText(phrase.translation);
                    break;
            }
            if (b) {
                holder.med1TV.setVisibility(View.VISIBLE);
                holder.med2TV.setVisibility(View.VISIBLE);
            } else {
                holder.med1TV.setVisibility(View.GONE);
                holder.med2TV.setVisibility(View.GONE);
            }

            return convertView;
        }

        public void setPhrases(ArrayList<Phrase> phrases) {
            this.mPhrases = phrases;
        }

        public void setVisibilities(ArrayList<Boolean> visibilities) {
            this.mVisibilities = visibilities;
        }

        @Override
        public int getCount() {
            return mPhrases.size();
        }

        @Override
        public Object getItem(int position) {
            return mPhrases.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
