package webmate.sample;

import java.util.logging.Level;

import webmate.IWebMateListener;
import webmate.MateServer;

public class SimpleWebMateService implements IWebMateListener {

        @Override
        public void onWebMateData(String data) {
        }

        /**
         * @param args
         */
        public static void main(String[] args) {
                SimpleWebMateService service = new SimpleWebMateService();
                // enable logging
                MateServer.Log.setLevel(Level.FINE);
                // starting WebMate server
                new MateServer(service);
        }

}
