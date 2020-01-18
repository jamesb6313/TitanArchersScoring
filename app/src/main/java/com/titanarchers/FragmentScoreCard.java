package com.titanarchers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class FragmentScoreCard extends Fragment {

    private ArrowPointViewModel model;

    WebView wv_ScoreCard;
    TargetView v_TargetImage;
    View v_target;


    Bitmap mBitmap;
    Button btn_share;
    String html;

    private String fn;
    private static final int CREATE_REQUEST_CODE = 40;

    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        model = ViewModelProviders.of(this.getActivity()).get(ArrowPointViewModel.class);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            if (model != null) {
                Uri baseUrl;

                File newFile = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "newTargetImage.png");

                //Save v_target view to append img src PNG file in webView HTML
                baseUrl = takeScreenShot(v_target, newFile);    // have v_target save as file

                // build HTML to Load into webView
                //File fImage = savebitmap("newTargetImage.png");
                //baseUrl = FileProvider.getUriForFile(getActivity(), "com.titanarchers", fImage);

                String html = createHTMLTable("newTargetImage.png");
                wv_ScoreCard.loadDataWithBaseURL(baseUrl.toString(), html,"text/html","UTF-8", null);
                //Works without these stmts - try later
                /*wv_ScoreCard.setWebViewClient(new WebViewClient());
                wv_ScoreCard.getSettings().setBuiltInZoomControls(true);
                wv_ScoreCard.getSettings().setDisplayZoomControls(false);
                wv_ScoreCard. getSettings().setSupportZoom(true);
                wv_ScoreCard.getSettings().setUseWideViewPort(true);
                wv_ScoreCard.getSettings().setLoadWithOverviewMode(true);
                wv_ScoreCard.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);*/
            }
        }
    }

    // This method will be invoked when the Fragment view object is created.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_scorecard, container);

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

                    //Uri pictureUri;
                    //File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "newTargetImage.png");
                    setBtn_share(wv_ScoreCard);
                }
            });

        }
        return retView;

    }

    public Uri takeScreenShot(View v1, File fn){
        // create bitmap screen capture
        Bitmap bitmap;
        v1.setDrawingCacheEnabled(true);

        // bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        bitmap = loadBitmapFromView(v1, v1.getWidth(), v1.getHeight());
        v1.setDrawingCacheEnabled(false);

        Uri newPictureUri;

        newPictureUri = FileProvider.getUriForFile(getActivity(), "com.titanarchers", fn);

        OutputStream outStream = null;

        if (fn.exists()) {
            fn.delete();

            //fn = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "newTargetImage.png");
            newPictureUri = FileProvider.getUriForFile(getActivity(), "com.titanarchers", fn);
            //file = new File(extStorageDirectory, filename);
            Log.e("newFile exist", "" + fn + ",Bitmap= " + newPictureUri.getPath().toString());
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

        //sendScreenShot(imageFile);
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, width, height);

        //Get the view’s background
        Drawable bgDrawable =v.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(c);
        else
            //does not have background drawable, then draw white background on the canvas
            c.drawColor(Color.WHITE);
        v.draw(c);
        return b;
    }

    //SEE:https://guides.codepath.com/android/Sharing-Content-with-Intents (using file Provider - create res/xml/fileprovider.xml , change AndroidManifest.xml)
    private File savebitmap(String filename) {

        Uri newPictureUri;
        File newFile = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);
        newPictureUri = FileProvider.getUriForFile(getActivity(), "com.titanarchers", newFile);


        OutputStream outStream = null;

        if (newFile.exists()) {
            newFile.delete();

            newFile = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);
            newPictureUri = FileProvider.getUriForFile(getActivity(), "com.titanarchers", newFile);
            //file = new File(extStorageDirectory, filename);
            Log.e("newFile exist", "" + newFile + ",Bitmap= " + filename);
        }
        try {
            // make a new bitmap from your file
            Bitmap bitmap = BitmapFactory.decodeFile(newFile.getName());
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.archery_target);

            outStream  = getActivity().getContentResolver().openOutputStream(newPictureUri);

            //outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("newFile", "" + newFile);
        return newFile;

    }

    private void setBtn_share(View wv_ScoreCard) {
        Uri pictureUri;
        File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
        pictureUri = takeScreenShot(wv_ScoreCard, file);


        // wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration
        //pictureUri = FileProvider.getUriForFile(getActivity(), "com.titanarchers", file);

/*        try {
            OutputStream fout = getActivity().getContentResolver().openOutputStream(pictureUri);
            //File imageFile = new File(mPath);

            //fout = new FileOutputStream(imageFile);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 90, fout);
            fout.flush();
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share images..."));

    }
    private String createHTMLTable(String imgFileName) {

        List<ArrowPoint> apList = model.getArrowPoints().getValue();
        final StringBuilder sb_HTML = new StringBuilder();
        String imageElem = "<img src=" + "'" + imgFileName +"' style='width:100%'>";
        String html = " <html><head><style>" +
                " table { font-size:12px; text-align:center; vertical-align:middle;font-weight:bold; }" +
                "</style></head><body>";
        html = html + imageElem +
                //"<img src='archery_target.9.png'  style='width:100%'>" +
                "</br> <table border='1' bordercolor='green' style='width:100%;height:10%'> <tr><b><th>A1</th><th>A2</th><th>A3</th><th>A4</th><th>A5</th><th>A6</th><th>Total</th></b></tr>";
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

            tempStr = "<td>" + apList.get(i).score + "</td>";
            sb_HTML.append(tempStr);

            if ((i + 1) % 6 == 0) {
                tempStr = "<td>" + subtotal + "</td>";
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
        sb_HTML.append("<tr> <td style='text-align:right' colspan='6'>Total</td><td>"+total+"</td></tr>");
        sb_HTML.append("</table>");
        html = sb_HTML.toString();

        return html;
    }
}
