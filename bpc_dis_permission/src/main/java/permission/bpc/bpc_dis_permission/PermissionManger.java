package permission.bpc.bpc_dis_permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import bpc.dis.utilities.SharedPreferencesManager.SharedPreferencesManager;

public class PermissionManger {

    private Activity activity;
    private int permissionRequestCode;
    private SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager();
    public PermissionManger(Activity activity, int permissionRequestCode) {
        this.activity = activity;
        this.permissionRequestCode = permissionRequestCode;
    }
    private  boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    private List<String> getUnGrantedPermissions(Activity activity, List<String> permissions) {
        List<String> unGrantedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (shouldAskPermissions()) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    unGrantedPermissions.add(permission);
                }
            }
        }
        return unGrantedPermissions;
    }

    private void onFirstTimeAskingPermission(Activity activity, String permission) {
        sharedPreferencesManager.set(activity, permission, false);
    }

    private boolean isFirstTimeAskingPermission(Activity activity, String permission) {
        return sharedPreferencesManager.get(activity, permission, true);
    }


    public void checkPermission(List<String> permissions, PermissionListener permissionListener) {
        List<String> unGrantedPermissions = getUnGrantedPermissions(activity, permissions);
        if (unGrantedPermissions.size() <= 0) {
            permissionListener.onPermissionGranted();
            return;
        }

        boolean permissionDeniedForEver = false;
        boolean permissionDeniedRationale = false;

        for (String permission : unGrantedPermissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) && !isFirstTimeAskingPermission(activity, permission)) {
                permissionDeniedForEver = true;
                break;
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                permissionDeniedRationale = true;
                break;
            }
        }

        if (permissionDeniedForEver) {
            permissionListener.onPermissionDeniedForEver();
            return;
        }
        if (permissionDeniedRationale) {
            permissionListener.onPermissionDenied(unGrantedPermissions);
            return;
        }

        showRuntimeAskPermission(unGrantedPermissions);
    }

    private void showRuntimeAskPermission(List<String> permissions) {
        for (String permission : permissions) {
            onFirstTimeAskingPermission(activity, permission);
        }
        int length = permissions.size();
        ActivityCompat.requestPermissions(activity, permissions.toArray(new String[length]), permissionRequestCode);
    }

}