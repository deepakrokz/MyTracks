/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.apps.mytracks.fragments;

import com.google.android.apps.mytracks.util.PreferencesUtils;
import com.google.android.maps.mytracks.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

/**
 * A DialogFrament to confirm sync to Google Drive.
 * 
 * @author Jimmy Shih
 */
public class ConfirmSyncDialogFragment extends DialogFragment {

  /**
   * Interface for caller of this dialog fragment.
   * 
   * @author Jimmy Shih
   */
  public interface ConfirmSyncCaller {

    /**
     * Called when confirm sync is done.
     */
    public void onConfirmSyncDone(boolean enable);
  }

  public static final String CONFIRM_SYNC_DIALOG_TAG = "confirmSyncDialog";

  private static final String KEY_TITLE_ID = "titleId";
  private static final String KEY_MESSAGE = "message";

  private ConfirmSyncCaller caller;
  private FragmentActivity fragmentActivity;

  public static ConfirmSyncDialogFragment newInstance(int titleId, String message) {
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_TITLE_ID, titleId);
    bundle.putString(KEY_MESSAGE, message);

    ConfirmSyncDialogFragment confirmSyncDialogFragment = new ConfirmSyncDialogFragment();
    confirmSyncDialogFragment.setArguments(bundle);
    return confirmSyncDialogFragment;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      caller = (ConfirmSyncCaller) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(
          activity.toString() + " must implement " + ConfirmSyncCaller.class.getSimpleName());
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    fragmentActivity = getActivity();
    if (PreferencesUtils.getBoolean(
        fragmentActivity, R.string.drive_sync_key, PreferencesUtils.DRIVE_SYNC_DEFAULT)) {
      dismiss();
      caller.onConfirmSyncDone(false);
      return;
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    int titleId = getArguments().getInt(KEY_TITLE_ID);
    String message = getArguments().getString(KEY_MESSAGE);
    return new AlertDialog.Builder(fragmentActivity).setMessage(message)
        .setNegativeButton(R.string.generic_no, new OnClickListener() {
            @Override
          public void onClick(DialogInterface dialog, int which) {
            caller.onConfirmSyncDone(false);
          }
        }).setPositiveButton(R.string.generic_yes, new OnClickListener() {
            @Override
          public void onClick(DialogInterface dialog, int which) {
            caller.onConfirmSyncDone(true);
          }
        }).setTitle(titleId).create();
  }

  @Override
  public void onCancel(DialogInterface arg0) {
    caller.onConfirmSyncDone(false);
  }
}