package swoop.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Net {
    
    private static Logger logger = LoggerFactory.getLogger(Net.class);
    
    public static String uriToPath(String uri) {
        return URI.create(uri).getPath();
    }
    
    public static String uriToQuery(String uri) {
        return URI.create(uri).getQuery();
    }

    public static String ip(SocketAddress socketAddress) {
        if(socketAddress==null) {
            logger.debug("SocketAddress is <null> cannot extract IP address from it");
        }
        else if(socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetAddress = (InetSocketAddress )socketAddress;
            InetAddress address = inetAddress.getAddress();
            // a bit paranoid?
            if(address==null) {
                logger.info("SocketAddress gives a <null> address cannot extract IP address from it");
            }
            else {
                return address.getHostAddress();
            }
        }
        else {
            logger.info("SocketAddress type is not supported ({}) cannot extract IP address from it", socketAddress.getClass());
        }
        return null;
    }
}
