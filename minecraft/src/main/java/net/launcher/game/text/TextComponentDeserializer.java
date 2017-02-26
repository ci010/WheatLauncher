package net.launcher.game.text;

import net.launcher.game.text.components.TextComponentString;
import net.launcher.utils.serial.Deserializer;
import org.to2mbn.jmccc.internal.org.json.JSONArray;
import org.to2mbn.jmccc.internal.org.json.JSONObject;

import java.util.Map;

/**
 * @author ci010
 */
public class TextComponentDeserializer implements Deserializer<TextComponent, JSONObject>
{
	@Override
	public TextComponent deserialize(JSONObject serialized, Map<Object, Object> context)
	{
		TextComponent textComponent = null;

		if (serialized.has("text")) textComponent = new TextComponentString(serialized.getString("text"));

		if (textComponent == null) textComponent = new TextComponentString("");
		if (serialized.has("extra"))
		{
			JSONArray arr = serialized.getJSONArray("extra");

			if (arr.length() <= 0) throw new IllegalArgumentException("Unexpected empty array of components");

			for (int j = 0; j < arr.length(); ++j)
				textComponent.append(deserialize(arr.getJSONObject(j)));
		}

		Deserializer<Style, JSONObject> deserializer = Style.deserializer();
		textComponent.style(deserializer.deserialize(serialized));
		return textComponent;
	}

//doesn't handle these case...

//			else if (serialized.has("translate"))
//			{
//				String s = serialized.getString("translate");
//
//				if (serialized.has("with"))
//				{
//					JSONArray jsonarray = serialized.getJSONArray("with");
//					Object[] arr = new Object[jsonarray.length()];
//
//					for (int i = 0; i < arr.length; ++i)
//					{
//						arr[i] = deserializer().deserialize(jsonarray.getJSONObject(i));
//
//						if (arr[i] instanceof TextComponentString)
//						{
//							TextComponentString textcomponentstring = (TextComponentString) arr[i];
//
//							if (textcomponentstring.getStyle().isEmpty() && textcomponentstring.getSiblings().isEmpty())
//								arr[i] = textcomponentstring.getText();
//						}
//					}
//
//					textComponent = new TextComponentTranslation(s, arr);
//				}
//				else
//					textComponent = new TextComponentTranslation(s);
//			}
//			else if (serialized.has("score"))
//			{
//				JSONObject obj = serialized.getJSONObject("score");
//
//				if (!obj.has("name") || !obj.has("objective"))
//					throw new IllegalArgumentException("A score component needs a least a name and an objective");
//				textComponent = new TextComponentScore(obj.getString("name"), obj.getString("objective"));
//				if (obj.has("value"))
//					((TextComponentScore) textComponent).setValue(obj.getString("value"));
//			}
//			else
//			{
//				if (!serialized.has("selector"))
//					throw new IllegalArgumentException("Don\'t know how to turn " + serialized + " into a Component");
//				textComponent = new TextComponentSelector(serialized.getString("selector"));
//			}

//	public TextComponent deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
//	{
//			if (p_deserialize_1_.isJsonArray())
//			{
//				JSONArray jsonarray1 = p_deserialize_1_.getAsJsonArray();
//				TextComponent TextComponent1 = null;
//
//				for (JsonElement jsonelement : jsonarray1)
//				{
//					TextComponent TextComponent2 = this.deserialize(jsonelement, jsonelement.getClass(), p_deserialize_3_);
//
//					if (TextComponent1 == null)
//					{
//						TextComponent1 = TextComponent2;
//					}
//					else
//					{
//						TextComponent1.append(TextComponent2);
//					}
//				}
//
//				return TextComponent1;
//			}
//			else
//			{
//				throw new IllegalFormatCodePointException("Don\'t know how to turn " + p_deserialize_1_ + " into a Component");
//			}
//	}
}
