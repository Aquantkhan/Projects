package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.model.CommandLine;

import java.io.IOException;
import java.util.*;

/**
 * this is the initiator
 */
public class Clevis {
	/**
	 * initiator
	 * @param args a described or initiator of method main
	 */
	public static void main(String[] args){
		String htmlname, txtname;
		if(args.length>0){
			if(args.length<4){
				System.out.println("Invalid filenames. Application terminates.");
				return;
			}
			if(args[0].equals("-html") && args[2].equals("-txt")){
				htmlname=args[1];txtname=args[3];
			}else if(args[2].equals("-html") && args[0].equals("-txt")){
				htmlname=args[3];txtname=args[1];
			}else{
				System.out.println("Invalid filenames. Application terminates.");
				return;
			}
		}else{
			htmlname="";txtname="";
		}

		try {
			CommandLine.initialize(htmlname, txtname);
		}catch (IOException e){
			System.out.println("Error occurred during file creation.");
			System.out.println("Check whether file already exists.");
			return;
		}

		Scanner input=new Scanner(System.in);
		while(true){
			try{
				CommandLine.getInput(input.nextLine());
			}catch(CommandLine.QuitException e){
				break;
			}
		}
	}
}
