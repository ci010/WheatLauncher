package net.launcher.utils;

import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * @author ci010
 */
public class NIOUtilsTest
{
	@Test
	public void writeString() throws Exception
	{
		String s = NIOUtils.readToString(
				Paths.get(
						"C:\\Users\\John\\AppData\\Roaming\\" +
								".launcher\\profiles\\1482023994710\\profile.json"));
		assertEquals(s, "{\n" +
				"    \"id\": \"1482023994710\",\n" +
				"    \"java\": \"C:\\\\Program Files\\\\Java\\\\jdk1.8.0_71\\\\jre\\\\bin\\\\java.exe\",\n" +
				"    \"memory\": 512,\n" +
				"    \"minecraft\": \"C:\\\\Users\\\\John\\\\Workspace\\\\WheatLauncher\\\\.minecraft\",\n" +
				"    \"name\": \"default\",\n" +
				"    \"resolution\": \"856x482\"\n" +
				"}");
	}

	@Test
	public void readToString() throws Exception
	{

	}

	@Test
	public void readToStream() throws Exception
	{

	}

	@Test
	public void readToBuffer() throws Exception
	{

	}

	@Test
	public void readToBuffer1() throws Exception
	{

	}

	@Test
	public void readToBytes() throws Exception
	{

	}

	@Test
	public void mapToConsumer() throws Exception
	{

	}

	@Test
	public void mapToBuffer() throws Exception
	{

	}

	@Test
	public void mapToBytes() throws Exception
	{

	}

	@Test
	public void mapToStream() throws Exception
	{

	}

	@Test
	public void mapToString() throws Exception
	{

	}

	@Test
	public void toUTF8() throws Exception
	{

	}

}