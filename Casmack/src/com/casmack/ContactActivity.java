package com.casmack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.util.StringUtils;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ContactActivity extends ListActivity  {
	
	private static final String TAG = "ContactActivity";
	
	private List<Account> m_discussionThread;
	private AccountAdapter m_discussionThreadAdapter;
	private Handler m_handler;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_handler = new Handler();
        
        Roster roster = ConnectionActivity.m_connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        
        m_discussionThread = new ArrayList<Account>();
        
        for (RosterEntry entry : entries) {
        	Account account = new Account();
        	account.setUser(entry.getUser());
        	Presence presence = roster.getPresence(entry.getUser());
        	account.setName(entry.getName());
        	account.setMode(presence.getMode());
        	account.setStatus(presence.getStatus());
        	Log.i(TAG, "RosterEntry:" + account);
        	m_discussionThread.add(account);
        }
        
        m_discussionThreadAdapter = new AccountAdapter(this, R.layout.account_line_list_item, m_discussionThread);
        setListAdapter(m_discussionThreadAdapter);
        getListView().setTextFilterEnabled(true);

        
        
        roster.addRosterListener(new RosterListener() {
        	
        	@Override
            public void entriesDeleted(Collection<String> addresses) {
        		System.out.println("EntriesDeleted: " + addresses.toString());
        	}
            
            @Override
            public void entriesUpdated(Collection<String> addresses) {
            	System.out.println("EntriesUpdated: " + addresses.toString());
            }
            
            @Override
            public void presenceChanged(Presence presence) {
                System.out.println("Presence changed: " + presence.getFrom() + " " + presence);
                
                if ( presence.getFrom() != null ) {
                	Account account = searchAccount(StringUtils.parseBareAddress(presence.getFrom()));
	                if ( account != null ) {
	                	
	                	if (  Presence.Type.available.equals(presence.getType()) ) {
	                		if (presence.getMode() == null ) {
	                			account.setMode(Presence.Mode.available);	
	                		} else {
	                			account.setMode(presence.getMode());
	                		}
	                		account.setStatus(presence.getStatus());	
	                		
	                	} else if (  Presence.Type.unavailable.equals(presence.getType()) ) {
	                		account.setMode(null);
	                	} 
	                	
	                	m_handler.post(new Runnable() {
							public void run() {
								m_discussionThreadAdapter.notifyDataSetChanged();
							}
						});
	                	
	                }
                }
            }
			@Override
			public void entriesAdded(Collection<String> addresses) {
				System.out.println("EntriesAdded: " + addresses.toString());
			}
        });
        
        
        // ListActivity has a ListView, which you can get with:
        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {
				Account account = m_discussionThread.get(pos);
				Log.i(TAG, "click on: "+ account);
				
				Intent intent = new Intent(ContactActivity.this, ChatActivity.class);
				intent.putExtra("user", account.getUser());
				startActivity(intent);
				
			}
        	
		});
        
        
        

    }
    
    private Account searchAccount(String user) {
    	
    	for ( Account account : m_discussionThread ) {
    		if ( account.getUser().equals(user)) {
    			return account;
    		}
    	}

    	return null;
    }
    
    
    
    class Account {
    	private String name;
		private String user;
		private String status = "";
    	private Mode mode;
    	
    	public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
    	public String getUser() {
			return user;
		}
		public void setUser(String user) {
			this.user = user;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public Mode getMode() {
			return mode;
		}
		public void setMode(Mode mode) {
			this.mode = mode;
		}
		
		@Override
		public String toString() {
			return "Account [mode=" + mode + ", name=" + name + ", status="
					+ status + ", user=" + user + "]";
		}
    }
    
    
    class AccountAdapter extends ArrayAdapter<Account> {
		
		Context context;
		
		AccountAdapter(Context context, int resource,  List<Account> objects ) {
			super(context, resource, objects);
			this.context=context;
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = View.inflate(context, R.layout.account_line_list_item, null);
			if ( position < m_discussionThread.size() ) {
				Account account = m_discussionThread.get(position);
				if ( account != null ){
					if ( account.getName() != null ) {
						TextView login=(TextView)row.findViewById(R.id.name);
						login.setText(account.getName());
					} else if ( account.getUser() != null )  {
						TextView login=(TextView)row.findViewById(R.id.name);
						login.setText(account.getUser());
					}
					
					if ( account.getStatus() != null ) {
						TextView message=(TextView)row.findViewById(R.id.message);
						message.setText(account.getStatus());
					}
					
					
					if ( account.getMode() != null ) {
						ImageView icon=(ImageView)row.findViewById(R.id.icon_presence);
						switch ( account.getMode() ) {
							
							case chat:
								icon.setImageResource(R.drawable.chat);
								break;
							
							case available:
								icon.setImageResource(R.drawable.available);
								break;
	
							case away:
								icon.setImageResource(R.drawable.away);
								break;
	
							case xa:
								icon.setImageResource(R.drawable.extended_away);
								break;
	
							case dnd:
								icon.setImageResource(R.drawable.busy);
								break;
							
						}
					}
					
				}
				
				
				
			}

			return(row);
		}
	}


}
