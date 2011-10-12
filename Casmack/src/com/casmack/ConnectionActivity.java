package com.casmack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConnectionActivity extends Activity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection);
        
        final EditText user_text = (EditText) this.findViewById(R.id.user);
        final EditText domain_text = (EditText) this.findViewById(R.id.domain);
        final EditText resource_text = (EditText) this.findViewById(R.id.resource);
        final EditText password_text = (EditText) this.findViewById(R.id.password);
        Button connectionButton = (Button) this.findViewById(R.id.connection_button);
        
        connectionButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String serverHost = domain_text.getText().toString(); 
				String login = user_text.getText().toString();
				String resource = resource_text.getText().toString();
				String password = password_text.getText().toString();
				
				Intent intentService = new Intent(ConnectionActivity.this,XmppConnectionService.class);
				intentService.putExtra(XmppConnectionService.SERVER_HOST, serverHost);
				intentService.putExtra(XmppConnectionService.LOGIN, login);
				intentService.putExtra(XmppConnectionService.PASSWORD, password);
				intentService.putExtra(XmppConnectionService.RESOURCE, resource);
				startService(intentService);
			}
        });
        
    }
    
    

}
