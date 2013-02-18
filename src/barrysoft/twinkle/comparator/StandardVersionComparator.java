package barrysoft.twinkle.comparator;

import java.util.List;
import java.util.Vector;

/**
 * <p>The standard implementation of a {@link VersionComparator}.</p>
 * 
 * <p>The implementation divides possible characters in a version string 
 * in three different types:
 * <blockquote>
 * <ul>
 * <li><i>Period</i> - a period character
 * <li><i>Number</i> - a numeric digit
 * <li><i>String</i> - a letter or a whitespace
 * </ul>
 *  </blockquote>
 * When comparing two version strings, the strings' components are split.
 * A split occur if any of the following is satisfied:
 * <blockquote>
 * <ul>
 * <li>If the character is a period</li>
 * <li>If the character's type differ from the following character's type</li>
 * </ul>
 * </blockquote>
 * The resulting components are then used for comparison using the following
 * rules:
 * <blockquote>
 * <ul>
 * <li>If both are numbers they are compared numerically</li>
 * <li>If both are strings they are compared alphabetically</li>
 * <li>If component types differ, numbers win</li>
 * </ul>
 * </blockquote>
 * The comparison ends when two components are compared according to
 * the aforementioned rules and they are not equal.</p>
 * 
 * <p>If one of the version strings is longer than the other and comparison
 * couldn't determine which one represents a newer version before reaching
 * the end of the shortest version, than the longest string wins only if
 * the part in excess is a number, otherwise the shortest wins.</p>
 * 
 * <p>Some examples:
 * <pre>
 * Newer	Older
 * -----	--------
 * 2.0  	1.0
 * 1.0  	1.0pre1
 * 1.0a1	0.9
 * 1.0  	1.0 beta
 * </pre>
 * </p>
 * 
 * <p>This implementation is based on Sparkle's SUStandardVersionComparator.</p>
 * 
 * @author Daniele Rapagnani
 */

public class StandardVersionComparator implements VersionComparator 
{
	private final static StandardVersionComparator instance = 
		new StandardVersionComparator();

	private enum CharacterType
	{
		PERIOD,
		NUMBER,
		STRING
	}
	
	public static final StandardVersionComparator getInstance()
	{
		return instance;
	}
	
	private StandardVersionComparator()
	{
	}
	
	public ComparatorResult compareVersions(String version, String toVersion)
	{
		List<String> partsA = splitVersionString(version);
		List<String> partsB = splitVersionString(toVersion);
		
		String 			partA, partB;
		int				intA, intB;
		CharacterType 	typeA, typeB;
	
		int n = Math.min(partsA.size(), partsB.size());
		
		for (int i=0; i < n; i++)
		{
			partA = partsA.get(i);
			partB = partsB.get(i);
			
			typeA = getCharacterType(partA);
			typeB = getCharacterType(partB);
			
			if (typeA == typeB) 
			{
				if (typeA == CharacterType.NUMBER)
				{
					intA = Integer.parseInt(partA);
					intB = Integer.parseInt(partB);
					
					if (intA > intB)
						return ComparatorResult.VERSION_OLD;
					else if (intA < intB)
						return ComparatorResult.VERSION_NEW;
				}
				else if (typeA == CharacterType.STRING)
				{
					int result = partA.compareTo(partB);
					
					if (result != 0)
						return ComparatorResult.parseCompareResult(result);
				}
			}
			else
			{
				if (typeA != CharacterType.STRING && typeB == CharacterType.STRING)
				{
					return ComparatorResult.VERSION_OLD;
				} 
				else if (typeA == CharacterType.STRING && typeB != CharacterType.STRING)
				{
					return ComparatorResult.VERSION_NEW;
				}
				else
				{
					if (typeA == CharacterType.NUMBER)
						return ComparatorResult.VERSION_OLD;
					else
						return ComparatorResult.VERSION_NEW;
				}
			}
		}
		
		if (partsA.size() != partsB.size())
		{
			String 			missingPart;
			CharacterType 	missingType;
			
			ComparatorResult shorterResult, largerResult;
			
			if (partsA.size() > partsB.size())
			{
				missingPart = partsA.get(n);
				
				shorterResult = ComparatorResult.VERSION_NEW;
				largerResult = ComparatorResult.VERSION_OLD;
			}
			else
			{
				missingPart = partsB.get(n);
				
				shorterResult = ComparatorResult.VERSION_OLD;
				largerResult = ComparatorResult.VERSION_NEW;
			}
			
			missingType = getCharacterType(missingPart);
			
			if (missingType == CharacterType.STRING)
				return shorterResult;
			else
				return largerResult;
		}
		
		return ComparatorResult.VERSION_EQUAL;
	}
	
	protected CharacterType getCharacterType(String str)
	{
		return getCharacterType(str.charAt(0));
	}

	protected CharacterType getCharacterType(Character c)
	{
		if (c.equals('.'))
			return CharacterType.PERIOD;
		else if (Character.isDigit(c))
			return CharacterType.NUMBER;
		else if (Character.isLetter(c) || Character.isWhitespace(c))
			return CharacterType.STRING;
		
		throw new RuntimeException("Unknown character type: " + c);
	}
	
	protected List<String> splitVersionString(String version)
	{
		Vector<String> parts = new Vector<String>();
		
		if (version == null || version.isEmpty())
			return parts;
		
		CharacterType 	oldType = getCharacterType(version.charAt(0));
		CharacterType 	newType;
		
		String			curPart = Character.toString(version.charAt(0));
		Character		curChar;
		
		for (int i=1; i < version.length(); i++)
		{
			curChar = version.charAt(i);
			newType = getCharacterType(curChar);
			
			if (oldType != newType || oldType == CharacterType.PERIOD)
			{
				parts.add(new String(curPart));
				curPart = Character.toString(curChar);
			}
			else
			{
				curPart += curChar;
			}
			
			oldType = newType;
		}
		
		parts.add(new String(curPart));
		
		return parts;
	}
}
