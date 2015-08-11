package serialize;

import java.io.File;

public class ResourceLoader {

	private static ResourceLoader rl = new ResourceLoader();

	/*
	public static InputStream getInputStream(String s){
		return rl.getClass().getResourceAsStream("/"+s);
	}

	public static String getFilePath(String s){
		System.out.println(rl.getClass().getResource("/"+s).getPath());
		return rl.getClass().getResource("/"+s).getFile();
	}


	public static BufferedReader getFileBufferedReader(String s){
		InputStreamReader stream = new InputStreamReader(rl.getClass().getResourceAsStream("/"+s));
		return new BufferedReader(stream);
	}

	public static InputStream getFileInputStream(String s){
		InputStream stream = (rl.getClass().getResourceAsStream("/"+s));
		return stream;
	}

	public static FileOutputStream getFileOuputStream(String s){
		Path p= Paths.get(s); 

		File f = new File(p.toString());
		System.out.println(f.getPath());
		FileOutputStream out;
		try {
			out = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return out;
	} */

	public static File getFile(String s){
		/*String userDirectory = System.getProperty("user.dir");
		//System.out.println(userDirectory); 

		return new File(userDirectory+"\\"+s);*/
		if(rl.getClass().getResource("/"+s) ==null){
			System.out.println("aha! : "+rl.getClass().getResource("/"));
		}
		return new File(rl.getClass().getResource("/"+s).getPath());
	}

	public static File getFileUserDirectory(String s){
		String userDirectory = System.getProperty("user.dir");
		System.out.println("userDirectory: "+userDirectory); 

		return new File(userDirectory+"\\"+s);

	}
}
