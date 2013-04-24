package com.mohammadag.powerampshortcuts;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import com.maxmpz.audioplayer.player.PowerAMPiAPI;

public class MainActivity extends Activity implements OnClickListener {

	boolean _isSelectingShortcut = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.next_button).setOnClickListener(this);
		findViewById(R.id.prev_button).setOnClickListener(this);
		findViewById(R.id.play_pause_button).setOnClickListener(this);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		if (extras != null && extras.containsKey("PowerampAction")) {
			int action = extras.getInt("PowerampAction");
			Intent powerampActionIntent = new Intent(PowerAMPiAPI.ACTION_API_COMMAND);
			powerampActionIntent.putExtra(PowerAMPiAPI.COMMAND, action);
			startService(powerampActionIntent);
			finish();
			return;
		}
		
		if (intent.getAction().equals(Intent.ACTION_CREATE_SHORTCUT)) {
			_isSelectingShortcut = true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private String getStringForAction(int action) {
		switch (action) {
		case PowerAMPiAPI.Commands.NEXT:
			return getString(R.string.next_song);
		case PowerAMPiAPI.Commands.PREVIOUS:
			return getString(R.string.prev_song);
		case PowerAMPiAPI.Commands.TOGGLE_PLAY_PAUSE:
			return getString(R.string.play_pause);
		case PowerAMPiAPI.Commands.RESUME:
			return getString(R.string.play_song);
		case PowerAMPiAPI.Commands.PAUSE:
			return getString(R.string.pause_song);
		default:
			return null;
		}
	}
	
	private int getIconForAction(int action) {
		switch (action) {
		case PowerAMPiAPI.Commands.NEXT:
			return R.drawable.ic_action_next;
		case PowerAMPiAPI.Commands.PREVIOUS:
			return R.drawable.ic_action_prev;
		case PowerAMPiAPI.Commands.TOGGLE_PLAY_PAUSE:
		case PowerAMPiAPI.Commands.RESUME:
			return R.drawable.ic_action_play;
		case PowerAMPiAPI.Commands.PAUSE:
			return R.drawable.ic_action_pause;
		default:
			return 0;
		}
	}

	@Override
	public void onClick(View view) {
		
		Intent powerampActionIntent = new Intent(PowerAMPiAPI.ACTION_API_COMMAND);
		int action = 0;
		switch(view.getId()) {
		case R.id.next_button:
			action = PowerAMPiAPI.Commands.NEXT;
			break;
		case R.id.prev_button:
			action = PowerAMPiAPI.Commands.PREVIOUS;
			break;
		case R.id.play_pause_button:
			action = PowerAMPiAPI.Commands.TOGGLE_PLAY_PAUSE;
			break;
		default:
			break;
		}
		
		powerampActionIntent.putExtra(PowerAMPiAPI.COMMAND, action);
		
		if (_isSelectingShortcut) {
			Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this, getIconForAction(action));
			
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("PowerampAction", action);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			Intent shortcutIntent = new Intent();
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getStringForAction(action));
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
			setResult(RESULT_OK, shortcutIntent);
			finish();
		} else {
			startService(powerampActionIntent);
		}
	}
}
