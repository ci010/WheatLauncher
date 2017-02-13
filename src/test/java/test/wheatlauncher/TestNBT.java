package test.wheatlauncher;

//import org.junit.Test;

/**
 * @author ci010
 */
public class TestNBT
{
//	@Test
//	public void testRead() throws IOException
//	{
//		File file = new File("C:\\Users\\CIJhn\\Workspace\\Output\\Standard\\" +
//				".minecraft\\saves\\We_Are_The_Ranger_Map_MC1.9V2\\level.dat");
//		NBT read = NBT.read(file, true);
//		assert read != null;
//	}
//
//	@Test
//	public void testRead1() throws IOException
//	{
//		File file = new File("C:\\Users\\CIJhn\\Workspace\\Output\\Standard\\" +
//				".minecraft\\saves\\We_Are_The_Ranger_Map_MC1.9V2\\level.dat");
//		NBT read = NBT.read(file, true);
//		assert read != null;
//
//		NBTCompound asCompound = read.asCompound().get("Data").asCompound();
//		String t = null;
//		for (Map.Entry<String, NBT> stringNBTEntry : asCompound.entrySet())
//			System.out.println((t = stringNBTEntry.getKey()) + " " + stringNBTEntry.getValue());
//		assert asCompound.get(t) != null;
//	}
//
//	@Test
//	public void testConstruct() throws IOException
//	{
//		File file = new File("C:\\Users\\CIJhn\\Workspace\\Output\\Standard\\" +
//				".minecraft\\saves\\We_Are_The_Ranger_Map_MC1.9V2\\level.dat");
//		NBT read = NBT.read(file, true);
//		assert read != null;
//
//		NBTCompound asCompound = read.asCompound().get("Data").asCompound();
//
//		WorldInfo deserialize = WorldInfo.SERIALIZER.deserialize(asCompound, Collections.singletonMap("fileName", "We_Are_The_Ranger_Map_MC1.9V2"));
//
//		System.out.println(deserialize);
//		assert deserialize != null;
//	}
//
//	@Test
//	public void testWrite() throws IOException
//	{
//		File file = new File("C:\\Users\\CIJhn\\Desktop\\level.dat");
//		NBT read = NBT.read(file, true);
//		assert read != null;
//
//		NBTCompound asCompound = read.asCompound().get("Data").asCompound();
//
//		WorldInfo deserialize = WorldInfo.SERIALIZER.deserialize(asCompound, Collections.singletonMap("fileName", "We_Are_The_Ranger_Map_MC1.9V2"));
//
//		System.out.println(deserialize);
//		assert deserialize != null;
//
//		deserialize.setDisplayName("wtf");
//
//		WorldInfo.WRITER.writeTo(deserialize, asCompound);
//
//		assert asCompound.get("LevelName").asPrimitive().asString().equals("wtf");
//
//		NBTCompound compound = NBT.compound();
//		compound.put("Data", asCompound);
//		NBT.write(file, compound, true);
//	}

}
