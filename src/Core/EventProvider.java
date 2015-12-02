package Core;

import java.util.EventListener;

public interface EventProvider {

	String getId();
	boolean isSupportedListener(EventListener listener);

}
