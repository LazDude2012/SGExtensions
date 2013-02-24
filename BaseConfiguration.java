//------------------------------------------------------
//
//   Greg's Mod Base - Configuration
//
//------------------------------------------------------

package sgextensions;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import java.io.File;

public class BaseConfiguration extends Configuration
{

	public boolean extended = false;

	public BaseConfiguration(File file)
	{
		super(file);
	}

	boolean getBoolean(String category, String key, boolean defaultValue)
	{
		return get(category, key, defaultValue).getBoolean(defaultValue);
	}

	@Override
	public Property get(String category, String key, String defaultValue, String comment, Property.Type type)
	{
		if (!hasKey(category, key))
			extended = true;
		return super.get(category, key, defaultValue, comment, type);
	}

}
