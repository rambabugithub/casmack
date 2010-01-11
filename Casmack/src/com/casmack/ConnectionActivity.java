package com.casmack;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConnectionActivity extends Activity {
	
	static XMPPConnection m_connection;
	
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
				
				
				try {
					String serverHost = domain_text.getText().toString(); 
					String login = user_text.getText().toString();
					String resource = resource_text.getText().toString();
					String password = password_text.getText().toString();
					initConnection(serverHost, login, password, resource);
					
					Intent intent = new Intent(ConnectionActivity.this, ContactActivity.class);
					startActivity(intent);
				} catch (XMPPException e) {
					Toast.makeText(ConnectionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
        });
        
    }
    
    /**
     * Initialisation de la connexion
     * 
     */
    private void initConnection(String serverHost, String login, String password, String resource) throws XMPPException {
    	ConnectionConfiguration config = new ConnectionConfiguration(serverHost);
		m_connection = new XMPPConnection(config);
        m_connection.connect();
        m_connection.login(login, password, resource);
        Presence presence = new Presence(Presence.Type.available);
        m_connection.sendPacket(presence);
        
	}

}
