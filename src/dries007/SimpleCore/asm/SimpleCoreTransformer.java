package dries007.SimpleCore.asm;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.relauncher.IClassTransformer;

public class SimpleCoreTransformer  implements IClassTransformer
{
	public static HashMap<String, ClassOverride> override = new HashMap<String, ClassOverride>();
	
	public static void addClassOverride(String name, String discription)
	{
		override.put(name , new ClassOverride(discription));
	}
	
	@Override
	public byte[] transform(String name, byte[] bytes) 
	{
		if(override.containsKey(name))
		{
			if(override.get(name).isActive())
			{
				bytes = overrideBytes(name, bytes, SimpleCorePlugin.myLocation);
			}
		}
		return bytes;
	}
	
	public static byte[] overrideBytes(String name, byte[] bytes, File location)
	{
		try
		{
			ZipFile zip = new ZipFile(location);
			ZipEntry entry = zip.getEntry(name.replace('.', '/')+".class");
			if(entry == null)
				System.out.println(name+" not found in "+location.getName());
			else
			{
				InputStream zin = zip.getInputStream(entry);
				bytes = new byte[(int) entry.getSize()];
				zin.read(bytes);
				zin.close();
				System.out.println(name+" was overriden from "+location.getName());
			}
			zip.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Error overriding "+name+" from "+location.getName(), e);
		}
		return bytes;
	}
	
	public static class ClassOverride
	{
		public String description = "";
		public boolean active;
		
		public ClassOverride(String description)
		{
			this.description = description;
		}
		
		public ClassOverride()
		{
			
		}
		
		public void setState(boolean state)
		{
			this.active = state;
		}
		
		public boolean isActive()
		{
			return this.active;
		}
	}
}
