package com.doomonafireball.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: derek Date: 5/10/12 Time: 7:20 PM
 */
public class Phrase {

    public String phrase = "";
    public String pronunciation = "";
    public String translation = "";

    public Phrase(String mPhrase, String mPronunciation, String mTranslation) {
        this.phrase = mPhrase;
        this.pronunciation = mPronunciation;
        this.translation = mTranslation;
    }

    public Phrase(String json) {
        try {
            JSONObject o = new JSONObject(json);
            this.phrase = o.optString("phrase");
            this.pronunciation = o.optString("pronunciation");
            this.translation = o.optString("translation");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Phrase(JSONObject o) {
        this.phrase = o.optString("phrase");
        this.pronunciation = o.optString("pronunciation");
        this.translation = o.optString("translation");
    }

    public String toJson() {
        try {
            JSONObject o = new JSONObject();
            o.put("phrase", phrase);
            o.put("pronunciation", pronunciation);
            o.put("translation", translation);
            return o.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
