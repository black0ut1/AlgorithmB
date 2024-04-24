package cz.zcu.pperncka.io.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.io.File;

/**
 * Class representing validation of existence of file
 * that is passed as command line argument.
 */
public class FileExistsValidator implements IParameterValidator {
	
	@Override
	public void validate(String name, String value) throws ParameterException {
		File file = new File(value);
		
		if (!file.exists())
			throw new ParameterException("Value of parameter " + name + " is a file that does not exist.");
		
		if (!file.isFile())
			throw new ParameterException("Value of parameter " + name + " is not a (normal) file.");
	}
}
