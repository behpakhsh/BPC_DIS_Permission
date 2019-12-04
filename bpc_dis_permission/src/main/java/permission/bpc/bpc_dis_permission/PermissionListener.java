package permission.bpc.bpc_dis_permission;

import java.util.List;

public interface PermissionListener {

    void onPermissionGranted();

    void onPermissionDenied(List<String> permissions);

    void onPermissionDeniedForEver();

}