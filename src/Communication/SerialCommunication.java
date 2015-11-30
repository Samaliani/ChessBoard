package Communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import LowLevel.BoardData;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class SerialCommunication extends BoardCommunication {

	String portName;
	SerialPort serialPort;
	
	InputStream inStream;
	OutputStream outStream;
	
	public SerialCommunication() {
	}

	protected Event getEvent() {

		if (serialPort == null)
			return null;
		
		try {
			if (inStream.available() == 0)
				return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		InputStreamReader is = new InputStreamReader(inStream);
		BufferedReader br = new BufferedReader(is);
	
		String line = readLine(br);

		int id = Integer.parseInt(line, 16);
		Event.Type eventType = Event.Type.fromInt(id);
		switch (eventType) {
		case BoardChange:
			String data = readLine(br);
			return new Event(Event.Type.BoardChange, new BoardData(data));
		case Move:
			return new Event(Event.Type.Move);
		default:
			return null;
		}
	}

	protected String readLine(BufferedReader reader) {
		try {
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public String getPortName(String portName) {
		return portName;
	}

	public void setPortName(String portName) {

		disconnect();
		this.portName = portName;
		try {
			
			connect(portName);
		} catch (IOException e) {
			// TODO
			portName = "";
			disconnect();
			e.printStackTrace();
		}
	}

	public void connect(String portName) throws IOException {
		try {
			// Obtain a CommPortIdentifier object for the port you want to open
			CommPortIdentifier portId = CommPortIdentifier
					.getPortIdentifier(portName);

			// Get the port's ownership
			SerialPort port = (SerialPort) portId.open("ChessBoard", 1000);

			// Set the parameters of the connection.
			setSerialPortParameters(port);

			// Open the input and output streams for the connection. If they
			// won't
			// open, close the port before throwing an exception.
			outStream = port.getOutputStream();
			inStream = port.getInputStream();
			
			serialPort = port; 
			
		} catch (NoSuchPortException e) {
			throw new IOException(e.getMessage());
		} catch (PortInUseException e) {
			throw new IOException(e.getMessage());
		} catch (IOException e) {
			serialPort.close();
			throw e;
		}
	}

	private void setSerialPortParameters(SerialPort port) throws IOException {
		int baudRate = 9600; // 57600bps

		try {
			// Set serial port to 57600bps-8N1..my favourite
			port.setSerialPortParams(baudRate, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		} catch (UnsupportedCommOperationException ex) {
			throw new IOException("Unsupported serial port parameter");
		}
	}

	public void disconnect() {

		if (serialPort != null) {

			outStream = null;
			inStream = null;

			serialPort.close();
			serialPort = null;
		}
	}

	public void stop()
	{
		super.stop();
		disconnect();
	}
	
}
