package webmate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MateServer extends Thread {

	// GET key do identify the request data from browser
	public static final String REQUEST_KEY = "webmatedata";
	// default port. used if none is given in constructor
	public static final int STANDARD_PORT = 3000;
	
	// modify the log-level before you create a MateServer instance to receive all messages.
	public static final Logger Log = Logger.getLogger(MateServer.class.getName());
	static {
		// warning: server doesent work
		// info: server start/stop, listener invocation
		// fine: all GET parameters
		Log.setLevel(Level.WARNING);
	}
	
	private int port;
	private IWebMateListener callback;
	private ServerSocket s = null;
	
	/**
	 * Starts a MateServer instance. Uses default port STANDARD_PORT.
	 * @param par_callback Eventlistener
	 */
	public MateServer(IWebMateListener par_callback) {
		this(MateServer.STANDARD_PORT, par_callback);
	}
	/**
	 * Starts a MateServer instance.
	 * @param par_port Port on which the MateServer should listen.
	 * @param par_callback Eventlistener
	 */
	public MateServer(int par_port, IWebMateListener par_callback) {
		port = par_port;
		callback = par_callback;
		this.start();
	}
	
	/**
	 * Starts the server.
	 */
	public void run() {
		Log.info(String.format("Starting WebMate Server on port %d..", port));
		
		try {
			s = new ServerSocket(port);
		} catch (IOException e) {
			this.interrupt();
			Log.warning(e.getMessage());
		}
		
		while (!s.isClosed()) {
			Log.info("Waiting for connection");
			
			try {
				Socket remote = s.accept();
				
				Log.info("Got connection, parsing data..");
				
				// get the streams
				BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
				PrintWriter out = new PrintWriter(remote.getOutputStream());
				
				String str;
				while (!(str = in.readLine()).equals("")) {
					// request line
					if (str.startsWith("GET")) {
						// get request part
						String str_reqURI = str.split(" ")[1];
						String str_reqParams = str_reqURI.split("\\?")[1];
						
						Map<String, String> params = getQueryMap(str_reqParams);
						for (String key: params.keySet()) {
							Log.fine(String.format("%s: %s\n", key, params.get(key)));
						}
						
						if (params.containsKey(MateServer.REQUEST_KEY)) {
							String data = params.get(MateServer.REQUEST_KEY);
							Log.info(String.format("Received Chars: %d, Data: %s", data.length(), (data.length()>100)?data.substring(0, 40) + ".........." + data.substring(data.length()-60):data)); // prevent log flooding
							callback.onWebMateData(params.get(MateServer.REQUEST_KEY));
						}
					}
				}
	
				// sending ok without content to browser
				Log.info("Parsing complete. Sending OK..");
				
				out.println("HTTP/1.0 200 OK");
				out.println("Content-Type: text/javascript");
				out.println("Server: MateServer");
				out.flush();
	
				remote.close();
			} catch (IOException e) {
				this.interrupt();
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Stops the server
	 */
	public void interrupt() {
		try {
			Log.info("Stopping WebMate Server.");
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses a http query string.
	 * @param str_query Query string with encoded '&' and '=' as found in raw request.
	 * @return Map of request parameters
	 */
	private static Map<String, String> getQueryMap(String str_query) {
		HashMap<String, String> map = new HashMap<String, String>();
		for (String entry: str_query.split("&")) {
			String[] pair = entry.split("=");
			try {
				map.put(URLDecoder.decode(pair[0], "UTF-8"), URLDecoder.decode(pair[1], "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				Log.warning(e.getMessage());
			}
		}
		return map;
	}
}
