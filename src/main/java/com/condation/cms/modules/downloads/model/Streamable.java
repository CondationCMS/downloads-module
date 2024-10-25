package com.condation.cms.modules.downloads.model;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author thmar
 */
public interface Streamable {

	InputStream getInputStream() throws IOException;
	
	String getFileName ();
	
	long getSize ();
}
