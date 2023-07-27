package space.lobanov.translate;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkReceiver extends BroadcastReceiver {
    Context context;
    Dialog dialog;

    public NetworkReceiver(Context context) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.no_internet);
        dialog.setCancelable(false);
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!isConnected()) {
            dialog.show();
        } else {
            dialog.cancel();
        }
    }
}
