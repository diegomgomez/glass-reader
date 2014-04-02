package bcn.cursosglass.glassreader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

public class MainActivity extends Activity {

	private static final String TAG = "GlassReader";
	private static final String RSS_FEED_URL = "http://rss.nytimes.com/services/xml/rss/nyt/Europe.xml";
	
	private CardScrollView mCardScroll;
	private ArrayList<RssItem> mNews;
	private TextToSpeech mSpeech;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        new FeedReader().execute();
        
        mSpeech = new TextToSpeech(this, null);
        mCardScroll = new CardScrollView(this);
        
        mCardScroll.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				RssItem item = mNews.get(position);
				
				if (null != item && null != item.getDescription()) {
					if (mSpeech.isSpeaking()) {
						mSpeech.stop();
					}
					
					mSpeech.speak(item.getDescription(), TextToSpeech.QUEUE_FLUSH, null);
				}
			}
        	
		});
        
        Card c = new Card(this);
        c.setText("Waiting for content...");
        
        setContentView(c.toView());
    }
    
    @Override
    protected void onPause() {
    	if (mSpeech.isSpeaking()) {
    		mSpeech.stop();
    	}
    	
    	super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void onFeedDonwloaded(RssFeed feed) {
    	if (null != feed) {
    		mNews = feed.getRssItems();
    		mCardScroll.setAdapter(new NewsCardScrollAdapter());
    		mCardScroll.activate();
    		setContentView(mCardScroll);
    	}
    }
    
    private class NewsCardScrollAdapter extends CardScrollAdapter {
    	
		@Override
		public int findIdPosition(Object id) {
			return -1;
		}

		@Override
		public int findItemPosition(Object item) {
			return mNews.indexOf(item);
		}

		@Override
		public int getCount() {
			return mNews.size();
		}

		@Override
		public Object getItem(int position) {
			return mNews.get(position);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RssItem info = (RssItem) getItem(position);
			
			Card c = new Card(MainActivity.this);
			c.addImage(getRandomImage());
			
			if (null != info.getTitle()) {
				c.setText(info.getTitle());
			}
			
			if (null != info.getCategory()) {
				c.setFootnote(info.getCategory());
			}
			
			return c.toView();
		}
    	
		private int getRandomImage() {
			int id = R.drawable.one;
			int n = new Random().nextInt(10) + 1;
			
			switch (n) {
			case 1:
				id = R.drawable.one;
				break;
			case 2:
				id = R.drawable.two;
				break;
			case 3:
				id = R.drawable.three;
				break;
			case 4:
				id = R.drawable.four;
				break;
			case 5:
				id = R.drawable.five;
				break;
			case 6:
				id = R.drawable.six;
				break;
			case 7:
				id = R.drawable.seven;
				break;
			case 8:
				id = R.drawable.eight;
				break;
			case 9:
				id = R.drawable.nine;
				break;
			case 10:
				id = R.drawable.ten;
				break;
			}
			
			return id;
		}
    }
    
    private class FeedReader extends AsyncTask<Void, Void, RssFeed> {
    	
		@Override
		protected RssFeed doInBackground(Void... params) {
			try {
				URL url = new URL(RSS_FEED_URL);
				return RssReader.read(url);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(RssFeed result) {
			super.onPostExecute(result);
			onFeedDonwloaded(result);
		}
    }
}