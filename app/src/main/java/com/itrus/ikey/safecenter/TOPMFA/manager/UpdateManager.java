package com.itrus.ikey.safecenter.TOPMFA.manager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.itrus.ikey.safecenter.TOPMFA.utils.DialogUtil;
import com.itrus.ikey.safecenter.TOPMFA.entitiy.MyCallback;
import com.itrus.ikey.safecenter.TOPMFA.utils.Validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class UpdateManager {

    private Activity mContext;

    //返回的安装包url
    private final String updata_url = "http://192.168.100.156:9080/hawk/updata/updata.xml";

    private String apk_url = "";

    /* 下载包安装路径 */
    private static final String savePath = "/sdcard/safeCenter/";

    private static final String saveFileName = savePath + "safeCenter.apk";

    private Thread downLoadThread;

    private boolean interceptFlag = false;


    public UpdateManager(Activity context) {
        this.mContext = context;
    }

    //外部接口让主Activity调用
    public void checkUpdateInfo(final int mode) {
        final boolean[] isUpdata = new boolean[1];
        new Thread() {
            @Override
            public void run() {
                String json = sendPost(updata_url);
                if (Validator.isBlank(json)) {
//                    Log.d("pgx", "json is null maybe connect is time out");
                    return;
                }
                String version = json.substring(json.indexOf("<version>") + "<version>".length(), json.lastIndexOf("</version>"));
                apk_url = json.substring(json.indexOf("<url>") + "<url>".length(), json.lastIndexOf("</url>"));
                try {
                    String versionCode = mContext.getApplicationContext().getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
                    if (!versionCode.equals(version)) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showNoticeDialog(apk_url);
                            }
                        });
                    } else {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mode == 1) {
                                    DialogUtil.showAlert(mContext, "当前已经是最新版本.", null);
                                }
                            }
                        });
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    //提示有更新
    private void showNoticeDialog(final String url) {
        DialogUtil.showAlertOkCancel(mContext, "软件有新的版本,是否更新?", new MyCallback<Boolean>() {

            @Override
            public void onCallback(Boolean ok) {
                if (ok) {
                    showDownloadDialog(url);
                }
            }
        });
    }

    private void showDownloadDialog(String url) {

        DialogUtil.showProgress(mContext);
        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apk_url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    if (numread <= 0) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtil.dismissProgress();
                                installApk();
                            }
                        });
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);//点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * 下载apk
     */

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);

    }


    protected String sendPost(String urlStr) {
        HttpURLConnection uRLConnection = null;
        InputStream is = null;
        BufferedReader buffer = null;
        String result = null;
        try {
            URL url = new URL(urlStr);
            uRLConnection = (HttpURLConnection) url.openConnection();
            uRLConnection.setDoInput(true);
            uRLConnection.setDoOutput(true);
            uRLConnection.setRequestMethod("POST");
            uRLConnection.setUseCaches(false);
            uRLConnection.setConnectTimeout(10 * 1000);
            uRLConnection.setReadTimeout(10 * 1000);
            uRLConnection.setInstanceFollowRedirects(false);
            uRLConnection.setRequestProperty("Connection", "Keep-Alive");
            uRLConnection.setRequestProperty("Charset", "UTF-8");
            uRLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            uRLConnection.setRequestProperty("Content-Type", "application/json");

            uRLConnection.connect();


            is = uRLConnection.getInputStream();

            String content_encode = uRLConnection.getContentEncoding();

            if (null != content_encode && !"".equals(content_encode) && content_encode.equals("gzip")) {
                is = new GZIPInputStream(is);
            }

            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
            result = strBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (uRLConnection != null) {
                uRLConnection.disconnect();
            }
        }
        return result;
    }
}
