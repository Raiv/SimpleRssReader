package ru.raiv.simplerssreader.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList; 
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import ru.raiv.simplerssreader.R;
import ru.raiv.simplerssreader.db.DataProvider;
import ru.raiv.simplerssreader.db.RssRecord;
import ru.raiv.simplerssreader.utils.LocalIntents;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NetworkService extends IntentService {

	
	private static final String TAG = "ru.raiv.simplerssreader.service";
	private static final String RSS = "http://habrahabr.ru/rss/hubs/";

	private static final String WRONG_URL_IN_NETWORK_SERVICE = "Wrong URL in networkService";
	
	public NetworkService() {
		super("Service Thread");
		try {
			url = new URL(RSS);
		} catch (MalformedURLException e) {
			throw new RuntimeException(WRONG_URL_IN_NETWORK_SERVICE, e);
		}

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "start working on intent");
		if (intent.getAction().equals(LocalIntents.REQUEST_DATA)) {
			doRequestData();
		}

	}

	private final URL url;
	private final Intent intent = new Intent(LocalIntents.DATA_READY);

	protected void doRequestData() {

		try {

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			try {
				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());
				consumeData(in);
			} finally {
				urlConnection.disconnect();
			}
			getApplication().sendBroadcast(intent);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			Toast.makeText(getApplication(), R.string.network_error,
					Toast.LENGTH_LONG).show();
		}

	}

	private void consumeData(InputStream in)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// dbf.setValidating(true);
		dbf.setIgnoringComments(true);

		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(in);
		NodeList nl = doc.getElementsByTagName("item");
		if (nl.getLength() > 0) {
			RssRecord records[] = new RssRecord[nl.getLength()];
			for (int i = 0; i < nl.getLength(); i++) {
				Element item = (Element) nl.item(i);
				Node itemNode = item.getFirstChild();
				RssRecord itemData = new RssRecord();
				if (itemNode == null) {
					throw new IOException("Not an RSS feed!");
				}

				do {
					if (itemNode.getNodeType() == Node.ELEMENT_NODE) {

						if (itemNode.getNodeName().equals("title")) {
							itemData.setTitle(getTextContent(itemNode));
						} else if (itemNode.getNodeName().equals("description")) {
							itemData.setDescription(getTextContent(itemNode));
						} else if (itemNode.getNodeName().equals("link")) {
							itemData.setLink(getTextContent(itemNode));
						} else if (itemNode.getNodeName().equals("guid")) {
							itemData.setGuid(getTextContent(itemNode));
						} else if (itemNode.getNodeName().equals("pubDate")) {
							itemData.setTime(getTextContent(itemNode));
						}
					}

				} while ((itemNode = itemNode.getNextSibling()) != null);
				records[i]= itemData;

			}
			DataProvider.getInstance(getApplication()).refresh(records);
		}else
		{
			DataProvider.getInstance(getApplication()).refresh(null);
		}

	}
	
	private String getTextContent(Node n) throws IOException
	{
		Node text = n.getFirstChild();
		if((text!=null)&&((text.getNodeType()==Node.CDATA_SECTION_NODE)||(text.getNodeType()==Node.TEXT_NODE)))
		{
			return ((Text)text).getData();
		}else throw new IOException(WRONG_URL_IN_NETWORK_SERVICE);
	}
	
}
