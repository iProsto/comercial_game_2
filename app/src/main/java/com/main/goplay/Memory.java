package com.main.goplay;

import android.content.SharedPreferences;

public class Memory {

    SharedPreferences sPref;
    private final String REF_NAME = "web_url";
    private final String FIRST_START = "first_start";
    private final String STATE_NAME = "state_path";

    public Memory(SharedPreferences preferences){
        sPref = preferences;
    }

    public void saveLink(String url) {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(REF_NAME, url);
        ed.commit();
    }

    public String loadLink(String webViewURL) {
        String answerString;
        answerString = sPref.getString(REF_NAME, webViewURL);
        return answerString;
    }

    public void saveState(boolean showGame) {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(STATE_NAME, showGame);
        ed.commit();
    }

    public boolean isShowGame(boolean showGame) {
        return sPref.getBoolean(STATE_NAME, showGame);
    }

    public boolean firstStart() {
        boolean answ;
        answ = sPref.getBoolean(FIRST_START, true);

        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(FIRST_START, false);
        ed.commit();
        return answ;
    }
}
