package com.singun.openvpn.ui;

/**
 * Created by Administrator on 2017-05-19.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.singun.openvpn.client.R;

import static android.app.admin.DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE;
import static android.app.admin.DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME;

/**
 * This {@link Fragment} handles initiation of managed profile provisioning.
 */
public class SetupProfileFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_PROVISION_MANAGED_PROFILE = 1;

    public static SetupProfileFragment newInstance() {
        return new SetupProfileFragment();
    }

    public SetupProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.set_up_profile).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_up_profile: {
                provisionManagedProfile();
                break;
            }
        }
    }

    /**
     * Initiates the managed profile provisioning. If we already have a managed profile set up on
     * this device, we will get an error dialog in the following provisioning phase.
     */
    private void provisionManagedProfile() {
        Activity activity = getActivity();
        if (null == activity) {
            return;
        }
        Intent intent = new Intent(ACTION_PROVISION_MANAGED_PROFILE);
        intent.putExtra(EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME,
                activity.getApplicationContext().getPackageName());
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_PROVISION_MANAGED_PROFILE);
            activity.finish();
        } else {
            Toast.makeText(activity, "Device provisioning is not enabled. Stopping.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PROVISION_MANAGED_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getActivity(), "Provisioning done.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Provisioning failed.", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
