package com.titanarchers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
//import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class FragmentScoreCard extends Fragment {

    private ArrowPointViewModel model;

    private WebView wv_ScoreCard;
    private View v_target;
    private MainActivity mMainActivity;

    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        model = ViewModelProviders.of(this.getActivity()).get(ArrowPointViewModel.class);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            if (model != null) {
                Uri baseUrl;

                File newFile = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "newTargetImage.png");

                //Save v_target view to append img src PNG file in webView HTML
                //TODO - currently uses next arrow color instead of current arrow color. Need to sync
                baseUrl = takeScreenShot(v_target, newFile);    // have v_target save as file

                String html = createHTMLTable("newTargetImage.png");
                wv_ScoreCard.loadDataWithBaseURL(baseUrl.toString(), html, "text/html", "UTF-8", null);
            }
        }
    }

    // This method will be invoked when the Fragment view object is created.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_scorecard, container);
        Button btn_share;

        if (retView != null) {
            // Get ref to webView
            wv_ScoreCard = retView.findViewById(R.id.wv_ScoreCard);

            // Get ref to TargetView - Get Fragment belonged Activity
            final FragmentActivity fragmentBelongActivity = getActivity();
            FragmentManager fragmentManager = fragmentBelongActivity.getSupportFragmentManager();

            // Get target Fragment object from MainActivity()
            Fragment targetImage = fragmentManager.findFragmentById(R.id.fragmentTarget);
            v_target = targetImage.getView();

            btn_share = retView.findViewById(R.id.btn_share);
            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setBtn_share(wv_ScoreCard);
                }
            });

        }
        return retView;

    }

    //SEE:https://guides.codepath.com/android/Sharing-Content-with-Intents (using file Provider - create res/xml/fileprovider.xml , change AndroidManifest.xml)
    private Uri takeScreenShot(View v1, File fn) {
        // create bitmap screen capture
        Bitmap bitmap;
        v1.setDrawingCacheEnabled(true);

        bitmap = loadBitmapFromView(v1, v1.getWidth(), v1.getHeight());
        v1.setDrawingCacheEnabled(false);

        Uri newPictureUri;
        newPictureUri = FileProvider.getUriForFile(getActivity(), "com.titanarchers", fn);

        if (fn.exists()) {
            fn.delete();

            newPictureUri = FileProvider.getUriForFile(getActivity(), "com.titanarchers", fn);
            Log.i("newFile exist", "FragmentScoreCard line 119 (takeScreenShot) fn = " + fn + ",Bitmap= " + newPictureUri.getPath());
        }
        try {
            OutputStream fout = getActivity().getContentResolver().openOutputStream(newPictureUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fout);
            fout.flush();
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return newPictureUri;
        } catch (IOException e) {
            e.printStackTrace();
            return newPictureUri;
        }
        return newPictureUri;

    }

    private static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, width, height);

        //Get the view’s background
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(c);
        else
            //does not have background drawable, then draw white background on the canvas
            c.drawColor(Color.WHITE);
        v.draw(c);
        return b;
    }

    private void setBtn_share(View wv_ScoreCard) {
        String fn;
        String timeStr = DateFormat.format("MM_dd_yyyy_hh_mm", System.currentTimeMillis()).toString();
        fn ="TitanTargetImage_"+timeStr +".png";

        Uri pictureUri;
        File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fn);
        pictureUri = takeScreenShot(wv_ScoreCard, file);


        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share images..."));

    }


    public int defaultDistance;
    public String defaultName;
    public String defaultUnits;

    private boolean loadSavedPreferences() {
        boolean result = false;

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        defaultDistance = sharedPreferences.getInt("storedDistance", defaultDistance);
        defaultName = sharedPreferences.getString("storedName", defaultName);
        defaultUnits = sharedPreferences.getString("storedUnits", defaultUnits);
        if (defaultName != null) {
            result = defaultName.equalsIgnoreCase("YourName");
        }
        return result;
    }

    private String createHTMLTable(String imgFileName) {

        String timeStr = DateFormat.format("MMM dd, yyyy hh:mm a", System.currentTimeMillis()).toString();
        List<ArrowPoint> apList = model.getArrowPoints().getValue();

        final StringBuilder sb_HTML = new StringBuilder();
        String imageElem = "<img src=" + "'" + imgFileName +"' style='width:100%'>";
        String html = " <html><head><style>" +
                " table { font-size:12px; text-align:center; vertical-align:middle;font-weight:bold; }" +
                "</style></head><body>";
        html = html + imageElem +
                //"<img src='archery_target.9.png'  style='width:100%'>" +
                "<br> <table border='1' bordercolor='green' style='width:100%;height:10%'> <tr><b><th>A1</th><th>A2</th><th>A3</th><th>A4</th><th>A5</th><th>A6</th><th>Total</th></b></tr>";
        sb_HTML.append(html);
        sb_HTML.append("<tr>");

        int subtotal = 0;
        int total = 0;
        int cntr = 0;
        ArrowPoint ap;

        for (int i = 0; i < apList.size(); i++) {
            cntr++;
            ap = apList.get(i);
            subtotal += (ap.score == 11) ? 10 : ap.score;
            total += (ap.score == 11) ? 10 : ap.score;

            String tempStr;
            String arrowColor = String.format("#%06x", ap.color & 0xffffff);

            tempStr = "<td style='color:" + arrowColor + "'>" + apList.get(i).score + "</td>";
            sb_HTML.append(tempStr);

            if ((i + 1) % 6 == 0) {
                tempStr = "<td style='color:red'>" + subtotal + "</td>";
                sb_HTML.append(tempStr);
                sb_HTML.append("</tr>");
                subtotal = 0;
            }

        }

        //Complete scorecard if not finished
        for (int i = cntr; i < 30; i++) {
            cntr++;
            sb_HTML.append("<td>00</td>");

            if ((i + 1) % 6 == 0) {
                sb_HTML.append("<td>00</td></tr>");
            }
        }
        html = "<tr> <td style='text-align:right' colspan='6'>Total</td><td>"+total+"</td></tr>";
        sb_HTML.append(html);
        html = "</table><h3> Date: " + timeStr + "</h3>";
        sb_HTML.append(html);

        mMainActivity.loadSavedPreferences();
        if (mMainActivity.defaultName != null || !mMainActivity.defaultName.equalsIgnoreCase("YourName")) {
            html = "<br><h3> Name: " + mMainActivity.defaultName + " at " + mMainActivity.defaultDistance + " " + mMainActivity.defaultUnits + "</h3>";
            sb_HTML.append(html);
        }
        html = sb_HTML.toString();

        return html;
    }
}
