package hk.edu.polyu.comp.comp2021.clevis.model;
import java.io.*;
/**
 * create file to store order
 */
public class FileCreator
{
	private final File filetxt, filehtml;
	private String htmlString = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n" +
		"\"http://www.w3.org/TR/html4/loose.dtd\"> \n" +  // where this address point for, does anyone could access in and search it with this web address?
		"<html>\n" +    //head ?
		"<style>\n" +   // part 1
		"table, th, td {\n" +
		"  border:1px solid black;\n" +  // custom a...title or a pane/box
		"}\n" +
		"</style>\n" +  // part 1 end
		"<head>\n" +    // part 2
		"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
		"<title>$title</title>\n" +
		"</head>\n" +   // part 2 end
		"<body>\n" +    // so here is the place to store command
		"<table style=\"width:50%\">\n" +  // custome smt
		"<tr> \n" +
		"\t<th> Index </th>\n" +
		"    <th> Command </th>\n" +  // coorespond to text in class "fileWriter"
		"</tr>\n" +
		"<!--put_here-->\n" +      // for update
		"</table>\n" +
		"</body>\n" +           // body end
		"</html>";

	private int index = 1;
	/**
	 * @param htmlname the name of html file
	 * @param txtname the name of txt file
	 * as its name, bascially for creating files
	 * @throws IOException when file(s) cannot be created
	 */
	FileCreator(String htmlname, String txtname) throws IOException// as its name, bascially for creating a html file
	{
		filetxt = new File(txtname);
		filehtml = new File(htmlname);
		if (!filetxt.createNewFile() || !filehtml.createNewFile())  // check whether there already existed a file with same name
		{
			throw new IOException();
		}
		FileWriter base = new FileWriter(filehtml.getName());
		base.write(htmlString);
		base.close();
		System.out.println("File " + filetxt.getName() + " successfully created!");
		System.out.println("File " + filehtml.getName() + " successfully created!");

	}

	/**
	 * @param text the texts will be written into file
	 */
	public final void fileWrite(String text)  // text in here !!
	{
		if(filetxt.exists() && filehtml.exists())
		{
			try
			{
				FileWriter txtWriter = new FileWriter(filetxt.getName(),true);
				txtWriter.write(text+"\n");
				txtWriter.close();
				//System.out.println("Successfully wrote to the .txt file.");
				FileWriter htmlWriter = new FileWriter(filehtml.getName());
				String htmlText = "<tr>\n" +
						"\t<td>" + index +"</td>\n" +      // with index, for trace the command
						"    <td>" + text + "</td>\n" +    // update file
						"</tr>\n" +
						"<!--put_here-->";    // for new update, good design
				index++;
				htmlString = htmlString.replace("<!--put_here-->", htmlText);
				//System.out.println(htmlString);
				htmlWriter.write(htmlString);
				htmlWriter.close();
				//System.out.println("Successfully wrote to the .html file.");
			}
			catch (IOException e)
			{
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("File does not exist!");
		}
	}
}
