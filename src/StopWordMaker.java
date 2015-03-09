import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Hamster
 * Date: 3/7/2015
 * Time: 11:55 PM
 */
public class StopWordMaker
{
	public StopWordMaker()
	{
		
	}

	public static void main(String[] args)
	{

		try
		{
			InputStream in = new FileInputStream(args[0]);
			Scanner scanner = new Scanner(in);
			while (scanner.hasNext())
			{
				String ss = scanner.nextLine();
				ss = ss.replaceAll("[^a-zA-Z ]", "");
				System.out.println("STOP_WORDS.put(\"" + ss + "\", true);");
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
