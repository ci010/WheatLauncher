package net.wheatlauncher.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author ci010
 */
public class ValidatorAuthStateListener extends ValidatorInState
{
	private StringProperty type = new SimpleStringProperty(this, "type");
	private StringProperty onlineType = new SimpleStringProperty();

	public ValidatorAuthStateListener() {}

	public ValidatorAuthStateListener(String type)
	{
		this.type.set(type);
	}

	public StringProperty typeProperty() {return type;}

	public StringProperty onlineTypeProperty() {return onlineType;}

	public final void setType(String type)
	{
		this.type.set(type);
	}

	public final String getType(){return this.type.get();}

	public void setOnlineType(String t)
	{
		onlineType.set(t);
	}

	@Override
	protected String getContext()
	{
		return onlineType.getValue() + "." + type.getValue();
	}
}
