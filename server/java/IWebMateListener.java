package webmate;

public interface IWebMateListener {
	/**
	 * This method will be called on request from browser.
	 * @param data The data from browser (url decoded)
	 */
	public void onWebMateData(String data);
}
