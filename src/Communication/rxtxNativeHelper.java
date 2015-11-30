package Communication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import Core.Program;

public class rxtxNativeHelper {

	private static void loadDll(String resourcePath, String libraryName) throws IOException {

		String path = System.getProperty("user.dir");
		File fileOut = new File(path + "/" + libraryName);
		if (fileOut.exists())
			return;

		InputStream inStream = Program.class.getResourceAsStream(resourcePath + libraryName);
		FileOutputStream out = FileUtils.openOutputStream(fileOut);
		IOUtils.copy(inStream, out);
		out.close();
	}

	public static void initialize() {

		String model = System.getProperty("sun.arch.data.model");
		String libName = "rxtxSerial.dll";

		String path;
		if (model.equals("32"))
			path = "/Resources/dll_32/";
		else if (model.equals("64"))
			path = "/Resources/dll_64/";
		else
			return;

		try {
			loadDll(path, libName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
