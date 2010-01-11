package com.casmack;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;



public class ChatActivity extends Activity {

	private List<Message> m_discussionThread;
	private ArrayAdapter<Message> m_discussionThreadAdapter;
	private Handler m_handler;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        
        Intent intent = getIntent();
        CharSequence bundleUser = intent.getCharSequenceExtra("user");
        
        m_handler = new Handler();
        
        final EditText recipient = (EditText) this.findViewById(R.id.recipient);
		recipient.setText(bundleUser);
		final EditText message = (EditText) this.findViewById(R.id.embedded_text_editor);		
		ListView list = (ListView) this.findViewById(R.id.thread);
		
		m_discussionThread = new ArrayList<Message>();
		m_discussionThreadAdapter = new MessageAdapter(this, R.layout.multi_line_list_item, m_discussionThread);
		list.setAdapter(m_discussionThreadAdapter);
		
		
		Button send = (Button) this.findViewById(R.id.send_button);
		send.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String to = recipient.getText().toString();
				String text = message.getText().toString();
		
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);
				ConnectionActivity.m_connection.sendPacket(msg);
				m_discussionThread.add(msg);
				m_discussionThreadAdapter.notifyDataSetChanged();
			}
		});
		
		
		PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
		ConnectionActivity.m_connection.addPacketListener(new PacketListener() {
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					if (message.getBody() != null) {
						
						m_discussionThread.add(message);
						
						m_handler.post(new Runnable() {
							public void run() {
								m_discussionThreadAdapter.notifyDataSetChanged();
							}
						});
					}
				}
			}, filter);
	}
	
	class MessageAdapter extends ArrayAdapter<Message> {
		
		Context context;
		
		MessageAdapter(Context context, int resource, List<Message> objects) {
			super(context, resource, objects);
			this.context=context;
		}


		
		
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = View.inflate(context, R.layout.multi_line_list_item, null);
			if ( position < m_discussionThread.size() ) {
				Message mess = m_discussionThread.get(position);
				TextView message=(TextView)row.findViewById(R.id.message);
				message.setText(mess.getBody());
				
				TextView from=(TextView)row.findViewById(R.id.from);
				if ( mess.getFrom() != null ) {
					String fromName = StringUtils.parseBareAddress(mess.getFrom());
					from.setText(fromName +" :");
				} else {
					from.setText("me :");
				}
				
				ImageView icon=(ImageView)row.findViewById(R.id.icon);
				if ( position%2 == 0) {
					icon.setImageResource(R.drawable.bebe_gnu_small);
				} else {
					icon.setImageResource(R.drawable.bebe_tux_small);
				}
			}

			return(row);
		}
	}



}