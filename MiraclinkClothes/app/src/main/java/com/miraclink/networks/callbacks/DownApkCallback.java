package com.miraclink.networks.callbacks;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.miraclink.networks.CallbackUtils;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DownApkCallback implements Callback {
    private static final String TAG = DownApkCallback.class.getSimpleName();
    private Context context;

    public DownApkCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        LogUtil.i(TAG, "down failure" + e);
        CallbackUtils.baseOnFailure(context, call, e, this);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()) {
            String path = null;
            File file = new File(Environment.getExternalStorageDirectory(), "ClothesAPP.apk");
            path = file.getAbsolutePath();
            FileOutputStream fos = new FileOutputStream(path);
            InputStream is = response.body().byteStream();
            long total = response.body().contentLength();
            int len = 0;
            long sum = 0;
            byte[] buf = new byte[1024];
            int xzxcount = 0;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                sum += len;
                int progress = (int) (sum * 1.0f / total * 100);

                xzxcount++;
                if (xzxcount > 100) {
                    xzxcount = xzxcount - 100;
                    context.sendBroadcast(new Intent(BroadCastAction.DOWN_APK).putExtra("progress", progress));
                }
            }
            fos.flush();
            fos.close();
            is.close();
            context.sendBroadcast(new Intent(BroadCastAction.DOWN_APK_FINISHED));
            Utils.installApk(context, path);
        } else {
            LogUtil.i(TAG, "down on response fail");
        }
    }
}
