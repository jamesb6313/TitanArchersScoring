package com.titanarchers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class FragmentScoreCard extends Fragment {

    private ArrowPointViewModel model;

    private WebView wv_ScoreCard;
    //TargetView v_TargetImage;
    private View v_target;


    //Bitmap mBitmap;
    //String html;

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

                String html = createHTMLTable("newTargetImage.png");
                wv_ScoreCard.loadDataWithBaseURL(baseUrl.toString(), html, "text/html", "UTF-8", null);

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
        Button btn_share, btn_save;

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

            btn_save = retView.findViewById(R.id.btn_save);
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Uri pictureUri;
                    //File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "newTargetImage.png");
                    setBtn_save(wv_ScoreCard);
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

        // bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        bitmap = loadBitmapFromView(v1, v1.getWidth(), v1.getHeight());
        v1.setDrawingCacheEnabled(false);

        Uri newPictureUri;
        newPictureUri = FileProvider.getUriForFile(getActivity(), "com.titanarchers", fn);

        //OutputStream outStream = null;

        if (fn.exists()) {
            fn.delete();

            newPictureUri = FileProvider.getUriForFile(getActivity(), "com.titanarchers", fn);
            Log.e("newFile exist", "" + fn + ",Bitmap= " + newPictureUri.getPath());
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
        String timeStr = DateFormat.format("dd_MM_yyyy_hh_mm", System.currentTimeMillis()).toString();
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

    private void setBtn_save(View wv_ScoreCard){
        String timeStr = DateFormat.format("dd_MM_yyyy_hh_mm", System.currentTimeMillis()).toString();
        fn ="TitanTargetImage_"+timeStr +".png";

        //Uri pictureUri;
        //File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fn);
        //pictureUri = takeScreenShot(wv_ScoreCard, file);

        Intent intentShareFile = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intentShareFile.addCategory(Intent.CATEGORY_OPENABLE);

        intentShareFile.putExtra(Intent.EXTRA_TITLE, fn);
        //intentShareFile.putExtra(Intent.EXTRA_STREAM, pictureUri);
        intentShareFile.setType("image/*");

        startActivityForResult(intentShareFile, CREATE_REQUEST_CODE);

        //need onActivityResult - see below
    }

/*
TODO: causes API deprecated error

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode,resultCode,resultData);

        Uri currentUri;

        if (resultCode == Activity.RESULT_OK)
        {

            if (requestCode == CREATE_REQUEST_CODE)
            {
                if (resultData != null) {
                    currentUri = resultData.getData();
                    //writeFileContent(currentUri);
                }
            }
        }
    }*/

   /* private void writeFileContent(Uri uri)
    {

        wv_ScoreCard.measure(View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        wv_ScoreCard.layout(0, 0, wv_ScoreCard.getMeasuredWidth(), wv_ScoreCard.getMeasuredHeight());
        wv_ScoreCard.setDrawingCacheEnabled(true);
        wv_ScoreCard.buildDrawingCache();
        Bitmap bm = Bitmap.createBitmap(wv_ScoreCard.getMeasuredWidth(),
                wv_ScoreCard.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas bigcanvas = new Canvas(bm);
        Paint paint = new Paint();
        int iHeight = bm.getHeight();
        bigcanvas.drawBitmap(bm, 0, iHeight, paint);
        wv_ScoreCard.draw(bigcanvas);
        System.out.println("WIDTH=" + bigcanvas.getWidth());
        System.out.println("HEIGHT=" + bigcanvas.getHeight());

        if (bm != null) {
            try {
                OutputStream fOut = getActivity().getContentResolver().openOutputStream(uri);

                bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                bm.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }*/

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
        sb_HTML.append("<tr> <td style='text-align:right' colspan='6'>Total</td><td>"+total+"</td></tr>");
        sb_HTML.append("</table>");
        html = sb_HTML.toString();

        return html;
    }
}
