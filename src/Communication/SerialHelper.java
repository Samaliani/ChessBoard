package Communication;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class SerialHelper {

	List<String> ports;

	public SerialHelper() {
		ports = getPortsAvailable();
	}

	public List<String> getPorts() {
		return ports;
	}

	@SuppressWarnings("unchecked")
	static public List<String> getPortsAvailable() {
		List<String> result = new ArrayList<String>();
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL)
				result.add(portIdentifier.getName());
		}
		return result;
	}
}
