package com.tchip.carlauncher.wxapi;

import com.tchip.carlauncher.Constant;
import com.tchip.carlauncher.R;
import com.tchip.carlauncher.ui.activity.GetFromWXActivity;
import com.tchip.carlauncher.ui.activity.SendToWXActivity;
import com.tchip.carlauncher.ui.activity.ShowFromWXActivity;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

	private Button gotoBtn, regBtn, launchBtn, checkBtn;

	private IWXAPI api;
	private boolean isRegSuccess = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry);

		api = WXAPIFactory.createWXAPI(this, Constant.WeChat.APP_ID, false);

		regBtn = (Button) findViewById(R.id.reg_btn);
		regBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isRegSuccess = api.registerApp(Constant.WeChat.APP_ID);
				Log.v(Constant.TAG, "isRegSuccess:" + isRegSuccess);
			}
		});

		gotoBtn = (Button) findViewById(R.id.goto_send_btn);
		gotoBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(WXEntryActivity.this,
						SendToWXActivity.class));
				finish();
			}
		});

		launchBtn = (Button) findViewById(R.id.launch_wx_btn);
		launchBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(WXEntryActivity.this,
						"launch result = " + api.openWXApp(), Toast.LENGTH_LONG)
						.show();
			}
		});

		checkBtn = (Button) findViewById(R.id.check_timeline_supported_btn);
		checkBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int wxSdkVersion = api.getWXAppSupportAPI();
				if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
					Toast.makeText(
							WXEntryActivity.this,
							"wxSdkVersion = "
									+ Integer.toHexString(wxSdkVersion)
									+ "\ntimeline supported", Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(
							WXEntryActivity.this,
							"wxSdkVersion = "
									+ Integer.toHexString(wxSdkVersion)
									+ "\ntimeline not supported",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			goToGetMsg();
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			goToShowMsg((ShowMessageFromWX.Req) req);
			break;
		default:
			break;
		}
	}

	@Override
	public void onResp(BaseResp resp) {
		int result = 0;

		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}

		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}

	private void goToGetMsg() {
		Intent intent = new Intent(this, GetFromWXActivity.class);
		intent.putExtras(getIntent());
		startActivity(intent);
		finish();
	}

	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
		WXMediaMessage wxMsg = showReq.message;
		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;

		StringBuffer msg = new StringBuffer();
		msg.append("description: ");
		msg.append(wxMsg.description);
		msg.append("\n");
		msg.append("extInfo: ");
		msg.append(obj.extInfo);
		msg.append("\n");
		msg.append("filePath: ");
		msg.append(obj.filePath);

		Intent intent = new Intent(this, ShowFromWXActivity.class);
		intent.putExtra(Constant.ShowMsgActivity.STitle, wxMsg.title);
		intent.putExtra(Constant.ShowMsgActivity.SMessage, msg.toString());
		intent.putExtra(Constant.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
		startActivity(intent);
		finish();
	}
}