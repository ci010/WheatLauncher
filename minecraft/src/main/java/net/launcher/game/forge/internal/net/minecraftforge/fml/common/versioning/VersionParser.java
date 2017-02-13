/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.launcher.game.forge.internal.net.minecraftforge.fml.common.versioning;


/**
 * Parses version strings according to the specification here:
 * http://docs.codehaus.org/display/MAVEN/Versioning
 * and allows for comparison of versions based on that document.
 * Bounded version specifications are defined as
 * http://maven.apache.org/plugins/maven-enforcer-plugin/rules/versionRanges.html
 * <p>
 * Borrows heavily from maven version range management code
 *
 * @author cpw
 */
public class VersionParser
{

	public static ArtifactVersion parseVersionReference(String labelledRef)
	{
		if (labelledRef == null || labelledRef.equals(""))
		{
			throw new RuntimeException(String.format("Empty reference %s", labelledRef));
		}

		String[] split = labelledRef.split("@");
		if (split.length > 2)
		{
			throw new RuntimeException(String.format("Invalid versioned reference %s", labelledRef));
		}
		if (split.length == 1)
		{
			return new DefaultArtifactVersion(split[0], true);
		}
		try
		{
			return new DefaultArtifactVersion(split[0], parseRange(split[1]));
		}
		catch (InvalidVersionSpecificationException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static boolean satisfies(ArtifactVersion target, ArtifactVersion source)
	{
		return target.containsVersion(source);
	}

	public static VersionRange parseRange(String range) throws InvalidVersionSpecificationException
	{
		return VersionRange.createFromVersionSpec(range);
	}
}
