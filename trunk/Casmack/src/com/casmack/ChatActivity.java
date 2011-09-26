package com.casmack;

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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.casmack.model.ChatMessage;



public class ChatActivity extends Activity {

	private ChatMessageList m_discussionThread;
	private ArrayAdapter<ChatMessage> m_discussionThreadAdapter;
	private Handler m_handler;
	
	private static final String MESSAGES = "messages";
	
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
		
		
		m_discussionThread = (ChatMessageList) (savedInstanceState != null ? savedInstanceState.getParcelable(MESSAGES) : null);
		if ( m_discussionThread == null ) {
			m_discussionThread = new ChatMessageList();	
		}
		
		
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
				ChatMessage chatMessage = new ChatMessage(msg);
				m_discussionThread.getMessages().add(chatMessage);
				m_discussionThreadAdapter.notifyDataSetChanged();
			}
		});
		
		
		PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
		ConnectionActivity.m_connection.addPacketListener(new PacketListener() {
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					if (message.getBody() != null) {
						ChatMessage chatMessage = new ChatMessage(message);
						m_discussionThread.getMessages().add(chatMessage);
						
						m_handler.post(new Runnable() {
							public void run() {
								m_discussionThreadAdapter.notifyDataSetChanged();
							}
						});
					}
				}
			}, filter);
	}
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MESSAGES, m_discussionThread);
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      m_discussionThread = savedInstanceState.getParcelable(MESSAGES);
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	Log.i("gnou", intent.toString());
    }

	
	class MessageAdapter extends ArrayAdapter<ChatMessage> {
		
		Context context;
		
		MessageAdapter(Context context, int resource, ChatMessageList objects) {
			super(context, resource, objects.getMessages());
			this.context=context;
		}


		
		
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = View.inflate(context, R.layout.multi_line_list_item, null);
			if ( position < m_discussionThread.getMessages().size() ) {
				ChatMessage mess = m_discussionThread.getMessages().get(position);
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