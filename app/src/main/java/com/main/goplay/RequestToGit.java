package com.main.goplay;

import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class RequestToGit {
    final String URL_TO_LINK = "aHR0cHM6Ly9yYXcuZ2l0aHVidXNlcmNvbnRlbnQuY29tL0dvbGRBbWlnby9nb3BsYXkvbWFpbi9MaW5r";
    final String URL_TO_BOOL = "aHR0cHM6Ly9yYXcuZ2l0aHVidXNlcmNvbnRlbnQuY29tL0dvbGRBbWlnby9nb3BsYXkvbWFpbi9BY3RpdmU=";

    public RequestToGit(RequestListener requestListener) {
        new Thread(() -> {
            try {
                URL url = new URL(new String(Base64.decode(URL_TO_BOOL, Base64.DEFAULT)));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                boolean result = (char) in.read() == '1';
                requestListener.waiterForBool(result);
                if (!result) {
                    try {
                        URL urlLink = new URL(new String(Base64.decode(URL_TO_LINK, Base64.DEFAULT)));
                        HttpURLConnection urlConnection2 = (HttpURLConnection) urlLink.openConnection();
                        InputStream in2 = urlConnection2.getInputStream();
                        Scanner scanner = new Scanner(in2);
                        scanner.useDelimiter("\\A");
                        if (scanner.hasNext()) {
                            requestListener.waiterForLink(scanner.next());
                        }
                        urlConnection2.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                requestListener.rejection();
            }
        }).start();
    }
}
