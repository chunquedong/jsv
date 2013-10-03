package chunquedong.jsv.route;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotReloader {
	private Map<String, Long> fileMap = new HashMap<String, Long>();
	private boolean needReload = false;
	private JsvClassLoader classLoader = null;
	private String classPath;
	
	public HotReloader(String classPath) {
		this.classPath = classPath;
	}
	
	public Class<?> findClass(String className) throws ClassNotFoundException {
		scan();
		if (classLoader == null) {
			return Class.forName(className);
		}
		return classLoader.loadClass(className);
	}
	
	private void scan() {
		needReload = false;
		File file = new File(classPath);
		walk(file, this);
		if (needReload) {
			System.out.println("reloading...");
			reload();
		}
	}
	
	private void reload() {
		List<String> classList = new ArrayList<String>(fileMap.size());
		for ( String s : fileMap.keySet() ) {
			String className = pathToClassName(s);
			
			//load self by system
			if (className.startsWith("chunquedong.jsv.route")) {
				continue;
			}
			classList.add(className);
		}
		
		try {
			classLoader = new JsvClassLoader(classPath, classList);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private String pathToClassName(String path) {
		int i = path.lastIndexOf(".class");
		int i0 = 0;
		if (path.startsWith(classPath)) {
			i0 = classPath.length()+1;
		}
		path = path.substring(i0, i);
		String classname = path.replace(File.separatorChar, '.');
		return classname;
	}
	
	private void onVisitFile(File f) {
		String path = f.getPath();
		if (fileMap.containsKey(path)) {
			if (!fileMap.get(path).equals(f.lastModified())) {
				needReload = true;
				fileMap.put(f.getPath(), f.lastModified());
				return;
			}
		} else {
			fileMap.put(f.getPath(), f.lastModified());
			needReload = true;
		}
	}
	
	private void walk(File dir, HotReloader fileVisiter) {
		File[] files = dir.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				walk(f, fileVisiter);
			} else if (f.getName().endsWith(".class")) {
				fileVisiter.onVisitFile(f);
			}
		}
	}
}
